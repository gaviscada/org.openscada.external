/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 jOpenDocument, by ILM Informatique. All rights reserved.
 * 
 * The contents of this file are subject to the terms of the GNU General Public License Version 3
 * only ("GPL"). You may not use this file except in compliance with the License. You can obtain a
 * copy of the License at http://www.gnu.org/licenses/gpl-3.0.html See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each file.
 * 
 */

package org.jopendocument.model.dom;

import org.jopendocument.model.table.TableTableCell;

public class DomTableCell extends TableTableCell {
    /*
     * Cell c;
     * 
     * public DomTableCell(Cell c) { this.c; }
     * 
     * @Override public String getTableDateValue() {
     * 
     * return c.getTableDateValue(); }
     * 
     * public void setTableDateValue(){ c.setTableDateValue(); }
     */

    /*
     * ElementAccessor e; static protected final SimpleDateFormat TIME_FORMAT = new
     * SimpleDateFormat("'PT'HH'H'mm'M'ss'S'"); static protected final SimpleDateFormat OODateFormat =
     * new SimpleDateFormat("yyyy-MM-dd'T'HH':'mm':'ss");
     * 
     * static final Element createEmpty(NS ns) { return createEmpty(ns, 1); }
     * 
     * static final Element createEmpty(NS ns, int count) { final Element e = new
     * Element("table-cell", ns.getTABLE()); if (count > 1)
     * e.setAttribute("number-columns-repeated", count + "", ns.getTABLE()); return e; }
     * 
     * public DomTableCell(TableTableRow parent, Element e) { this.e = e; this.row = parent; }
     * 
     * protected final NS getNS() { return
     * this.getRow().getTable().getSpreadsheet().getSpreadSheet().getNS(); }
     * 
     * protected final Namespace getValueNS() { return this.getNS().getVersion().equals(OOUtils.OD) ?
     * this.getNS().getOFFICE() : this.getNS().getTABLE(); }
     * 
     * protected final String getType() { return this.getElement().getAttributeValue("value-type",
     * getValueNS()); }
     * 
     * public Element getElement() { return this.e; }
     * 
     * public final String getStyleName() { return this.getElement().getAttributeValue("style-name",
     * getNS().getTABLE()); }
     * 
     * private final String getValue(String attrName) { return
     * this.getElement().getAttributeValue(attrName, getValueNS()); }
     */
}
