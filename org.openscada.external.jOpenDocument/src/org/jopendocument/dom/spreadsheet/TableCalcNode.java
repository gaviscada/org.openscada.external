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

import org.jopendocument.dom.ODDocument;
import org.jopendocument.dom.ODXMLDocument;
import org.jopendocument.dom.StyleStyle;
import org.jopendocument.util.ReflectUtils;

import org.jdom.Element;
import org.jdom.Namespace;

class TableCalcNode<S extends StyleStyle, D extends ODDocument> extends CalcNode {

    private final D parent;
    private final Class<S> styleClass;

    @SuppressWarnings("unchecked")
    public TableCalcNode(D parent, Element local) {
        super(local);
        this.parent = parent;
        this.styleClass = (Class<S>) ReflectUtils.getTypeArguments(this, TableCalcNode.class).get(0);
    }

    public final D getODDocument() {
        return this.parent;
    }

    protected final Namespace getTABLE() {
        return this.getODDocument().getNS().getTABLE();
    }

    protected final ODXMLDocument getContent() {
        return this.getODDocument().getPackage().getContent();
    }

    public final S getStyle() {
        return StyleStyle.findStyle(this.getODDocument().getPackage(), this.styleClass, getStyleName());
    }

    // some nodes have more complicated ways of finding their style (eg Cell)
    protected String getStyleName() {
        return this.getElement().getAttributeValue("style-name", this.getTABLE());
    }
}