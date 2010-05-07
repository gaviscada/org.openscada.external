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

import static java.util.Collections.singleton;
import org.jopendocument.dom.spreadsheet.CellStyle;
import org.jopendocument.dom.spreadsheet.ColumnStyle;
import org.jopendocument.dom.spreadsheet.RowStyle;
import org.jopendocument.dom.spreadsheet.TableStyle;
import org.jopendocument.dom.text.ParagraphStyle;
import org.jopendocument.util.JDOMUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A style:style, see section 14.1. Maintains a map of family to classes.
 * 
 * @author Sylvain
 */
public class StyleStyle extends ODNode {

    private static final Map<XMLVersion, Map<String, StyleDesc<?>>> family2Desc;
    private static final Map<XMLVersion, Map<Class<? extends StyleStyle>, StyleDesc<?>>> class2Desc;
    private static boolean descsLoaded = false;
    static {
        family2Desc = new HashMap<XMLVersion, Map<String, StyleDesc<?>>>();
        class2Desc = new HashMap<XMLVersion, Map<Class<? extends StyleStyle>, StyleDesc<?>>>();
        for (final XMLVersion v : XMLVersion.values()) {
            family2Desc.put(v, new HashMap<String, StyleDesc<?>>());
            class2Desc.put(v, new HashMap<Class<? extends StyleStyle>, StyleDesc<?>>());
        }
    }

    // lazy initialization to avoid circular dependency (i.e. ClassLoader loads PStyle.DESC which
    // loads StyleStyle which needs PStyle.DESC)
    private static void loadDescs() {
        if (!descsLoaded) {
            registerAllVersions(CellStyle.DESC);
            registerAllVersions(RowStyle.DESC);
            registerAllVersions(ColumnStyle.DESC);
            registerAllVersions(TableStyle.DESC);
            registerAllVersions(ParagraphStyle.DESC);
            registerAllVersions(GraphicStyle.DESC);
            descsLoaded = true;
        }
    }

    // until now styles have remained constant through versions
    private static void registerAllVersions(StyleDesc<? extends StyleStyle> desc) {
        for (final XMLVersion v : XMLVersion.values()) {
            if (v == desc.getVersion())
                register(desc);
            else
                register(StyleDesc.copy(desc, v));
        }
    }

    public static void register(StyleDesc<? extends StyleStyle> desc) {
        if (family2Desc.get(desc.getVersion()).put(desc.getFamily(), desc) != null)
            throw new IllegalStateException(desc.getFamily() + " duplicate");
        if (class2Desc.get(desc.getVersion()).put(desc.getStyleClass(), desc) != null)
            throw new IllegalStateException(desc.getStyleClass() + " duplicate");
    }

    /**
     * Create the most specific instance for the passed element.
     * 
     * @param pkg the package where the style is defined.
     * @param styleElem a style:style XML element.
     * @return the most specific instance, e.g. a new ColumnStyle.
     */
    public static StyleStyle warp(final ODPackage pkg, final Element styleElem) {
        final StyleStyle generic = new StyleStyle(pkg, styleElem);
        loadDescs();
        final Map<String, StyleDesc<?>> map = family2Desc.get(pkg.getVersion());
        if (map.containsKey(generic.getFamily())) {
            final StyleDesc<?> styleClass = map.get(generic.getFamily());
            return styleClass.create(pkg, styleElem);
        } else
            return generic;
    }

    static <S extends StyleStyle> StyleDesc<S> getStyleDesc(Class<S> clazz, final XMLVersion version) {
        return getStyleDesc(clazz, version, true);
    }

    @SuppressWarnings("unchecked")
    private static <S extends StyleStyle> StyleDesc<S> getStyleDesc(Class<S> clazz, final XMLVersion version, final boolean mustExist) {
        loadDescs();
        final Map<Class<? extends StyleStyle>, StyleDesc<?>> map = class2Desc.get(version);
        if (map.containsKey(clazz))
            return (StyleDesc<S>) map.get(clazz);
        else if (mustExist)
            throw new IllegalArgumentException("unregistered " + clazz + " for version " + version);
        else
            return null;
    }

    private final StyleDesc<?> desc;
    private final ODPackage pkg;
    private final String name, family;
    private final XMLVersion ns;

    public StyleStyle(final ODPackage pkg, final Element styleElem) {
        super(styleElem);
        this.pkg = pkg;
        this.name = this.getElement().getAttributeValue("name", this.getSTYLE());
        this.family = this.getElement().getAttributeValue("family", this.getSTYLE());
        this.ns = this.pkg.getVersion();
        this.desc = getStyleDesc(this.getClass(), this.ns, false);
        if (this.desc != null && !this.desc.getFamily().equals(this.getFamily()))
            throw new IllegalArgumentException("expected " + this.desc.getFamily() + " but got " + this.getFamily() + " for " + styleElem);
        // assert that styleElem is in pkg (and thus have the same version)
        assert this.pkg.getXMLFile(getElement().getDocument()) != null;
        assert this.pkg.getVersion() == XMLVersion.getVersion(getElement());
    }

    protected final Namespace getSTYLE() {
        return this.getElement().getNamespace("style");
    }

    public final XMLVersion getNS() {
        return this.ns;
    }

    public final String getName() {
        return this.name;
    }

    public final String getFamily() {
        return this.family;
    }

    public final Element getFormattingProperties() {
        return this.getFormattingProperties(this.getFamily());
    }

    /**
     * Create if necessary and return the wanted properties.
     * 
     * @param family type of properties, eg "text".
     * @return the matching properties, eg &lt;text-properties&gt;.
     */
    public final Element getFormattingProperties(final String family) {
        final String childName;
        if (this.getNS() == XMLVersion.OD)
            childName = family + "-properties";
        else
            childName = "properties";
        Element res = this.getElement().getChild(childName, this.getSTYLE());
        if (res == null) {
            res = new Element(childName, this.getSTYLE());
            this.getElement().addContent(res);
        }
        return res;
    }

    /**
     * Return the elements referring to this style in the passed document.
     * 
     * @param doc an XML document.
     * @param wantSingle whether elements that affect only themselves should be included.
     * @param wantMulti whether elements that affect multiple others should be included.
     * @return the list of elements referring to this.
     */
    private final List<Element> getReferences(final Document doc, final boolean wantSingle, boolean wantMulti) {
        return this.desc.getReferences(doc, getName(), wantSingle, wantMulti);
    }

    /**
     * Return the elements referring to this style.
     * 
     * @return the list of elements referring to this.
     */
    public final List<Element> getReferences() {
        return this.getReferences(true, true);
    }

    private final List<Element> getReferences(final boolean wantSingle, final boolean wantMulti) {
        final Document myDoc = this.getElement().getDocument();
        final Document content = this.pkg.getContent().getDocument();
        // my document can always refer to us
        final List<Element> res = this.getReferences(myDoc, wantSingle, wantMulti);
        // but only common styles can be referenced from the content
        if (myDoc != content && !this.isAutomatic())
            res.addAll(this.getReferences(content, wantSingle, wantMulti));
        return res;
    }

    private final boolean isAutomatic() {
        return this.getElement().getParentElement().getQualifiedName().equals("office:automatic-styles");
    }

    public final boolean isReferencedAtMostOnce() {
        // i.e. no multi-references and at most one single reference
        return this.getReferences(false, true).size() == 0 && this.getReferences(true, false).size() <= 1;
    }

    /**
     * Make a copy of this style and add it to its document.
     * 
     * @return the new style with an unused name.
     */
    public final StyleStyle dup() {
        // don't use an ODXMLDocument attribute, search for our document in an ODPackage, that way
        // even if our element changes document (toSingle()) we will find the proper ODXMLDocument
        final ODXMLDocument xmlFile = this.pkg.getXMLFile(this.getElement().getDocument());
        final String unusedName = xmlFile.findUnusedName(getFamily(), this.desc == null ? this.getName() : this.desc.getBaseName());
        final Element clone = (Element) this.getElement().clone();
        clone.setAttribute("name", unusedName, this.getSTYLE());
        JDOMUtils.insertAfter(this.getElement(), singleton(clone));
        return this.desc == null ? new StyleStyle(this.pkg, clone) : this.desc.create(this.pkg, clone);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof StyleStyle))
            return false;
        final StyleStyle o = (StyleStyle) obj;
        return this.getName().equals(o.getName()) && this.getFamily().equals(o.getFamily());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode() + this.getFamily().hashCode();
    }
}
