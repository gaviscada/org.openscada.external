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

package org.jopendocument.model.style;

import java.awt.Color;
import java.awt.Font;

import org.jopendocument.util.ValueHelper;

public class StyleTextProperties {

    private Color color;

    private String fontName;

    private String fontSize;

    private String fontWeight;

    Font lastFont;

    double lastResizeFactor;

    public Color getColor() {
        return this.color;
    }

    public Font getFont(final double resizeFactor) {
        if (this.lastResizeFactor == resizeFactor) {
            return this.lastFont;
        }
        String currentFontName = this.fontName;

        int fonttype = Font.PLAIN;
        if (this.fontWeight != null && this.fontWeight.equals("bold")) {
            fonttype = Font.BOLD;
        }
        if (this.fontSize == null) {
            this.fontSize = "10pt";
            System.err.println("Assert default font size: 10");
        }
        final String substring = this.fontSize.substring(0, this.fontSize.length() - 2);
        final int fSize = Integer.valueOf(substring).intValue();
        if (currentFontName.equalsIgnoreCase("Times")) {
            currentFontName = "Times New Roman";
        }

        if (currentFontName == null) {
            throw new IllegalStateException("font name null!!");
        }
        final Font font = new Font(currentFontName, fonttype, (int) (fSize * 360 / resizeFactor));
        // System.err.println("StyleTextProperties.getFont():"+font.getFontName());
        // System.err.println("StyleTextProperties.getFont() " + currentFontName + " font size:" +
        // this.fontSize + " " + (int) ((fSize * 360) / resizeFactor));
        this.lastFont = font;
        this.lastResizeFactor = resizeFactor;
        return font;
    }

    public String getFontName() {
        return this.fontName;
    }

    public String getFontSize() {
        return this.fontSize;
    }

    public String getFontWeight() {
        return this.fontWeight;
    }

    public void setColor(final Color fontColor) {
        this.color = fontColor;

    }

    public void setColor(final String value) {
        if (value != null) {

            this.color = ValueHelper.getColor(value);

        }
    }

    public void setFontName(final String value) {
        this.fontName = value;
    }

    public void setFontSize(final String value) {
        this.fontSize = value;
    }

    public void setFontWeight(final String value) {
        this.fontWeight = value;
    }

    @Override
    public String toString() {
        return "StyleTextProperty:" + this.fontName + " " + this.fontSize + " " + this.fontWeight;
    }
}
