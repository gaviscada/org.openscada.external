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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Encapsulate all namespaces for a particular version of xml.
 * 
 * @author ILM Informatique 26 juil. 2004
 */
public final class NS {

    private static final String OFFICE_1 = "http://openoffice.org/2000/office";
    private static final String STYLE_1 = "http://openoffice.org/2000/style";
    private static final String TEXT_1 = "http://openoffice.org/2000/text";
    private static final String NUMBER_1 = "http://openoffice.org/2000/datastyle";
    private static final String TABLE_1 = "http://openoffice.org/2000/table";
    private static final String DRAW_1 = "http://openoffice.org/2000/drawing";
    private static final String FO_1 = "http://www.w3.org/1999/XSL/Format";
    private static final NS OO;

    private static final String OFFICE_2 = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
    private static final String STYLE_2 = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";
    private static final String TEXT_2 = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
    private static final String NUMBER_2 = "urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0";
    private static final String TABLE_2 = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";
    private static final String DRAW_2 = "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0";
    private static final String FO_2 = "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0";
    private static final NS OD;

    private static final Map<String, NS> instances = new HashMap<String, NS>();
    private static final Set<String> mandatoryNS;
    static {
        mandatoryNS = new HashSet<String>();

        mandatoryNS.add("office");
        mandatoryNS.add("style");
        mandatoryNS.add("text");
        mandatoryNS.add("table");

        OO = create(OOUtils.OOo, "http://openoffice.org/2001/manifest", OFFICE_1, STYLE_1, TEXT_1, TABLE_1);
        OO.put("number", NUMBER_1);
        OO.put("draw", DRAW_1);
        OO.put("number", NUMBER_1);
        OO.put("fo", FO_1);
        OO.put("form", "http://openoffice.org/2000/form");
        OO.put("xlink", "http://www.w3.org/1999/xlink");
        OO.put("script", "http://openoffice.org/2000/script");
        OO.put("svg", "http://www.w3.org/2000/svg");
        OO.put("meta", "http://openoffice.org/2000/meta");
        OO.put("dc", "http://purl.org/dc/elements/1.1/");

        OD = create(OOUtils.OD, "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0", OFFICE_2, STYLE_2, TEXT_2, TABLE_2);
        OD.put("number", NUMBER_2);
        OD.put("draw", DRAW_2);
        OD.put("number", NUMBER_2);
        OD.put("fo", FO_2);
        OD.put("form", "urn:oasis:names:tc:opendocument:xmlns:form:1.0");
        OD.put("xlink", "http://www.w3.org/1999/xlink");
        OD.put("script", "urn:oasis:names:tc:opendocument:xmlns:script:1.0");
        OD.put("svg", "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0");
        OD.put("meta", "urn:oasis:names:tc:opendocument:xmlns:meta:1.0");
        OD.put("dc", "http://purl.org/dc/elements/1.1/");
    }

    private static NS create(String name, String manifest, String office, String style, String text, String table) {
        final Map<String, Namespace> m = new HashMap<String, Namespace>();
        m.put("office", Namespace.getNamespace("office", office));
        m.put("style", Namespace.getNamespace("style", style));
        m.put("text", Namespace.getNamespace("text", text));
        m.put("table", Namespace.getNamespace("table", table));
        return new NS(name, m, Namespace.getNamespace("manifest", manifest));
    }

    private final String name;
    private final Map<String, Namespace> nss;
    private final Namespace manifest;

    private NS(String name, Map<String, Namespace> namespaces, Namespace manifest) {
        this.name = name;
        this.nss = new HashMap<String, Namespace>(namespaces);
        this.manifest = manifest;
        if (!this.nss.keySet().containsAll(mandatoryNS))
            throw new IllegalArgumentException(namespaces + " must contains the following mandatory namespaces: " + mandatoryNS);

        instances.put(this.getVersion(), this);
    }

    public void put(String prefix, String uri) {
        this.nss.put(prefix, Namespace.getNamespace(prefix, uri));
    }

    public final Namespace getNS(String prefix) {
        if (!this.nss.containsKey(prefix))
            throw new IllegalStateException("unknown " + prefix + " : " + this.nss.keySet());
        return this.nss.get(prefix);
    }

    public final String getVersion() {
        return this.name;
    }

    public final Namespace getManifest() {
        return this.manifest;
    }

    public Namespace getOFFICE() {
        return this.getNS("office");
    }

    public Namespace getSTYLE() {
        return this.getNS("style");
    }

    public Namespace getTEXT() {
        return this.getNS("text");
    }

    public Namespace getTABLE() {
        return this.getNS("table");
    }

    public Namespace getMETA() {
        return this.getNS("meta");
    }

    public Namespace[] getALL() {
        return this.nss.values().toArray(new Namespace[this.nss.size()]);
    }

    // *** static public

    public static final NS get(String name) {
        if (instances.containsKey(name))
            return instances.get(name);
        else
            throw new IllegalArgumentException("unknown XML version : " + name + " ; current valid values are " + instances.keySet());
    }

    /**
     * Namespaces for OpenOffice.org 1.x.
     * 
     * @return namespaces for OO.o 1.
     */
    public static final NS getOOo() {
        return OO;
    }

    /**
     * Namespaces for OpenDocument/OpenOffice.org 2.x.
     * 
     * @return namespaces for OpenDocument.
     */
    public static final NS getOD() {
        return OD;
    }

    /**
     * Find the NS to which belongs the passed namespace.
     * 
     * @param ns the namespace, eg office=http://openoffice.org/2000/office.
     * @return the matching NS, eg NS.getOOo(), or <code>null</code> if none is found.
     */
    public static final NS getParent(Namespace ns) {
        final Iterator<NS> iter = instances.values().iterator();
        while (iter.hasNext()) {
            final NS n = iter.next();
            if (n.getNS(ns.getPrefix()).equals(ns))
                return n;
        }
        return null;
    }

    public static final String getVersion(Document doc) {
        return getVersion(doc.getRootElement());
    }

    /**
     * Infer the version of an OO xml element from its namespace.
     * 
     * @param elem the element to be tested, eg &lt;text:line-break/&gt;.
     * @return the name of the version.
     * @throws IllegalArgumentException if the namespace is unknown.
     * @see OOUtils#VERSION_1
     * @see OOUtils#VERSION_2
     */
    public static final String getVersion(Element elem) {
        final NS parent = getParent(elem.getNamespace());
        if (parent == null)
            throw new IllegalArgumentException(elem + " is not an OpenOffice element.");
        return parent.getVersion();
    }

}
