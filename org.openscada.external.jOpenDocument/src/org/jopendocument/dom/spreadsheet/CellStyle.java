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

import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.StyleDesc;
import org.jopendocument.dom.StyleProperties;
import org.jopendocument.dom.StyleStyle;
import org.jopendocument.dom.XMLVersion;
import org.jopendocument.dom.text.ParagraphStyle.SyleParagraphProperties;
import org.jopendocument.dom.text.TextStyle.SyleTextProperties;

import java.awt.Color;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.jdom.Namespace;

public class CellStyle extends StyleStyle {

    static public enum Side {
        TOP, BOTTOM, LEFT, RIGHT
    }

    static public final String STYLE_FAMILY = "table-cell";

    // from section 18.728 in v1.2-part1
    public static final StyleDesc<CellStyle> DESC = new StyleDesc<CellStyle>(CellStyle.class, XMLVersion.OD, STYLE_FAMILY, "ce", "table", Arrays.asList("table:body", "table:covered-table-cell",
            "table:even-rows", "table:first-column", "table:first-row", "table:last-column", "table:last-row", "table:odd-columns", "table:odd-rows", "table:table-cell")) {

        {
            this.getMultiRefElementsMap().putAll("table:default-cell-style-name", "table:table-column", "table:table-row");
        }

        @Override
        public CellStyle create(ODPackage pkg, Element e) {
            return new CellStyle(pkg, e);
        }
    };

    private SyleTableCellProperties cellProps;
    private SyleTextProperties textProps;
    private SyleParagraphProperties pProps;

    public CellStyle(final ODPackage pkg, Element tableColElem) {
        super(pkg, tableColElem);
    }

    public final Color getBackgroundColor() {
        return getTableCellProperties().getBackgroundColor();
    }

    public final SyleTableCellProperties getTableCellProperties() {
        if (this.cellProps == null)
            this.cellProps = new SyleTableCellProperties(this);
        return this.cellProps;
    }

    public final SyleTextProperties getTextProperties() {
        if (this.textProps == null)
            this.textProps = new SyleTextProperties(this);
        return this.textProps;
    }

    public final SyleParagraphProperties getParagraphProperties() {
        if (this.pProps == null)
            this.pProps = new SyleParagraphProperties(this);
        return this.pProps;
    }

    /**
     * See section 15.11 of OpenDocument v1.1 : Table Cell Formatting Properties.
     * 
     * @author Sylvain CUAZ
     */
    public static class SyleTableCellProperties extends StyleProperties {

        private static final Pattern spacePattern = Pattern.compile(" +");

        public SyleTableCellProperties(StyleStyle style) {
            super(style, STYLE_FAMILY);
        }

        public final String getBorder(final Side s) {
            return getSideAttribute(s, "border", this.getNS("fo"));
        }

        /**
         * If the line style for the border is double, specify the width of the inner and outer
         * lines and the distance between them. See section 15.5.26.
         * 
         * @param s which side.
         * @return the width of the inner line, the distance between the two lines, the width of the
         *         outer line, <code>null</code> if the line style of the border is not double.
         */
        public final String[] getBorderLineWidth(final Side s) {
            final String res = getSideAttribute(s, "border-line-width", this.getElement().getNamespace("style"));
            return res == null ? null : spacePattern.split(res);
        }

        private final String getSideAttribute(final Side s, final String attrName, final Namespace ns) {
            final String allBorder = getElement().getAttributeValue(attrName, ns);
            final String res;
            if (allBorder != null)
                res = allBorder;
            else
                res = getElement().getAttributeValue(attrName + "-" + s.name().toLowerCase(), ns);
            return res;
        }

        public final int getRotationAngle() {
            final String s = this.getElement().getAttributeValue("rotation-angle", this.getElement().getNamespace("style"));
            return s == null ? 0 : Integer.parseInt(s);
        }

        public final boolean isContentPrinted() {
            return parseBoolean(this.getElement().getAttributeValue("print-content", this.getElement().getNamespace("style")), true);
        }

        public final boolean isContentRepeated() {
            return parseBoolean(this.getElement().getAttributeValue("repeat-content", this.getElement().getNamespace("style")), false);
        }

        public final boolean isShrinkToFit() {
            return parseBoolean(this.getElement().getAttributeValue("shrink-to-fit", this.getElement().getNamespace("style")), false);
        }
    }

}
