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
import java.awt.Graphics2D;

import org.jopendocument.model.style.StyleParagraphProperties;
import org.jopendocument.model.style.StyleStyle;
import org.jopendocument.model.style.StyleTableCellProperties;
import org.jopendocument.model.table.TableTableCell;
import org.jopendocument.model.text.TextP;
import org.jopendocument.util.ValueHelper;

public class ODTCellTextRenderer implements ODTCellRenderer {

    public void draw(Graphics2D g, int x, int y, int cellWidth, int cellHeight, double resizeFactor, TableTableCell cell, StyleTableCellProperties cellProps) {
        final TextP textp = cell.getTextP();

        if (textp != null) {
            if (textp.isEmpty()) {
                return;
            }
            final StyleStyle cellStyle = cell.getStyle();
            if (cellStyle == null) {
                g.setColor(Color.RED);
                g.drawRect(x, y, cellWidth, cellHeight);
                return;
            }
            final ODTCellText text = new ODTCellText(g, textp, resizeFactor, cellStyle);

            if (!text.isEmpty()) {
                if (cellProps == null) {
                    cellProps = new StyleTableCellProperties();
                }
                if (cellProps.getVerticalAlign() == null) {
                    cellProps.setVerticalAlign("Standard");
                }
                if (cellProps.getPadding() == null) {
                    cellProps.setPadding("0.035cm");
                }
                if (cellStyle != null) {

                    int offsetX = 0;
                    int offsetY = 0;
                    int padding = 0;

                    String verticalAlign = cellProps.getVerticalAlign();
                    String padValue = cellProps.getPadding();
                    padding = 1 + (int) Math.round((ValueHelper.getLength(padValue) / resizeFactor));

                    if (verticalAlign.equals("middle")) {
                        offsetY = (cellHeight + text.getHeight()) / 2;
                    } else if (verticalAlign.equals("top")) {
                        offsetY = text.getHeight();
                        offsetY += padding;
                    } else {
                        offsetY = cellHeight;
                        offsetY -= padding;
                    }
                    boolean justify = false;
                    StyleParagraphProperties paragraphProps = cell.getStyle().getParagraphProperties();
                    if (paragraphProps != null) {
                        String textAlign = paragraphProps.getTextAlign();
                        if (textAlign != null) {
                            if (textAlign.equals("center")) {
                                int strWidth = text.getWidth();
                                offsetX = (cellWidth - strWidth) / 2;
                            } else if (textAlign.equals("end")) {
                                int strWidth = text.getWidth();
                                offsetX = cellWidth - strWidth - padding;
                            } else if (textAlign.equals("justify")) {
                                justify = true;
                                offsetX += padding;
                            } else {
                                // Left
                                offsetX += padding;
                            }
                        } else {
                            final String tableValueType = cell.getTableValueType();
                            if (tableValueType != null && tableValueType.equals("float")) {
                                int strWidth = text.getWidth();
                                offsetX = cellWidth - strWidth - padding;
                            } else {
                                // Left
                                offsetX += padding;
                            }
                        }
                    } else {
                        final String tableValueType = cell.getTableValueType();

                        if (tableValueType != null && tableValueType.equals("float")) {
                            int strWidth = text.getWidth();
                            offsetX = cellWidth - strWidth - padding;
                        } else {
                            // Left
                            offsetX += padding;
                        }
                    }

                    if (!justify) {
                        text.draw(x + offsetX, y + offsetY);
                    } else {
                        text.drawJustified(x, y, verticalAlign, padding, cellWidth);
                    }
                }
            }
        }

    }

}
