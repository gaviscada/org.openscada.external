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

import org.jdom.Document;
import org.jdom.Element;

/**
 * A node with a style.
 * 
 * @author Sylvain CUAZ
 * 
 * @param <S> type of style.
 * @param <D> type of document.
 */
public abstract class StyledNode<S extends StyleStyle, D extends ODDocument> extends ODNode {

    private final StyleDesc<S> styleClass;

    /**
     * Create a new instance. We used to find the {@link StyleStyle} class with reflection but this
     * was slow.
     * 
     * @param local our XML model.
     * @param styleClass our class of style, cannot be <code>null</code>.
     */
    public StyledNode(Element local, final Class<S> styleClass) {
        super(local);
        if (styleClass == null)
            throw new NullPointerException("null style class");
        this.styleClass = StyleStyle.getStyleDesc(styleClass, XMLVersion.getVersion(getElement()));
        assert this.styleClass.getRefElements().contains(this.getElement().getQualifiedName()) : this.getElement().getQualifiedName() + " not in " + this.styleClass;
    }

    // can be null if this node wasn't created from a document (eg new Paragraph())
    public abstract D getODDocument();

    public final S getStyle() {
        final D doc = this.getODDocument();
        return doc == null ? null : this.getStyle(doc.getPackage(), getElement().getDocument());
    }

    protected final S getStyle(final ODPackage pkg, final Document doc) {
        return this.styleClass.findStyle(pkg, doc, getStyleName());
    }

    /**
     * Assure that this node's style is only referenced by this. I.e. after this method returns the
     * style of this node can be safely modified without affecting other nodes.
     * 
     * @return this node's style.
     */
    public final S getPrivateStyle() {
        final S currentStyle = this.getStyle();
        if (currentStyle.isReferencedAtMostOnce())
            return currentStyle;

        final S newStyle = this.styleClass.getStyleClass().cast(currentStyle.dup());
        this.setStyleName(newStyle.getName());
        // return newStyle to avoid the costly getStyle()
        assert this.getStyle().equals(newStyle);
        return newStyle;
    }

    // some nodes have more complicated ways of finding their style (eg Cell)
    protected String getStyleName() {
        return this.getElement().getAttributeValue("style-name", this.getElement().getNamespace());
    }

    public final void setStyleName(final String name) {
        this.getElement().setAttribute("style-name", name, this.getElement().getNamespace());
    }
}