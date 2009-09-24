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

package org.jopendocument.dom;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.jopendocument.util.CollectionMap;
import org.jopendocument.util.CopyUtils;
import org.jopendocument.util.ExceptionUtils;
import org.jopendocument.util.JDOMUtils;
import org.jopendocument.util.ProductInfo;
import org.jopendocument.util.cc.ITransformer;

/**
 * An XML document containing all of an office document, see section 2.1 of OpenDocument 1.1.
 * 
 * @author Sylvain CUAZ 24 nov. 2004
 */
public class ODSingleXMLDocument extends ODXMLDocument implements Cloneable, ODDocument
{

    final static Set<String> DONT_PREFIX;
    static
    {
        DONT_PREFIX = new HashSet<String> ();
        // don't touch to user fields and variables
        // we want them to be the same across the document
        DONT_PREFIX.add ( "user-field-decl" );
        DONT_PREFIX.add ( "user-field-get" );
        DONT_PREFIX.add ( "variable-get" );
        DONT_PREFIX.add ( "variable-decl" );
        DONT_PREFIX.add ( "variable-set" );
    }

    // Voir le TODO du ctor
    // public static OOSingleXMLDocument createEmpty() {
    // }

    /**
     * Create a document from a collection of subdocuments.
     * 
     * @param content the content.
     * @param style the styles, can be <code>null</code>.
     * @return the merged document.
     */
    public static ODSingleXMLDocument createFromDocument ( final Document content, final Document style )
    {
        return createFromDocument ( content, style, null );
    }

    public static ODSingleXMLDocument createFromDocument ( final Document content, final Document style, final Document settings )
    {
        return createFromDocument ( content, style, settings, null, new ODPackage () );
    }

    static ODSingleXMLDocument createFromDocument ( final Document content, final Document style, final Document settings, final Document meta, final ODPackage files )
    {
        final Element root = content.getRootElement ();
        // see section 2.1.1 first meta, then settings, then the rest
        prependToRoot ( settings, root );
        prependToRoot ( meta, root );
        final ODSingleXMLDocument single = new ODSingleXMLDocument ( content, files );
        if ( single.getChild ( "body" ) == null )
        {
            throw new IllegalArgumentException ( "no body in " + single );
        }
        // signal that the xml is a complete document (was document-content)
        root.setName ( "document" );
        root.setAttribute ( "mimetype", files.getMimeType (), single.getNS ().getOFFICE () );
        if ( style != null )
        {
            // section 2.1 : Styles used in the document content and automatic styles used in the
            // styles themselves.
            // more precisely in section 2.1.1 : office:document-styles contains style, master
            // style, auto style, font decls ; the last two being also in content.xml but are *not*
            // related : eg P1 of styles.xml is *not* the P1 of content.xml
            try
            {
                single.mergeAllStyles ( new ODXMLDocument ( style ), true );
            }
            catch ( final JDOMException e )
            {
                throw new IllegalArgumentException ( "style is not valid", e );
            }
        }
        return single;
    }

    private static void prependToRoot ( final Document settings, final Element root )
    {
        if ( settings != null )
        {
            final Element officeSettings = (Element)settings.getRootElement ().getChildren ().get ( 0 );
            root.addContent ( 0, (Element)officeSettings.clone () );
        }
    }

    /**
     * Create a document from a file.
     * 
     * @param f an OpenDocument package file.
     * @return the merged file.
     * @throws JDOMException if the file is not a valid OpenDocument file.
     * @throws IOException if the file can't be read.
     * @see #createFromDocument(Document, Document)
     */
    public static ODSingleXMLDocument createFromFile ( final File f ) throws JDOMException, IOException
    {
        // this loads all linked files
        return new ODPackage ( f ).toSingle ();
    }

    /**
     * fix bug when a SingleXMLDoc is used to create a document (for example with P2 and 1_P2), and
     * then create another instance s2 with the previous document and add a second file (also with
     * P2 and 1_P2) => s2 will contain P2, 1_P2, 1_P2, 1_1_P2.
     */
    private static final String COUNT = "SingleXMLDocument_count";

    private static Element getCountElem ( final ODXMLDocument doc )
    {
        try
        {
            // section 3.3 says "applications should also preserve any additional content found
            // inside the <office:meta> element", but the strict relaxNG has no corresponding
            // provision, so use user-defined
            final Element meta = doc.getChild ( "meta", true );
            return (Element)doc.getXPath ( "./meta:user-defined[@meta:name='" + COUNT + "']" ).selectSingleNode ( meta );
        }
        catch ( final JDOMException e )
        {
            // static path, cannot happen
            throw new IllegalStateException ( e );
        }
    }

    private static void setCount ( final ODXMLDocument doc, final int count )
    {
        final Namespace metaNS = doc.getNS ().getNS ( "meta" );
        Element countElem = getCountElem ( doc );
        if ( countElem == null )
        {
            countElem = new Element ( "user-defined", metaNS );
            countElem.setAttribute ( "name", COUNT, metaNS );
            // user-defined are the last
            doc.getChild ( "meta", true ).addContent ( countElem );
        }
        countElem.setAttribute ( "value-type", "float", metaNS );
        countElem.setText ( count + "" );
    }

    private static boolean hasCount ( final ODXMLDocument doc )
    {
        return getCountElem ( doc ) != null;
    }

    private static int getCount ( final ODXMLDocument doc )
    {
        return Float.valueOf ( getCountElem ( doc ).getText () ).intValue ();
    }

    /** Le nombre de fichiers concat */
    private int numero;

    /** Les styles présent dans ce document */
    private final Set<String> stylesNames;

    /** Les styles de liste présent dans ce document */
    private final Set<String> listStylesNames;

    /** Les fichiers référencés par ce document */
    private final ODPackage pkg;

    // the element between each page
    private Element pageBreak;

    public ODSingleXMLDocument ( final Document content )
    {
        this ( content, new ODPackage () );
    }

    /**
     * A new single document. NOTE: this document will put himself in <code>pkg</code>, replacing
     * any previous content.
     * 
     * @param content the XML.
     * @param pkg the package this document belongs to.
     */
    private ODSingleXMLDocument ( final Document content, final ODPackage pkg )
    {
        super ( content );

        // inited in getPageBreak()
        this.pageBreak = null;

        this.pkg = pkg;
        this.pkg.rmFile ( "styles.xml" );
        this.pkg.rmFile ( "settings.xml" );
        this.pkg.putFile ( "content.xml", this, "text/xml" );

        // set the generator
        // creates if necessary meta at the right position
        this.getChild ( "meta", true );
        final Properties props = ProductInfo.getInstance ().getProps ();
        final String generator;
        if ( props == null )
        {
            generator = this.getClass ().getName ();
        }
        else
        {
            generator = props.getProperty ( "NAME" ) + "/" + props.getProperty ( "VERSION" );
        }
        this.pkg.getMeta ().setGenerator ( generator );

        if ( hasCount ( this ) )
        {
            this.setNumero ( getCount ( this ) );
        }
        else
        {
            // if not hasCount(), it's not us that created content
            // so there should not be any 1_
            this.setNumero ( 0 );
        }

        this.stylesNames = new HashSet<String> ( 64 );
        this.listStylesNames = new HashSet<String> ( 16 );

        // little trick to find the common styles names (not to be prefixed so they remain
        // consistent across the added documents)
        final Element styles = this.getChild ( "styles" );
        if ( styles != null )
        {
            // create a second document with our styles to collect names
            final Element root = this.getDocument ().getRootElement ();
            final Document clonedDoc = new Document ( new Element ( root.getName (), root.getNamespace () ) );
            clonedDoc.getRootElement ().addContent ( styles.detach () );
            try
            {
                this.mergeStyles ( new ODXMLDocument ( clonedDoc ) );
            }
            catch ( final JDOMException e )
            {
                throw new IllegalArgumentException ( "can't find common styles names." );
            }
            // reattach our styles
            styles.detach ();
            this.setChild ( styles );
        }
    }

    ODSingleXMLDocument ( final ODSingleXMLDocument doc, final ODPackage p )
    {
        super ( doc );
        this.setNumero ( doc.numero );
        this.stylesNames = new HashSet<String> ( doc.stylesNames );
        this.listStylesNames = new HashSet<String> ( doc.listStylesNames );
        this.pkg = p;
    }

    @Override
    public ODSingleXMLDocument clone ()
    {
        final ODPackage copy = new ODPackage ( this.pkg );
        return (ODSingleXMLDocument)copy.getContent ();
    }

    private void setNumero ( final int numero )
    {
        this.numero = numero;
        setCount ( this, this.numero );
    }

    public ODPackage getPackage ()
    {
        return this.pkg;
    }

    /**
     * Append a document.
     * 
     * @param doc the document to add.
     */
    public synchronized void add ( final ODSingleXMLDocument doc )
    {
        // ajoute un saut de page entre chaque document
        this.add ( doc, true );
    }

    /**
     * Append a document.
     * 
     * @param doc the document to add, <code>null</code> means no-op.
     * @param pageBreak whether a page break should be inserted before <code>doc</code>.
     */
    public synchronized void add ( final ODSingleXMLDocument doc, final boolean pageBreak )
    {
        if ( doc != null && pageBreak )
        {
            // only add a page break, if a page was really added
            this.getBody ().addContent ( this.getPageBreak () );
        }
        this.add ( null, 0, doc );
    }

    public synchronized void replace ( final Element elem, final ODSingleXMLDocument doc )
    {
        final Element parent = elem.getParentElement ();
        this.add ( parent, parent.indexOf ( elem ), doc );
        elem.detach ();
    }

    public synchronized void add ( final Element where, final int index, final ODSingleXMLDocument doc )
    {
        if ( doc == null )
        {
            return;
        }
        if ( !this.getVersion ().equals ( doc.getVersion () ) )
        {
            throw new IllegalArgumentException ( "version mismatch" );
        }

        this.setNumero ( this.numero + 1 );
        try
        {
            this.mergeEmbedded ( doc );
            this.mergeSettings ( doc );
            this.mergeAllStyles ( doc, false );
            this.mergeBody ( where, index, doc );
        }
        catch ( final JDOMException exn )
        {
            throw new IllegalArgumentException ( "XML error", exn );
        }
    }

    /**
     * Merge the four elements of style.
     * 
     * @param doc the xml document to merge.
     * @param sameDoc whether <code>doc</code> is the same OpenDocument than this, eg
     *        <code>true</code> when merging content.xml and styles.xml.
     * @throws JDOMException if an error occurs.
     */
    private void mergeAllStyles ( final ODXMLDocument doc, final boolean sameDoc ) throws JDOMException
    {
        // no reference
        this.mergeFontDecls ( doc );
        // section 14.1
        // § Parent Style only refer to other common styles
        // § Next Style cannot refer to an autostyle (only available in common styles)
        // § List Style can refer to an autostyle
        // § Master Page Name cannot (auto master pages does not exist)
        // § Data Style Name (for cells) can
        // but since the UI for common styles doesn't allow to customize List Style
        // and there is no common styles for tables : office:styles doesn't reference any automatic
        // styles
        this.mergeStyles ( doc );
        // on the contrary autostyles do refer to other autostyles :
        // choosing "activate bullets" will create an automatic paragraph style:style
        // referencing an automatic text:list-style.
        this.mergeAutoStyles ( doc, !sameDoc );
        // section 14.4
        // § Page Layout can refer to an autostyle
        // § Next Style Name refer to another masterPage
        this.mergeMasterStyles ( doc, !sameDoc );
    }

    private void mergeEmbedded ( final ODSingleXMLDocument doc )
    {
        // since we are adding another document our existing thumbnail is obsolete
        this.pkg.rmFile ( "Thumbnails/thumbnail.png" );
        // copy the files
        final ODPackage opkg = CopyUtils.copy ( doc.pkg );
        for ( final String name : opkg.getEntries () )
        {
            final ODPackageEntry e = opkg.getEntry ( name );
            if ( !ODPackage.isStandardFile ( e.getName () ) )
            {
                this.pkg.putFile ( this.prefix ( e.getName () ), e.getData (), e.getType () );
            }
        }
    }

    private void mergeSettings ( final ODSingleXMLDocument doc ) throws JDOMException
    {
        this.addIfNotPresent ( doc, "./office:settings", 0 );
    }

    /**
     * Fusionne les office:font-decls/style:font-decl. On ne préfixe jamais, on ajoute seulement si
     * l'attribut style:name est différent.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @throws JDOMException
     */
    private void mergeFontDecls ( final ODXMLDocument doc ) throws JDOMException
    {
        if ( this.getNS ().getVersion ().equals ( OOUtils.VERSION_1 ) )
        {
            this.mergeUnique ( doc, "font-decls", "style:font-decl" );
        }
        else
        {
            this.mergeUnique ( doc, "font-face-decls", "style:font-face" );
        }
    }

    // merge everything under office:styles
    private void mergeStyles ( final ODXMLDocument doc ) throws JDOMException
    {
        // les default-style (notamment tab-stop-distance)
        this.mergeUnique ( doc, "styles", "style:default-style", "style:family", NOP_ElementTransformer );
        // les styles
        this.stylesNames.addAll ( this.mergeUnique ( doc, "styles", "style:style" ) );
        // on ajoute outline-style si non présent
        this.addStylesIfNotPresent ( doc, "outline-style" );
        // les list-style
        this.listStylesNames.addAll ( this.mergeUnique ( doc, "styles", "text:list-style" ) );
        // les *notes-configuration
        this.addStylesIfNotPresent ( doc, "footnotes-configuration" );
        this.addStylesIfNotPresent ( doc, "endnotes-configuration" );
    }

    /**
     * Fusionne les office:automatic-styles, on préfixe tout.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @param ref whether to prefix hrefs.
     * @throws JDOMException
     */
    private void mergeAutoStyles ( final ODXMLDocument doc, final boolean ref ) throws JDOMException
    {
        final List<Element> addedStyles = this.prefixAndAddAutoStyles ( doc );
        for ( final Element addedStyle : addedStyles )
        {
            this.prefix ( addedStyle, ref );
        }
    }

    /**
     * Fusionne les office:master-styles. On ne préfixe jamais, on ajoute seulement si l'attribut
     * style:name est différent.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @param ref whether to prefix hrefs.
     * @throws JDOMException if an error occurs.
     */
    private void mergeMasterStyles ( final ODXMLDocument doc, final boolean ref ) throws JDOMException
    {
        // est référencé dans les styles avec "style:master-page-name"
        this.mergeUnique ( doc, "master-styles", "style:master-page", ref ? this.prefixTransf : this.prefixTransfNoRef );
    }

    /**
     * Fusionne les corps.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @throws JDOMException
     */
    private void mergeBody ( final Element where, final int index, final ODSingleXMLDocument doc ) throws JDOMException
    {
        this.add ( where, index, doc, this.getBodyPath (), new ElementTransformer () {
            public Element transform ( final Element elem ) throws JDOMException
            {
                // ATTN n'ajoute pas sequence-decls
                if ( elem.getName ().equals ( "sequence-decls" ) )
                {
                    return null;
                }

                if ( elem.getName ().equals ( "user-field-decls" ) )
                {
                    // user fields are global to a document, they do not vary across it.
                    // hence they are initialized at declaration
                    // we should assure that there's no 2 declaration with the same name
                    detachDuplicate ( elem );
                }

                if ( elem.getName ().equals ( "variable-decls" ) )
                {
                    // variables are not initialized at declaration
                    // we should still assure that there's no 2 declaration with the same name
                    detachDuplicate ( elem );
                }

                // par défaut
                return ODSingleXMLDocument.this.prefixTransf.transform ( elem );
            }
        } );
    }

    /**
     * Detach the children of elem whose names already exist in the body.
     * 
     * @param elem the elem to be trimmed.
     * @throws JDOMException if an error occurs.
     */
    protected final void detachDuplicate ( final Element elem ) throws JDOMException
    {
        final String singularName = elem.getName ().substring ( 0, elem.getName ().length () - 1 );
        final List thisNames = getXPath ( "./text:" + singularName + "s/text:" + singularName + "/@text:name" ).selectNodes ( getChild ( "body" ) );
        CollectionUtils.transform ( thisNames, new Transformer () {
            public Object transform ( final Object obj )
            {
                return ( (Attribute)obj ).getValue ();
            }
        } );

        final Iterator iter = elem.getChildren ().iterator ();
        while ( iter.hasNext () )
        {
            final Element decl = (Element)iter.next ();
            if ( thisNames.contains ( decl.getAttributeValue ( "name", getNS ().getTEXT () ) ) )
            {
                // on retire les déjà existant
                iter.remove ();
            }
        }
    }

    // *** Utils

    // returns the xpath to the body
    private final String getBodyPath ()
    {
        return this.getNS ().getVersion ().equals ( OOUtils.VERSION_1 ) ? "./office:body" : "./office:body/office:text";
    }

    public final Element getBody ()
    {
        try
        {
            return this.getDescendant ( this.getBodyPath () );
        }
        catch ( final JDOMException e )
        {
            throw new IllegalStateException ( "no body", e );
        }
    }

    /**
     * Verify that styles referenced by this document are indeed defined. NOTE this method is not
     * perfect : not all problems are detected.
     * 
     * @return <code>null</code> if no problem has been found, else a String describing it.
     */
    public final String checkStyles ()
    {
        try
        {
            final CollectionMap<String, String> stylesNames = this.getStylesNames ();
            // text:style-name : text:p, text:span
            // table:style-name : table:table, table:row, table:column, table:cell
            // draw:style-name : draw:text-box
            // style:data-style-name : <style:style style:family="table-cell">
            // TODO check by family
            final Set<String> names = new HashSet<String> ( stylesNames.values () );
            final Iterator attrs = this.getXPath ( ".//@text:style-name | .//@table:style-name | .//@draw:style-name | .//@style:data-style-name | .//@style:list-style-name" ).selectNodes ( this.getDocument () ).iterator ();
            while ( attrs.hasNext () )
            {
                final Attribute attr = (Attribute)attrs.next ();
                if ( !names.contains ( attr.getValue () ) )
                {
                    return "unknown style referenced by " + attr.getName () + " in " + JDOMUtils.output ( attr.getParent () );
                }
            }
            // TODO check other references like page-*-name (§3 of #prefix())
        }
        catch ( final IllegalStateException e )
        {
            return ExceptionUtils.getStackTrace ( e );
        }
        catch ( final JDOMException e )
        {
            return ExceptionUtils.getStackTrace ( e );
        }
        return null;
    }

    private final CollectionMap<String, String> getStylesNames () throws IllegalStateException
    {
        // section 14.1 § Style Name : style:family + style:name is unique
        final CollectionMap<String, String> res = new CollectionMap<String, String> ( HashSet.class );

        final List<Element> nodes = new ArrayList<Element> ();
        nodes.add ( this.getChild ( "styles" ) );
        nodes.add ( this.getChild ( "automatic-styles" ) );

        try
        {
            {
                final Iterator iter = this.getXPath ( "./style:style/@style:name" ).selectNodes ( nodes ).iterator ();
                while ( iter.hasNext () )
                {
                    final Attribute attr = (Attribute)iter.next ();
                    final String styleName = attr.getValue ();
                    final String family = attr.getParent ().getAttributeValue ( "family", attr.getNamespace () );
                    if ( res.getNonNull ( family ).contains ( styleName ) )
                    {
                        throw new IllegalStateException ( "duplicate style in " + family + " :  " + styleName );
                    }
                    res.put ( family, styleName );
                }
            }
            {
                final List<String> dataStyles = Arrays.asList ( "number-style", "currency-style", "percentage-style", "date-style", "time-style", "boolean-style", "text-style" );
                final String xpDataStyles = org.jopendocument.util.CollectionUtils.join ( dataStyles, " | ", new ITransformer<String, String> () {
                    @Override
                    public String transformChecked ( final String input )
                    {
                        return "./number:" + input;
                    }
                } );
                final Iterator listIter = this.getXPath ( "./text:list-style | " + xpDataStyles ).selectNodes ( nodes ).iterator ();
                while ( listIter.hasNext () )
                {
                    final Element elem = (Element)listIter.next ();
                    res.put ( elem.getQualifiedName (), elem.getAttributeValue ( "name", getNS ().getSTYLE () ) );
                }
            }
        }
        catch ( final JDOMException e )
        {
            throw new IllegalStateException ( e );
        }
        return res;
    }

    /**
     * Préfixe les attributs en ayant besoin.
     * 
     * @param elem l'élément à préfixer.
     * @param references whether to prefix hrefs.
     * @throws JDOMException if an error occurs.
     */
    void prefix ( final Element elem, final boolean references ) throws JDOMException
    {
        Iterator attrs = this.getXPath ( ".//@text:style-name | .//@table:style-name | .//@draw:style-name | .//@style:data-style-name" ).selectNodes ( elem ).iterator ();
        while ( attrs.hasNext () )
        {
            final Attribute attr = (Attribute)attrs.next ();
            // text:list/@text:style-name references text:list-style
            if ( !this.listStylesNames.contains ( attr.getValue () ) && !this.stylesNames.contains ( attr.getValue () ) )
            {
                attr.setValue ( this.prefix ( attr.getValue () ) );
            }
        }

        attrs = this.getXPath ( ".//@style:list-style-name" ).selectNodes ( elem ).iterator ();
        while ( attrs.hasNext () )
        {
            final Attribute attr = (Attribute)attrs.next ();
            if ( !this.listStylesNames.contains ( attr.getValue () ) )
            {
                attr.setValue ( this.prefix ( attr.getValue () ) );
            }
        }

        attrs = this.getXPath ( ".//@style:page-master-name | .//@style:page-layout-name | .//@text:name | .//@form:name | .//@form:property-name" ).selectNodes ( elem ).iterator ();
        while ( attrs.hasNext () )
        {
            final Attribute attr = (Attribute)attrs.next ();
            final String parentName = attr.getParent ().getName ();
            if ( !DONT_PREFIX.contains ( parentName ) )
            {
                attr.setValue ( this.prefix ( attr.getValue () ) );
            }
        }

        // prefix references
        if ( references )
        {
            attrs = this.getXPath ( ".//@xlink:href[../@xlink:show='embed']" ).selectNodes ( elem ).iterator ();
            while ( attrs.hasNext () )
            {
                final Attribute attr = (Attribute)attrs.next ();
                final String prefixedPath = this.prefixPath ( attr.getValue () );
                if ( prefixedPath != null )
                {
                    attr.setValue ( prefixedPath );
                }
            }
        }
    }

    /**
     * Prefix a path.
     * 
     * @param href a path inside the pkg, eg "./Object 1/content.xml".
     * @return the prefixed path or <code>null</code> if href is external, eg "./3_Object
     *         1/content.xml".
     */
    private String prefixPath ( final String href )
    {
        if ( this.getVersion ().equals ( OOUtils.OOo ) )
        {
            // in OOo 1.x inPKG is denoted by a #
            final boolean sharp = href.startsWith ( "#" );
            if ( sharp )
            {
                // eg #Pictures/100000000000006C000000ABCC02339E.png
                return "#" + this.prefix ( href.substring ( 1 ) );
            }
            else
            {
                // eg ../../../../Program%20Files/OpenOffice.org1.1.5/share/gallery/apples.gif
                return null;
            }
        }
        else
        {
            URI uri;
            try
            {
                uri = new URI ( href );
            }
            catch ( final URISyntaxException e )
            {
                // OO doesn't escape characters for files
                uri = null;
            }
            // section 17.5
            final boolean inPKGFile = uri == null || uri.getScheme () == null && uri.getAuthority () == null && uri.getPath ().charAt ( 0 ) != '/';
            if ( inPKGFile )
            {
                final String dotSlash = "./";
                if ( href.startsWith ( dotSlash ) )
                {
                    return dotSlash + this.prefix ( href.substring ( dotSlash.length () ) );
                }
                else
                {
                    return this.prefix ( href );
                }
            }
            else
            {
                return null;
            }
        }
    }

    private String prefix ( final String value )
    {
        return "_" + this.numero + value;
    }

    private final ElementTransformer prefixTransf = new ElementTransformer () {
        public Element transform ( final Element elem ) throws JDOMException
        {
            ODSingleXMLDocument.this.prefix ( elem, true );
            return elem;
        }
    };

    private final ElementTransformer prefixTransfNoRef = new ElementTransformer () {
        public Element transform ( final Element elem ) throws JDOMException
        {
            ODSingleXMLDocument.this.prefix ( elem, false );
            return elem;
        }
    };

    /**
     * Ajoute dans ce document seulement les éléments de doc correspondant au XPath spécifié et dont
     * la valeur de l'attribut style:name n'existe pas déjà.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @param topElem eg "office:font-decls".
     * @param elemToMerge les éléments à fusionner (par rapport à topElem), eg "style:font-decl".
     * @return les noms des éléments ajoutés.
     * @throws JDOMException
     * @see #mergeUnique(ODSingleXMLDocument, String, String, ElementTransformer)
     */
    private List<String> mergeUnique ( final ODXMLDocument doc, final String topElem, final String elemToMerge ) throws JDOMException
    {
        return this.mergeUnique ( doc, topElem, elemToMerge, NOP_ElementTransformer );
    }

    /**
     * Ajoute dans ce document seulement les éléments de doc correspondant au XPath spécifié et dont
     * la valeur de l'attribut style:name n'existe pas déjà. En conséquence n'ajoute que les
     * éléments possédant un attribut style:name.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @param topElem eg "office:font-decls".
     * @param elemToMerge les éléments à fusionner (par rapport à topElem), eg "style:font-decl".
     * @param addTransf la transformation à appliquer avant d'ajouter.
     * @return les noms des éléments ajoutés.
     * @throws JDOMException
     */
    private List<String> mergeUnique ( final ODXMLDocument doc, final String topElem, final String elemToMerge, final ElementTransformer addTransf ) throws JDOMException
    {
        return this.mergeUnique ( doc, topElem, elemToMerge, "style:name", addTransf );
    }

    private List<String> mergeUnique ( final ODXMLDocument doc, final String topElem, final String elemToMerge, final String attrFQName, final ElementTransformer addTransf ) throws JDOMException
    {
        final List<String> added = new ArrayList<String> ();
        final Element thisParent = this.getChild ( topElem, true );

        final XPath xp = this.getXPath ( "./" + elemToMerge + "/@" + attrFQName );

        // les styles de ce document
        final List thisElemNames = xp.selectNodes ( thisParent );
        // on transforme la liste d'attributs en liste de String
        CollectionUtils.transform ( thisElemNames, new Transformer () {
            public Object transform ( final Object obj )
            {
                return ( (Attribute)obj ).getValue ();
            }
        } );

        // pour chaque style de l'autre document
        final Iterator otherElemNames = xp.selectNodes ( doc.getChild ( topElem ) ).iterator ();
        while ( otherElemNames.hasNext () )
        {
            final Attribute attr = (Attribute)otherElemNames.next ();
            // on l'ajoute si non déjà dedans
            if ( !thisElemNames.contains ( attr.getValue () ) )
            {
                thisParent.addContent ( addTransf.transform ( (Element)attr.getParent ().clone () ) );
                added.add ( attr.getValue () );
            }
        }

        return added;
    }

    /**
     * Ajoute l'élément elemName de doc, s'il n'est pas dans ce document.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @param elemName l'élément à ajouter, eg "outline-style".
     * @throws JDOMException if elemName is not valid.
     */
    private void addStylesIfNotPresent ( final ODXMLDocument doc, final String elemName ) throws JDOMException
    {
        this.addIfNotPresent ( doc, "./office:styles/text:" + elemName );
    }

    /**
     * Prefixe les fils de auto-styles possédant un attribut "name" avant de les ajouter.
     * 
     * @param doc le document à fusionner avec celui-ci.
     * @return les élément ayant été ajoutés.
     * @throws JDOMException
     */
    private List<Element> prefixAndAddAutoStyles ( final ODXMLDocument doc ) throws JDOMException
    {
        final List<Element> result = new ArrayList<Element> ( 128 );
        final List otherNames = this.getXPath ( "./*/@style:name" ).selectNodes ( doc.getChild ( "automatic-styles" ) );
        final Iterator iter = otherNames.iterator ();
        while ( iter.hasNext () )
        {
            final Attribute attr = (Attribute)iter.next ();
            final Element parent = (Element)attr.getParent ().clone ();
            parent.setAttribute ( "name", this.prefix ( attr.getValue () ), this.getNS ().getSTYLE () );
            this.getChild ( "automatic-styles" ).addContent ( parent );
            result.add ( parent );
        }
        return result;
    }

    /**
     * Saves this OO document to a file.
     * 
     * @param f the file where this document will be saved, without extension, eg "dir/myfile".
     * @return the actual file where it has been saved (with extension), eg "dir/myfile.odt".
     * @throws IOException if an error occurs.
     */
    public File saveAs ( final File f ) throws IOException
    {
        return this.pkg.saveAs ( f );
    }

    private Element getPageBreak ()
    {
        if ( this.pageBreak == null )
        {
            final String styleName = "PageBreak";
            try
            {
                final XPath xp = this.getXPath ( "./style:style[@style:name='" + styleName + "']" );
                final Element styles = this.getChild ( "styles", true );
                if ( xp.selectSingleNode ( styles ) == null )
                {
                    final Element pageBreakStyle = new Element ( "style", this.getNS ().getSTYLE () );
                    pageBreakStyle.setAttribute ( "name", styleName, this.getNS ().getSTYLE () );
                    pageBreakStyle.setAttribute ( "family", "paragraph", this.getNS ().getSTYLE () );
                    pageBreakStyle.setContent ( getPProps ().setAttribute ( "break-after", "page", this.getNS ().getNS ( "fo" ) ) );
                    // <element name="office:styles"> <interleave>...
                    // so just append the new style
                    styles.addContent ( pageBreakStyle );
                }
            }
            catch ( final JDOMException e )
            {
                // static path, shouldn't happen
                throw new IllegalStateException ( "pb while searching for " + styleName, e );
            }
            this.pageBreak = new Element ( "p", this.getNS ().getTEXT () ).setAttribute ( "style-name", styleName, this.getNS ().getTEXT () );
        }
        return (Element)this.pageBreak.clone ();
    }

    private final Element getPProps ()
    {
        return new Element ( this.getVersion ().equals ( OOUtils.OD ) ? "paragraph-properties" : "properties", this.getNS ().getSTYLE () );
    }
}