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

package org.jopendocument.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jopendocument.model.style.StyleStyle;
import org.jopendocument.model.style.StyleTextProperties;
import org.jopendocument.model.text.TextP;
import org.jopendocument.model.text.TextSpan;

public class ODTCellText {

    private List<ODTCellTextItem> items = new Vector<ODTCellTextItem>();

    private TextP textP;

    private Graphics2D g2;

    private double resizeFactor;

    private StyleStyle cellTextStyle;

    public ODTCellText(Graphics2D g, TextP textp, double resizeFactor, StyleStyle cellStyle) {

        this.textP = textp;
        this.g2 = g;
        this.resizeFactor = resizeFactor;

        if (cellStyle == null) {
            throw new IllegalArgumentException("Default style null");
        }
        this.cellTextStyle = cellStyle;

        computeItems();
    }

    public String getFullText() {
        String t = "";
        List<TextSpan> lt = textP.getTextSpans();
        for (TextSpan tp : lt) {
            if (tp.getValue() != null) {
                t += tp.getValue();
            }
        }
        return t;
    }

    public boolean isEmpty() {

        List<TextSpan> lt = textP.getTextSpans();
        for (TextSpan tp : lt) {
            if (tp.getValue() != null && tp.getValue().length() > 0) {
                return false;
            }
        }
        return true;
    }

    public int getHeight() {
        int maxH = 0;
        for (ODTCellTextItem item : items) {
            int height = item.getHeight();
            if (height > maxH) {
                maxH = height;
            }
        }
        return maxH;
    }

    public int getWidth() {
        int w = 0;
        for (ODTCellTextItem item : items) {
            int width = item.getWidth();
            w += width;
        }
        return w;
    }

    public void draw(int startX, int y) {
        int x = startX;
        for (ODTCellTextItem item : items) {
            Font f = item.getFont();
            g2.setFont(f);
            g2.setColor(item.getColor());
            final String text = item.getText();
            if (text != null) {
                g2.drawString(text, x, y);
                x += item.getWidth();
            }
        }
    }

    private void computeItems() {
        final List<TextSpan> lt = textP.getTextSpans();
        for (TextSpan textpan : lt) {
            textpan.setTextStyle(mergeStyle(cellTextStyle, textpan.getTextStyle()));
            ODTCellTextItem item = new ODTCellTextItem(g2, textpan.getValue(), resizeFactor, textpan.getTextStyle());

            items.add(item);
        }

    }

    private StyleStyle mergeStyle(StyleStyle cellTextStyle, StyleStyle spanTextStyle) {
        StyleStyle s = new StyleStyle();
        StyleTextProperties cellTextProperties = cellTextStyle.getStyleTextProperties();
        StyleTextProperties spanTextProperties = null;
        if (spanTextStyle != null) {
            spanTextProperties = spanTextStyle.getStyleTextProperties();
        }
        s.setTextProperties(mergeTextProperties(cellTextProperties, spanTextProperties));

        return s;
    }

    private StyleTextProperties mergeTextProperties(StyleTextProperties cellTextProperties, StyleTextProperties spanTextProperties) {
        StyleTextProperties r = new StyleTextProperties();
        // Merge font name
        String fontName = null;
        if (spanTextProperties != null) {
            fontName = spanTextProperties.getFontName();
        }
        if (fontName == null && cellTextProperties != null) {
            fontName = cellTextProperties.getFontName();
        }
        if (fontName == null) {
            System.err.println("Assuming default font:Arial");
            fontName = "Arial";
        }
        // Merge font size
        String fontSize = null;
        if (spanTextProperties != null) {
            fontSize = spanTextProperties.getFontSize();
        }
        if (fontSize == null && cellTextProperties != null) {
            fontSize = cellTextProperties.getFontSize();
        }
        if (fontSize == null) {
            fontSize = "11pt";
        }
        // Merge font weight
        String fontWeight = null;
        if (spanTextProperties != null) {
            fontWeight = spanTextProperties.getFontWeight();
        }
        if (fontWeight == null && cellTextProperties != null) {
            fontWeight = cellTextProperties.getFontWeight();
        }
        if (fontWeight == null) {
            fontWeight = "normal";
        }
        // Merge font color
        Color fontColor = null;
        if (spanTextProperties != null) {
            fontColor = spanTextProperties.getColor();

        }
        if (fontColor == null && cellTextProperties != null) {
            fontColor = cellTextProperties.getColor();

        }
        if (fontColor == null) {
            fontColor = Color.BLACK;
        }

        // OK
        r.setFontName(fontName);
        r.setFontSize(fontSize);
        r.setFontWeight(fontWeight);
        r.setColor(fontColor);
        return r;
    }

    public void drawJustified(int x, int y, String verticalAlign, int padding, int cellWidth) {
        final int maxLineWidth = cellWidth - 2 * padding;
        List<ODTCellTextItem> smallitems = new ArrayList<ODTCellTextItem>();
        List<TextSpan> lt = textP.getTextSpans();
        for (TextSpan textpan : lt) {
            textpan.setTextStyle(mergeStyle(cellTextStyle, textpan.getTextStyle()));

            String[] strings = textpan.getCutedValues();
            for (int i = 0; i < strings.length; i++) {
                String s = strings[i].trim();
                ODTCellTextItem item = new ODTCellTextItem(g2, s, resizeFactor, textpan.getTextStyle());

                smallitems.add(item);
            }
        }

        int w = 0;

        ODTCellTextLineItem currentLine = new ODTCellTextLineItem();
        ArrayList<ODTCellTextLineItem> lines = new ArrayList<ODTCellTextLineItem>();
        lines.add(currentLine);
        int maxHeight = 0;
        for (ODTCellTextItem item : smallitems) {
            final int widthWithSpace = item.getWidthWithSpace();
            w += widthWithSpace;

            if (w > maxLineWidth) {

                currentLine = new ODTCellTextLineItem();

                maxHeight = 0;
                w = widthWithSpace;
                lines.add(currentLine);
            }
            final int height = item.getHeight();
            if (height > maxHeight) {
                maxHeight = height;
            }

            currentLine.addItem(item);
        }
        int cx = x + padding;
        int cy = y;
        for (int i = 0; i < lines.size(); i++) {
            ODTCellTextLineItem line = lines.get(i);

            int addSpace = 0;
            int totalSpace = maxLineWidth - line.getTotalWidthWithoutSpace();
            if (line.getSize() > 1 && totalSpace > 0 && i < lines.size() - 1) {
                addSpace = totalSpace / (line.getSize() - 1);
            }
            cy += line.getHeight();
            cy += (4 * 360) / resizeFactor;// FIXME: interligne

            for (ODTCellTextItem item : line.getItems()) {
                Font f = item.getFont();

                g2.setFont(f);
                g2.setColor(item.getColor());
                String text = item.getText();
                if (addSpace == 0) {
                    text += " ";

                    item.setText(text);
                }
                if (text != null) {
                    g2.drawString(text, cx, cy);
                    cx += item.getWidth() + addSpace;
                }
            }

            cx = x + padding;
        }
    }

}
