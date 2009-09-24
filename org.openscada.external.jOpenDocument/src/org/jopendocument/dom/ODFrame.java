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

import org.jopendocument.dom.spreadsheet.CalcNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Represents a draw:frame.
 * 
 * @author Sylvain
 */
public class ODFrame extends CalcNode {

    public static final String UNIT = "mm";
    // 15.2cm
    private static final Pattern lenghPattern = Pattern.compile("(\\d+(\\.\\d+)?)(\\p{Alpha}+)?");

    // 0: value, eg 15 ; 1: unit, eg "cm" or null
    private static final String[] parseLength2String(String l) {
        final Matcher m = lenghPattern.matcher(l);
        if (!m.matches())
            throw new IllegalStateException("unable to parse " + l);
        return new String[] { m.group(1), m.group(3) };
    }

    /**
     * Parse SVG and OD length.
     * 
     * @param l the string to parse, eg "1.53cm".
     * @return the length in {@link #UNIT}, eg 15.3.
     */
    public static final float parseLength(final String l) {
        final String[] valAndUnit = parseLength2String(l);
        final String unit = valAndUnit[1];
        final float multiplier;
        if (unit.equals("cm"))
            multiplier = 10;
        else if (unit.equals(UNIT))
            multiplier = 1;
        else if (unit.equals("in"))
            multiplier = 25.4f;
        else
            throw new IllegalStateException("unknown unit " + unit);
        return Float.parseFloat(valAndUnit[0]) * multiplier;
    }

    private final float width, height;

    public ODFrame(Element frame) {
        super(frame);
        this.width = parseLength(this.getSVGAttr("width"));
        this.height = parseLength(this.getSVGAttr("height"));
    }

    public final float getWidth() {
        return this.width;
    }

    public final float getHeight() {
        return this.height;
    }

    private Namespace getSVG() {
        return getElement().getNamespace("svg");
    }

    public String getSVGAttr(String name) {
        return this.getElement().getAttributeValue(name, getSVG());
    }

    public void setSVGAttr(String name, String val) {
        this.getElement().setAttribute(name, val, this.getSVG());
    }

    /**
     * This set the svg:name attribute to val mm.
     * 
     * @param name the name of the attribute, eg "x".
     * @param val the value of the attribute in {@link #getUnit()}, eg 15.3.
     */
    public void setSVGAttr(String name, double val) {
        this.setSVGAttr(name, val + this.getUnit());
    }

    public final float getRatio() {
        return this.width / this.height;
    }

    public final float getX() {
        return parseLength(this.getSVGAttr("x"));
    }

    public final float getY() {
        return parseLength(this.getSVGAttr("y"));
    }

    /**
     * The unit that all length methods use.
     * 
     * @return the unit used, eg "mm".
     */
    public final String getUnit() {
        return UNIT;
    }
}