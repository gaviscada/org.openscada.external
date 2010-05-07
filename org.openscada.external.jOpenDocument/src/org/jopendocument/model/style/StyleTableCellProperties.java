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

package org.jopendocument.model.style;

import java.awt.Color;

import org.jopendocument.util.ValueHelper;

public class StyleTableCellProperties {

    private String backgroundColor;

    private String border;

    private Color borderColorBottom;

    private Color borderColorLeft;

    private Color borderColorRight;

    private Color borderColorTop;

    private String borderTypeBottom; // solid, none

    private String borderTypeLeft; // solid, none

    private String borderTypeRight; // solid, none

    private String borderTypeTop; // solid, none

    private int borderWidthBottom;

    // Borders
    private int borderWidthLeft;

    private int borderWidthRight;

    private int borderWidthTop;

    private boolean hasBottomBorder;

    private boolean hasLeftBorder;

    private boolean hasRightBorder;

    private boolean hasTopBorder;

    private String padding;

    private String repeatContent;
    private String textAlignSource;
    private String verticalAlign;
    private String wrapOption;

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public String getBorder() {
        return this.border;
    }

    public final Color getBorderColorBottom() {
        return this.borderColorBottom;
    }

    public final Color getBorderColorLeft() {
        return this.borderColorLeft;
    }

    public final Color getBorderColorRight() {
        return this.borderColorRight;
    }

    public final Color getBorderColorTop() {
        return this.borderColorTop;
    }

    public final String getBorderTypeBottom() {
        return this.borderTypeBottom;
    }

    public final String getBorderTypeLeft() {
        return this.borderTypeLeft;
    }

    public final String getBorderTypeRight() {
        return this.borderTypeRight;
    }

    public final String getBorderTypeTop() {
        return this.borderTypeTop;
    }

    public final int getBorderWidthBottom() {
        return this.borderWidthBottom;
    }

    public final int getBorderWidthLeft() {
        return this.borderWidthLeft;
    }

    public final int getBorderWidthRight() {
        return this.borderWidthRight;
    }

    public final int getBorderWidthTop() {
        return this.borderWidthTop;
    }

    public String getPadding() {
        return this.padding;
    }

    public String getRepeatContent() {
        return this.repeatContent;
    }

    public String getTextAlignSource() {
        return this.textAlignSource;
    }

    /**
     * getters
     */
    public String getVerticalAlign() {
        return this.verticalAlign;
    }

    public String getWrapOption() {
        return this.wrapOption;
    }

    public final boolean hasBottomBorder() {
        return this.hasBottomBorder;
    }

    public final boolean hasLeftBorder() {
        return this.hasLeftBorder;
    }

    public final boolean hasRightBorder() {
        return this.hasRightBorder;
    }

    public final boolean hasTopBorder() {
        return this.hasTopBorder;
    }

    public void setBackgroundColor(final String value) {
        this.backgroundColor = value;

    }

    public void setBorder(final String value) {
        if (value != null) {
            this.setBorderLeft(value);
            this.setBorderTop(value);
            this.setBorderRight(value);
            this.setBorderBottom(value);

        }

    }

    public void setBorderBottom(String value) {
        if (value == null) {
            value = "none";
        }
        if (value.equals("none")) {
            // borderTypeBottom = value;

            return;
        }
        this.hasBottomBorder = true;
        final String s[] = value.split(" ");
        this.borderWidthBottom = ValueHelper.getLength(s[0]);
        this.borderTypeBottom = s[1];
        this.borderColorBottom = ValueHelper.getColor(s[2]);
    }

    public void setBorderLeft(String value) {
        if (value == null) {
            value = "none";
        }
        if (value.equals("none")) {
            // borderTypeLeft = value;

            return;
        }
        this.hasLeftBorder = true;
        final String s[] = value.split(" ");
        this.borderWidthLeft = ValueHelper.getLength(s[0]);
        this.borderTypeLeft = s[1];
        this.borderColorLeft = ValueHelper.getColor(s[2]);
    }

    public void setBorderRight(String value) {

        if (value == null) {
            value = "none";
        }
        if (value.equals("none")) {
            // borderTypeRight = value;

            return;
        }
        this.hasRightBorder = true;
        final String s[] = value.split(" ");
        this.borderWidthRight = ValueHelper.getLength(s[0]);
        this.borderTypeRight = s[1];
        this.borderColorRight = ValueHelper.getColor(s[2]);
        // System.err.println(s[0]+","+s[1]+","+s[2]);
    }

    public void setBorderTop(String value) {
        if (value == null) {
            value = "none";
        }
        if (value.equals("none")) {
            // borderTypeTop = value;

            return;
        }
        this.hasTopBorder = true;
        final String s[] = value.split(" ");
        this.borderWidthTop = ValueHelper.getLength(s[0]);
        this.borderTypeTop = s[1];
        this.borderColorTop = ValueHelper.getColor(s[2]);
    }

    public void setPadding(final String value) {
        this.padding = value;

    }

    public void setRepeatContent(final String value) {
        this.repeatContent = value;
    }

    public void setTextAlignSource(final String value) {
        this.textAlignSource = value;
    }

    /**
     * setters
     */
    public void setVerticalAlign(final String value) {
        this.verticalAlign = value;

    }

    public void setWrapOption(final String value) {
        this.wrapOption = value;
    }

    @Override
    public String toString() {

        return "StyleTableCellProperties: border: L:" + this.borderTypeLeft + " R:" + this.borderTypeRight + " T:" + this.borderTypeTop + " B:" + this.borderTypeTop;
    }
}
