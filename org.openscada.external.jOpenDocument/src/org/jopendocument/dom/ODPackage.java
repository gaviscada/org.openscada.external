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

import org.jopendocument.util.CopyUtils;
import org.jopendocument.util.FileUtils;
import org.jopendocument.util.StreamUtils;
import org.jopendocument.util.StringInputStream;
import org.jopendocument.util.Zip;
import org.jopendocument.util.ZippedFilesProcessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * An OpenDocument package, ie a zip containing XML documents and their associated files.
 * 
 * @author ILM Informatique 2 août 2004
 */
public class ODPackage {

    // use raw format, otherwise spaces are added to every spreadsheet cell
    private static final XMLOutputter OUTPUTTER = new XMLOutputter(Format.getRawFormat());

    private static final Set<String> subdocNames;
    static {
        subdocNames = new HashSet<String>();
        // section 2.1 of OpenDocument-v1.1-os.odt
        subdocNames.add("content.xml");
        subdocNames.add("styles.xml");
        subdocNames.add("meta.xml");
        subdocNames.add("settings.xml");
    }

    /**
     * Whether the passed entry is specific to a package.
     * 
     * @param name a entry name, eg "mimetype"
     * @return <code>true</code> if <code>name</code> is a standard file, eg <code>true</code>.
     */
    public static final boolean isStandardFile(final String name) {
        return name.equals("mimetype") || subdocNames.contains(name) || name.startsWith("Thumbnails") || name.startsWith("META-INF") || name.startsWith("Configurations");
    }

    private final Map<String, ODPackageEntry> files;
    private ContentTypeVersioned type;
    private File file;

    public ODPackage() {
        this.files = new HashMap<String, ODPackageEntry>();
        this.type = null;
        this.file = null;
    }

    public ODPackage(InputStream ins) throws IOException {
        this();

        final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        new ZippedFilesProcessor() {
            @Override
            protected void processEntry(ZipEntry entry, InputStream in) throws IOException {
                final String name = entry.getName();
                final Object res;
                if (subdocNames.contains(name)) {
                    try {
                        res = new ODXMLDocument(OOUtils.getBuilder().build(in));
                    } catch (JDOMException e) {
                        // always correct
                        throw new IllegalStateException("parse error", e);
                    }
                } else {
                    out.reset();
                    StreamUtils.copy(in, out);
                    res = out.toByteArray();
                }
                // we don't know yet the types
                putFile(name, res, null, entry.getMethod() == ZipEntry.DEFLATED);
            }
        }.process(ins);
        // fill in the missing types from the manifest, if any
        final ODPackageEntry me = this.files.remove(Manifest.ENTRY_NAME);
        if (me != null) {
            final byte[] m = (byte[]) me.getData();
            try {
                final Map<String, String> manifestEntries = Manifest.parse(new ByteArrayInputStream(m));
                for (final Map.Entry<String, String> e : manifestEntries.entrySet()) {
                    final String path = e.getKey();
                    final ODPackageEntry entry = this.files.get(path);
                    // eg directory
                    if (entry == null)
                        this.files.put(path, new ODPackageEntry(path, e.getValue(), null));
                    else
                        entry.setType(e.getValue());
                }
            } catch (JDOMException e) {
                throw new IllegalArgumentException("bad manifest " + new String(m), e);
            }
        }
    }

    public ODPackage(File f) throws IOException {
        this(new BufferedInputStream(new FileInputStream(f), 512 * 1024));
        this.file = f;
    }

    public ODPackage(ODPackage o) {
        this();
        // ATTN this works because, all files are read upfront
        for (final String name : o.getEntries()) {
            final ODPackageEntry entry = o.getEntry(name);
            final Object data = entry.getData();
            final Object myData;
            if (data instanceof byte[])
                // assume byte[] are immutable
                myData = data;
            else if (data instanceof ODSingleXMLDocument) {
                myData = new ODSingleXMLDocument((ODSingleXMLDocument) data, this);
            } else {
                myData = CopyUtils.copy(data);
            }
            this.putFile(name, myData, entry.getType(), entry.isCompressed());
        }
        this.file = o.file;
    }

    public final File getFile() {
        return this.file;
    }

    public final void setFile(File f) {
        this.file = this.addExt(f);
    }

    private final File addExt(File f) {
        final String ext = '.' + this.getContentType().getExtension();
        if (!f.getName().endsWith(ext))
            f = new File(f.getParentFile(), f.getName() + ext);
        return f;
    }

    /**
     * The version of this package, <code>null</code> if it cannot be found (eg this package is
     * empty, or contains no xml).
     * 
     * @return the version of this package, can be <code>null</code>.
     */
    public final XMLVersion getVersion() {
        final ODXMLDocument content = this.getContent();
        if (content == null)
            return null;
        else
            return content.getVersion();
    }

    /**
     * The type of this package, <code>null</code> if it cannot be found (eg this package is empty).
     * 
     * @return the type of this package, can be <code>null</code>.
     */
    public final ContentTypeVersioned getContentType() {
        if (this.type == null) {
            if (this.files.containsKey("mimetype"))
                this.type = ContentTypeVersioned.fromMime(new String(this.getBinaryFile("mimetype")));
            else if (this.getVersion().equals(XMLVersion.OOo)) {
                final Element contentRoot = this.getContent().getDocument().getRootElement();
                final String docClass = contentRoot.getAttributeValue("class", contentRoot.getNamespace("office"));
                this.type = ContentTypeVersioned.fromClass(docClass);
            } else if (this.getVersion().equals(XMLVersion.OD)) {
                final Element bodyChild = (Element) this.getContent().getChild("body").getChildren().get(0);
                this.type = ContentTypeVersioned.fromBody(bodyChild.getName());
            }
        }
        return this.type;
    }

    public final String getMimeType() {
        return this.getContentType().getMimeType();
    }

    /**
     * Call {@link OOXML#isValid(Document)} on each XML subdocuments.
     * 
     * @return all problems indexed by subdocuments names, ie empty if all ok.
     */
    public final Map<String, String> validateSubDocuments() {
        final OOXML ooxml = OOXML.get(getVersion());
        final Map<String, String> res = new HashMap<String, String>();
        for (final String s : subdocNames) {
            if (this.getEntries().contains(s)) {
                final String valid = ooxml.isValid(this.getDocument(s));
                if (valid != null)
                    res.put(s, valid);
            }
        }
        return res;
    }

    // *** getter on files

    public final Set<String> getEntries() {
        return this.files.keySet();
    }

    public final ODPackageEntry getEntry(String entry) {
        return this.files.get(entry);
    }

    protected final Object getData(String entry) {
        final ODPackageEntry e = this.getEntry(entry);
        return e == null ? null : e.getData();
    }

    public final byte[] getBinaryFile(String entry) {
        return (byte[]) this.getData(entry);
    }

    public final ODXMLDocument getXMLFile(String xmlEntry) {
        return (ODXMLDocument) this.getData(xmlEntry);
    }

    public final ODXMLDocument getXMLFile(final Document doc) {
        for (final String s : subdocNames) {
            final ODXMLDocument xmlFile = getXMLFile(s);
            if (xmlFile != null && xmlFile.getDocument() == doc) {
                return xmlFile;
            }
        }
        return null;
    }

    public final ODXMLDocument getContent() {
        return this.getXMLFile("content.xml");
    }

    public final ODMeta getMeta() {
        final ODMeta meta;
        if (this.getEntries().contains("meta.xml"))
            meta = ODMeta.create(this.getXMLFile("meta.xml"));
        else
            meta = ODMeta.create(this.getContent());
        return meta;
    }

    /**
     * Return an XML document.
     * 
     * @param xmlEntry the filename, eg "styles.xml".
     * @return the matching document, or <code>null</code> if there's none.
     * @throws JDOMException if error about the XML.
     * @throws IOException if an error occurs while reading the file.
     */
    public Document getDocument(String xmlEntry) {
        final ODXMLDocument xml = this.getXMLFile(xmlEntry);
        return xml == null ? null : xml.getDocument();
    }

    /**
     * Find the passed automatic or common style referenced from the content.
     * 
     * @param family the family, eg "paragraph".
     * @param name the name, eg "P1".
     * @return the corresponding XML element.
     */
    public final Element getStyle(final String family, final String name) {
        return this.getStyle(this.getContent().getDocument(), family, name);
    }

    /**
     * Find the passed automatic or common style. NOTE : <code>referent</code> is needed because
     * there can exist automatic styles with the same name in both "content.xml" and "styles.xml".
     * 
     * @param referent the document referencing the style.
     * @param family the family, eg "paragraph".
     * @param name the name, eg "P1".
     * @return the corresponding XML element.
     * @see ODXMLDocument#getStyle(String, String)
     */
    public final Element getStyle(final Document referent, final String family, final String name) {
        // avoid searching in content then styles if it cannot be found
        if (name == null)
            return null;

        String refSubDoc = null;
        final String[] stylesContainer = new String[] { "content.xml", "styles.xml" };
        for (final String subDoc : stylesContainer)
            if (this.getDocument(subDoc) == referent)
                refSubDoc = subDoc;
        if (refSubDoc == null)
            throw new IllegalArgumentException("neither in content nor styles : " + referent);

        Element res = this.getXMLFile(refSubDoc).getStyle(family, name);
        // if it isn't in content.xml it might be in styles.xml
        if (res == null && refSubDoc.equals(stylesContainer[0]) && this.getXMLFile(stylesContainer[1]) != null)
            res = this.getXMLFile(stylesContainer[1]).getStyle(family, name);
        return res;
    }

    // *** setter

    public void putFile(String entry, Object data) {
        this.putFile(entry, data, null);
    }

    public void putFile(final String entry, final Object data, final String mediaType) {
        this.putFile(entry, data, mediaType, true);
    }

    public void putFile(final String entry, final Object data, final String mediaType, final boolean compress) {
        if (entry == null)
            throw new NullPointerException("null name");
        final Object myData;
        if (subdocNames.contains(entry)) {
            final ODXMLDocument oodoc;
            if (data instanceof Document)
                oodoc = new ODXMLDocument((Document) data);
            else
                oodoc = (ODXMLDocument) data;
            // si le package est vide n'importe quelle version convient
            if (this.getVersion() != null && !oodoc.getVersion().equals(this.getVersion()))
                throw new IllegalArgumentException("version mismatch " + this.getVersion() + " != " + oodoc);
            myData = oodoc;
        } else if (data != null && !(data instanceof byte[]))
            throw new IllegalArgumentException("should be byte[] for " + entry + ": " + data);
        else
            myData = data;
        final String inferredType = mediaType != null ? mediaType : FileUtils.findMimeType(entry);
        this.files.put(entry, new ODPackageEntry(entry, inferredType, myData, compress));
    }

    public void rmFile(String entry) {
        this.files.remove(entry);
    }

    /**
     * Transform this to use a {@link ODSingleXMLDocument}. Ie after this method, only "content.xml"
     * remains and it's an instance of ODSingleXMLDocument.
     * 
     * @return the created ODSingleXMLDocument.
     */
    public ODSingleXMLDocument toSingle() {
        if (!this.isSingle()) {
            // this removes xml files used by OOSingleXMLDocument
            final Document content = removeAndGetDoc("content.xml");
            final Document styles = removeAndGetDoc("styles.xml");
            final Document settings = removeAndGetDoc("settings.xml");
            final Document meta = removeAndGetDoc("meta.xml");

            return ODSingleXMLDocument.createFromDocument(content, styles, settings, meta, this);
        } else
            return (ODSingleXMLDocument) this.getContent();
    }

    public final boolean isSingle() {
        return this.getContent() instanceof ODSingleXMLDocument;
    }

    private Document removeAndGetDoc(String name) {
        if (!this.files.containsKey(name))
            return null;
        final ODXMLDocument xmlDoc = (ODXMLDocument) this.files.remove(name).getData();
        return xmlDoc == null ? null : xmlDoc.getDocument();
    }

    // *** save

    public final void save(OutputStream out) throws IOException {
        final Zip z = new Zip(out);

        // magic number, see section 17.4
        z.zipNonCompressed("mimetype", this.getMimeType().getBytes("UTF8"));

        final Manifest manifest = new Manifest(this.getVersion(), this.getMimeType());
        for (final String name : this.files.keySet()) {
            // added at the end
            if (name.equals("mimetype") || name.equals(Manifest.ENTRY_NAME))
                continue;

            final ODPackageEntry entry = this.files.get(name);
            final Object val = entry.getData();
            if (val != null) {
                if (val instanceof ODXMLDocument) {
                    final OutputStream o = z.createEntry(name);
                    OUTPUTTER.output(((ODXMLDocument) val).getDocument(), o);
                    o.close();
                } else {
                    z.zip(name, (byte[]) val, entry.isCompressed());
                }
            }
            final String mediaType = entry.getType();
            manifest.addEntry(name, mediaType == null ? "" : mediaType);
        }

        z.zip(Manifest.ENTRY_NAME, new StringInputStream(manifest.asString()));
        z.close();
    }

    /**
     * Save the content of this package to our file, overwriting it if it exists.
     * 
     * @return the saved file.
     * @throws IOException if an error occurs while saving.
     */
    public File save() throws IOException {
        return this.saveAs(this.getFile());
    }

    public File saveAs(final File fNoExt) throws IOException {
        final File f = this.addExt(fNoExt);
        if (f.getParentFile() != null)
            f.getParentFile().mkdirs();
        // ATTN at this point, we must have read all the content of this file
        // otherwise we could save to File.createTempFile("oofd", null).deleteOnExit();
        final FileOutputStream out = new FileOutputStream(f);
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out, 512 * 1024);
        try {
            this.save(bufferedOutputStream);
        } finally {
            bufferedOutputStream.close();
        }
        return f;
    }
}
