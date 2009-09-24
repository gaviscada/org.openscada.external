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

/*
 * Cell created on 10 décembre 2005
 */
package org.jopendocument.dom.spreadsheet;

import org.jopendocument.dom.NS;
import org.jopendocument.dom.ODDocument;
import org.jopendocument.dom.ODValueType;
import org.jopendocument.dom.OOUtils;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A cell in a calc document. If you want to change a cell value you must obtain a MutableCell.
 * 
 * @author Sylvain
 * @param <D> type of document
 */
public class Cell<D extends ODDocument> extends TableCalcNode<CellStyle, D> {

    static final Element createEmpty(NS ns) {
        return createEmpty(ns, 1);
    }

    static final Element createEmpty(NS ns, int count) {
        final Element e = new Element("table-cell", ns.getTABLE());
        if (count > 1)
            e.setAttribute("number-columns-repeated", count + "", ns.getTABLE());
        return e;
    }

    private final Row row;

    Cell(Row<D> parent, Element elem) {
        super(parent.getODDocument(), elem);
        this.row = parent;
    }

    protected final Row getRow() {
        return this.row;
    }

    protected final NS getNS() {
        return this.getODDocument().getNS();
    }

    protected final Namespace getValueNS() {
        return this.getNS().getVersion().equals(OOUtils.OD) ? this.getNS().getOFFICE() : this.getNS().getTABLE();
    }

    protected final String getType() {
        return this.getElement().getAttributeValue("value-type", getValueNS());
    }

    protected final ODValueType getValueType() {
        final String type = this.getType();
        return type == null ? null : ODValueType.get(type);
    }

    // cannot resolve our style since a single instance of Cell is used for all
    // repeated and thus if we need to check table-column table:default-cell-style-name
    // we wouldn't know which column to check.
    @Override
    protected String getStyleName() {
        throw new UnsupportedOperationException("cannot resolve our style, use MutableCell");
    }

    String getStyleAttr() {
        return this.getElement().getAttributeValue("style-name", getNS().getTABLE());
    }

    private final String getValue(String attrName) {
        return this.getElement().getAttributeValue(attrName, getValueNS());
    }

    public Object getValue() {
        final ODValueType vt = this.getValueType();
        if (vt == null || vt == ODValueType.STRING) {
            // ATTN oo generates string value-types w/o any @string-value
            final String attr = vt == null ? null : this.getValue(vt.getValueAttribute());
            if (attr != null)
                return attr;
            else {
                final Element child = this.getElement().getChild("p", getNS().getTEXT());
                // empty cell
                return child == null ? "" : child.getText();
            }
        } else {
            return vt.parse(this.getValue(vt.getValueAttribute()));
        }
    }

    public boolean isValid() {
        return !this.getElement().getName().equals("covered-table-cell");
    }

}