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

import org.jopendocument.dom.spreadsheet.CellStyle;
import org.jopendocument.dom.spreadsheet.ColumnStyle;
import org.jopendocument.dom.spreadsheet.RowStyle;
import org.jopendocument.dom.spreadsheet.TableStyle;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A style:style, see section 14.1. Maintains a map of family to classes.
 * 
 * @author Sylvain
 */
public class StyleStyle extends ODNode {

    private static final Map<String, Class<? extends StyleStyle>> family2Class;
    private static final Map<Class<? extends StyleStyle>, String> class2Family;
    static {
        family2Class = new HashMap<String, Class<? extends StyleStyle>>();
        class2Family = new HashMap<Class<? extends StyleStyle>, String>();
        register(CellStyle.class);
        register(RowStyle.class);
        register(ColumnStyle.class);
        register(TableStyle.class);
    }

    private static void register(Class<? extends StyleStyle> styleClass) {
        try {
            styleClass.getDeclaredConstructor(Element.class);
        } catch (NoSuchMethodException e1) {
            throw new IllegalStateException("no Element ctor for " + styleClass);
        }
        final String family;
        try {
            family = (String) styleClass.getDeclaredField("STYLE_FAMILY").get(null);
        } catch (Exception e) {
            throw new IllegalStateException("cannot access the family", e);
        }
        family2Class.put(family, styleClass);
        class2Family.put(styleClass, family);
    }

    public static String getFamily(Class<?> clazz) {
        if (class2Family.containsKey(clazz))
            return class2Family.get(clazz);
        else
            throw new IllegalArgumentException("unregistered " + clazz);
    }

    /**
     * Create the most specific instance for the passed element.
     * 
     * @param styleElem a style:style XML element.
     * @return the most specific instance, eg a new ColumnStyle.
     */
    public static StyleStyle warp(final Element styleElem) {
        final StyleStyle generic = new StyleStyle(styleElem);
        if (family2Class.containsKey(generic.getFamily())) {
            final Class<? extends StyleStyle> styleClass = family2Class.get(generic.getFamily());
            return create(styleClass, styleElem);
        } else
            return generic;
    }

    private static <S extends StyleStyle> S create(final Class<S> styleClass, final Element styleElem) {
        try {
            return styleClass.getDeclaredConstructor(Element.class).newInstance(styleElem);
        } catch (Exception e) {
            throw new IllegalStateException("unable to create " + styleClass, e);
        }
    }

    /**
     * Create a new XML element for the passed type of style.
     * 
     * @param <S> type of style.
     * @param doc where the new style will be added.
     * @param clazz type of style.
     * @param baseName the base name.
     * @return a new style whose name is based on <code>baseName</code>.
     */
    public static <S extends StyleStyle> S create(final ODDocument doc, final Class<S> clazz, final String baseName) {
        final Namespace style = doc.getNS().getSTYLE();
        final Element elem = new Element("style", style);
        final String family = getFamily(clazz);
        elem.setAttribute("family", family, doc.getNS().getSTYLE());
        elem.setAttribute("name", doc.getPackage().getContent().findUnusedName(family, baseName), doc.getNS().getSTYLE());
        return create(clazz, elem);
    }

    /**
     * Resolve the passed style name.
     * 
     * @param <S> type of style.
     * @param doc the document of the searched for style.
     * @param clazz type of style.
     * @param name the name of the style.
     * @return a corresponding StyleStyle.
     */
    public static <S extends StyleStyle> S findStyle(final ODPackage doc, final Class<S> clazz, final String name) {
        final Element styleElem = doc.getStyle(getFamily(clazz), name);
        return styleElem == null ? null : create(clazz, styleElem);
    }

    private final String name, family;
    private final NS ns;

    public StyleStyle(final Element styleElem) {
        super(styleElem);
        this.name = this.getElement().getAttributeValue("name", this.getSTYLE());
        this.family = this.getElement().getAttributeValue("family", this.getSTYLE());
        final String expectedFamily = class2Family.get(this.getClass());
        if (expectedFamily != null && !expectedFamily.equals(this.getFamily()))
            throw new IllegalArgumentException("expected " + expectedFamily + " but got " + this.getFamily() + " for " + styleElem);
        this.ns = NS.getParent(this.getSTYLE());
    }

    protected final Namespace getSTYLE() {
        return this.getElement().getNamespace("style");
    }

    public final String getName() {
        return this.name;
    }

    public final String getFamily() {
        return this.family;
    }

    public final Element getFormattingProperties() {
        final String childName;
        if (this.ns.getVersion().equals(OOUtils.OD))
            childName = this.getFamily() + "-properties";
        else
            childName = "properties";
        Element res = this.getElement().getChild(childName, this.getSTYLE());
        if (res == null) {
            res = new Element(childName, this.getSTYLE());
            this.getElement().addContent(res);
        }
        return res;
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
