/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 jOpenDocument, by ILM Informatique. All rights reserved.
 * 
 * The contents of this file are subject to the terms of the GNU
 * General Public License Version 3 only ("GPL").  
 * You may not use this file except in compliance with the License. 
 * You can obtain a copy of the License at http://www.gnu.org/licenses/gpl-3.0.html
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each file.
 * 
 */

package org.jopendocument.dom.spreadsheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.table.TableModel;

import org.apache.commons.collections.map.LinkedMap;
import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jopendocument.dom.ContentType;
import org.jopendocument.dom.ContentTypeVersioned;
import org.jopendocument.dom.NS;
import org.jopendocument.dom.ODDocument;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.OOUtils;

/**
 * A calc document.
 * 
 * @author Sylvain
 */
public class SpreadSheet implements ODDocument
{

    public static SpreadSheet createFromFile ( final File f ) throws IOException
    {
        return create ( new ODPackage ( f ) );
    }

    public static SpreadSheet create ( final ODPackage fd )
    {
        return new SpreadSheet ( fd.getDocument ( "content.xml" ), fd.getDocument ( "styles.xml" ), fd );
    }

    public static SpreadSheet createEmpty ( final TableModel t ) throws IOException
    {
        return createEmpty ( t, NS.getOD () );
    }

    public static SpreadSheet createEmpty ( final TableModel t, final NS ns ) throws IOException
    {
        final Document doc = new Document ( new Element ( "document", ns.getOFFICE () ) );
        // OpenDocument use relaxNG
        if ( ns.getVersion ().equals ( OOUtils.OOo ) )
        {
            doc.setDocType ( new DocType ( "office:document", "-//OpenOffice.org//DTD OfficeDocument 1.0//EN", "office.dtd" ) );
        }
        final ContentTypeVersioned ct = ContentTypeVersioned.fromType ( ContentType.SPREADSHEET, ns.getVersion () );
        if ( ct.getVersion ().equals ( OOUtils.VERSION_1 ) )
        {
            doc.getRootElement ().setAttribute ( "class", ct.getShortName (), ns.getOFFICE () );
        }
        // don't forget that, otherwise OO crash
        doc.getRootElement ().addContent ( new Element ( "automatic-styles", ns.getOFFICE () ) );

        final Element topBody = new Element ( "body", ns.getOFFICE () );
        final Element body;
        if ( ct.getVersion ().equals ( OOUtils.VERSION_2 ) )
        {
            body = new Element ( ct.getShortName (), ns.getOFFICE () );
            topBody.addContent ( body );
        }
        else
        {
            body = topBody;
        }
        doc.getRootElement ().addContent ( topBody );
        final Element sheetElem = Sheet.createEmpty ( ns );
        body.addContent ( sheetElem );

        final SpreadSheet spreadSheet = new SpreadSheet ( doc, null );
        spreadSheet.getSheet ( 0 ).merge ( t, 0, 0, true );
        return spreadSheet;
    }

    /**
     * Export the passed data to file.
     * 
     * @param t the data to export.
     * @param f where to export, if the extension is missing (or wrong) the correct one will be
     *        added, eg "dir/data".
     * @param ns the version of XML.
     * @return the saved file, eg "dir/data.ods".
     * @throws IOException if the file can't be saved.
     */
    public static File export ( final TableModel t, final File f, final NS ns ) throws IOException
    {
        return SpreadSheet.createEmpty ( t, ns ).saveAs ( f );
    }

    private final ODPackage originalFile;

    private final LinkedMap sheets;

    public SpreadSheet ( final Document doc, final Document styles )
    {
        this ( doc, styles, null );
    }

    private SpreadSheet ( final Document doc, final Document styles, final ODPackage orig )
    {
        if ( orig != null )
        {
            // ATTN OK because this is our private instance (see createFromFile())
            this.originalFile = orig;
        }
        else
        {
            this.originalFile = new ODPackage ();
        }
        this.originalFile.putFile ( "content.xml", doc );
        if ( styles != null )
        {
            this.originalFile.putFile ( "styles.xml", styles );
        }

        this.sheets = new LinkedMap ();
        final Iterator iter = this.getBody ().getChildren ( "table", this.getNS ().getTABLE () ).iterator ();
        while ( iter.hasNext () )
        {
            final Element t = (Element)iter.next ();
            this.sheets.put ( t.getAttributeValue ( "name", this.getNS ().getTABLE () ), t );
        }
    }

    final Document getContent ()
    {
        return this.getPackage ().getContent ().getDocument ();
    }

    private final String getVersion ()
    {
        return this.getPackage ().getVersion ();
    }

    private Element getBody ()
    {
        final Element body = this.getContent ().getRootElement ().getChild ( "body", this.getNS ().getOFFICE () );
        if ( this.getVersion ().equals ( OOUtils.VERSION_1 ) )
        {
            return body;
        }
        else
        {
            return body.getChild ( "spreadsheet", this.getNS ().getOFFICE () );
        }
    }

    protected final String[] resolve ( final String namedRange )
    {
        final Attribute attr;
        try
        {
            final XPath path = this.getXPath ( "./table:named-expressions/table:named-range[@table:name='" + namedRange + "']/@table:base-cell-address" );
            attr = (Attribute)path.selectSingleNode ( this.getBody () );
        }
        catch ( final JDOMException e )
        {
            throw new IllegalStateException ();
        }
        if ( attr != null )
        {
            // $facture.$H$34
            final String ref = attr.getValue ().replaceAll ( "\\$", "" );
            final int dotIndex = ref.indexOf ( '.' );
            final String sheetName = ref.substring ( 0, dotIndex );
            final String cellName = ref.substring ( dotIndex + 1 );
            return new String[] { sheetName, cellName };
        }
        else
        {
            return null;
        }
    }

    public Object getValueAt ( final String ref )
    {
        // OO doesn't allow cellRef as range names
        if ( Sheet.isCellRef ( ref ) )
        {
            throw new IllegalArgumentException ( ref + " is a cell range, you must use it on a sheet" );
        }
        else
        {
            final String[] r = this.resolve ( ref );
            return this.getSheet ( r[0] ).getValueAt ( r[1] );
        }
    }

    public final NS getNS ()
    {
        return NS.get ( this.getVersion () );
    }

    public XPath getXPath ( final String p ) throws JDOMException
    {
        return OOUtils.getXPath ( p, this.getVersion () );
    }

    public int getSheetCount ()
    {
        return this.sheets.size ();
    }

    public Sheet getSheet ( final int i )
    {
        return this.getSheet ( (String)this.sheets.get ( i ) );
    }

    public Sheet getSheet ( final String name )
    {
        Object res = this.sheets.get ( name );
        if ( res instanceof Element )
        {
            res = new Sheet ( this, (Element)res );
            this.sheets.put ( name, res );
        }
        return (Sheet)res;
    }

    // *** Files

    public File saveAs ( final File file ) throws FileNotFoundException, IOException
    {
        this.getPackage ().setFile ( file );
        return this.getPackage ().save ();
    }

    public final ODPackage getPackage ()
    {
        return this.originalFile;
    }

}