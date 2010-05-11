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

import org.jopendocument.util.CollectionMap;
import org.jopendocument.util.CollectionUtils;
import org.jopendocument.util.cc.ITransformer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 * Describe a family of style.
 * 
 * @author Sylvain CUAZ
 * 
 * @param <S> type of style
 */
public abstract class StyleDesc<S extends StyleStyle> {

    public static <C extends StyleStyle> StyleDesc<C> copy(final StyleDesc<C> toClone, final XMLVersion version) {
        final StyleDesc<C> res = new StyleDesc<C>(toClone.getStyleClass(), version, toClone.getFamily(), toClone.getBaseName()) {
            @Override
            public C create(ODPackage pkg, Element e) {
                return toClone.create(pkg, e);
            }
        };
        res.getRefElementsMap().putAll(toClone.getRefElementsMap());
        res.getMultiRefElementsMap().putAll(toClone.getMultiRefElementsMap());
        return res;
    }

    static private final ITransformer<String, String> Q_NAME_TRANSF = new ITransformer<String, String>() {
        public String transformChecked(String input) {
            return "name() = '" + input + "'";
        }
    };

    static private final String getXPath(Collection<Entry<String, Collection<String>>> entries) {
        return CollectionUtils.join(entries, " or ", new ITransformer<Entry<String, Collection<String>>, String>() {

            public String transformChecked(Entry<String, Collection<String>> e) {
                final String nameTest = CollectionUtils.join(e.getValue(), " or ", Q_NAME_TRANSF);
                return "( @" + e.getKey() + " = $name  and ( " + nameTest + " ))";
            }
        });
    }

    private final Class<S> clazz;
    // need version since each one might have different attributes and elements (plus we need it
    // anyway for the XPath, otherwise it fails when searching for an inexistant namespace)
    private final XMLVersion version;
    private final String family, baseName;
    // { attribute -> element }
    private final CollectionMap<String, String> refElements;
    private final CollectionMap<String, String> multiRefElements;
    private XPath refXPath;

    protected StyleDesc(final Class<S> clazz, final XMLVersion version, String family, String baseName, String ns) {
        this(clazz, version, family, baseName, ns, Collections.singletonList(ns + ":" + family));
    }

    protected StyleDesc(final Class<S> clazz, final XMLVersion version, String family, String baseName, String ns, final List<String> refQNames) {
        this(clazz, version, family, baseName);
        this.getRefElementsMap().putAll(ns + ":style-name", refQNames);
    }

    protected StyleDesc(final Class<S> clazz, final XMLVersion version, String family, String baseName) {
        super();
        this.clazz = clazz;
        this.version = version;
        this.family = family;
        this.baseName = baseName;
        this.refElements = new CollectionMap<String, String>();
        // 4 since they are not common
        this.multiRefElements = new CollectionMap<String, String>(4);
        this.refXPath = null;
    }

    public abstract S create(ODPackage pkg, Element e);

    final Class<S> getStyleClass() {
        return this.clazz;
    }

    public final XMLVersion getVersion() {
        return this.version;
    }

    public final String getFamily() {
        return this.family;
    }

    public final String getBaseName() {
        return this.baseName;
    }

    private final XPath getRefXPath() {
        if (this.refXPath == null) {
            final String attrOr = "( $includeSingle and " + getXPath(getRefElementsMap().entrySet()) + " )";
            final String multiOr;
            if (getMultiRefElementsMap().size() == 0)
                multiOr = "";
            else
                multiOr = " or ( $includeMulti and " + getXPath(getMultiRefElementsMap().entrySet()) + " )";
            try {
                this.refXPath = OOUtils.getXPath("//*[ " + attrOr + multiOr + " ]", this.version);
            } catch (JDOMException e) {
                throw new IllegalStateException("couldn't create xpath with " + getRefElements(), e);
            }
        }
        return this.refXPath;
    }

    @SuppressWarnings("unchecked")
    final List<Element> getReferences(final Document doc, final String name, final boolean wantSingle, boolean wantMulti) {
        final XPath xp = this.getRefXPath();
        try {
            synchronized (xp) {
                xp.setVariable("name", name);
                xp.setVariable("includeSingle", wantSingle);
                xp.setVariable("includeMulti", wantMulti);
                return xp.selectNodes(doc);
            }
        } catch (JDOMException e) {
            throw new IllegalStateException("unable to search for occurences of " + this, e);
        }
    }

    /**
     * The list of elements that can point to this family of style.
     * 
     * @return a list of qualified names, e.g. ["text:h", "text:p"].
     */
    protected final Collection<String> getRefElements() {
        return this.getRefElementsMap().values();
    }

    // e.g. { "text:style-name" -> ["text:h", "text:p"] }
    protected final CollectionMap<String, String> getRefElementsMap() {
        return this.refElements;
    }

    // e.g. { "table:default-cell-style-name" -> ["table:table-column", "table:table-row"] }
    protected final CollectionMap<String, String> getMultiRefElementsMap() {
        return this.multiRefElements;
    }

    /**
     * Resolve the passed style name.
     * 
     * @param pkg the package of the searched for style.
     * @param doc the document of the searched for style.
     * @param name the name of the style.
     * @return a corresponding StyleStyle.
     */
    public final S findStyle(final ODPackage pkg, final Document doc, final String name) {
        final Element styleElem = pkg.getStyle(doc, this.getFamily(), name);
        return styleElem == null ? null : this.create(pkg, styleElem);
    }

    public final S createAutoStyle(final ODPackage pkg) {
        return this.createAutoStyle(pkg, getBaseName());
    }

    /**
     * Create a new automatic style in the content of <code>pkg</code>.
     * 
     * @param pkg where to add the new style.
     * @param baseName the base name for the new style, eg "ce".
     * @return the new style, eg named "ce3".
     */
    public final S createAutoStyle(final ODPackage pkg, final String baseName) {
        final ODXMLDocument xml = pkg.getContent();
        final Namespace style = xml.getVersion().getSTYLE();
        final Element elem = new Element("style", style);
        elem.setAttribute("family", this.getFamily(), style);
        elem.setAttribute("name", xml.findUnusedName(this.getFamily(), baseName), style);
        xml.addAutoStyle(elem);
        return this.create(pkg, elem);
    }
}