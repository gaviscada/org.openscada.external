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

import java.util.EnumSet;

import org.jdom.Attribute;
import org.jdom.Element;

// eg <meta:user-defined meta:name="countOfSomething">5.2</meta:user-defined>
public class ODUserDefinedMeta extends ODNode {

    public static String getElemName() {
        return "user-defined";
    }

    static ODUserDefinedMeta create(String name, ODXMLDocument parent) {
        final Element elem = new Element(getElemName(), parent.getNS().getMETA());
        elem.setAttribute("name", name, parent.getNS().getMETA());
        final ODUserDefinedMeta res = new ODUserDefinedMeta(elem, parent);
        res.setValue("");
        return res;
    }

    static String getPathTo(String name) {
        return "meta:" + getElemName() + "[@meta:name = '" + name + "']";
    }

    static String getNamesPath() {
        return "meta:" + getElemName() + "/@meta:name";
    }

    private static final EnumSet<ODValueType> allowedTypes = EnumSet.of(ODValueType.FLOAT, ODValueType.DATE, ODValueType.TIME, ODValueType.BOOLEAN, ODValueType.STRING);

    private final ODXMLDocument parent;

    ODUserDefinedMeta(final Element elem, ODXMLDocument parent) {
        super(elem);
        this.parent = parent;
    }

    protected final ODXMLDocument getParent() {
        return this.parent;
    }

    public final String getName() {
        return this.getElement().getAttributeValue("name", this.getNS().getMETA());
    }

    private final NS getNS() {
        return this.getParent().getNS();
    }

    private final Attribute getValueTypeAttr() {
        return getValueTypeAttr(true);
    }

    private final Attribute getValueTypeAttr(boolean create) {
        Attribute res = this.getElement().getAttribute("value-type", this.getNS().getMETA());
        // oo don't put value-type for strings (eg File/Properties/User)
        if (res == null && create) {
            res = new Attribute("value-type", ODValueType.STRING.getName(), this.getNS().getMETA());
            this.getElement().setAttribute(res);
        }
        return res;
    }

    public final Object getValue() {
        return getValueType().parse(this.getElement().getTextTrim());
    }

    public final ODValueType getValueType() {
        return ODValueType.get(this.getValueTypeAttr().getValue());
    }

    public final void setValue(Object o) {
        this.setValue(o, ODValueType.forObject(o));
    }

    public final void setValue(Object o, final ODValueType vt) {
        if (!allowedTypes.contains(vt))
            throw new IllegalArgumentException(vt + " is not allowed: " + allowedTypes);
        if (vt != ODValueType.STRING)
            this.getValueTypeAttr().setValue(vt.getName());
        else {
            // OOo doesn't support value types
            final Attribute attr = this.getValueTypeAttr(false);
            if (attr != null)
                attr.detach();
        }
        this.getElement().setText(vt.format(o));
    }

}
