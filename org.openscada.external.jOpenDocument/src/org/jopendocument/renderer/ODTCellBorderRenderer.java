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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.jopendocument.model.style.StyleTableCellProperties;
import org.jopendocument.model.table.TableTableCell;


public class ODTCellBorderRenderer implements ODTCellRenderer {
    private static final boolean debug = false;

    final public void draw(final Graphics2D g, final int x, final int y, final int cellWidth, final int cellHeight, final double resizeFactor, final TableTableCell cell,
            final StyleTableCellProperties cellProps) {
        if (debug) {
            if (x % 2 == 0)
                g.setColor(Color.ORANGE);
            else
                g.setColor(Color.RED);
            g.setStroke(new BasicStroke(1f));
            g.drawRect(x, y, cellWidth - 1, cellHeight - 1);
            g.drawLine(x, y, x + cellWidth, y + cellHeight);
            g.drawLine(x+ cellWidth, y, x , y + cellHeight);
        }
        if (cellProps != null) {
            // Left
            if (cellProps.hasLeftBorder()) {
                g.setColor(cellProps.getBorderColorLeft());
                int w = cellProps.getBorderWidthLeft();
                w = (int) (w / resizeFactor);
                if (w < 1) {
                    w = 1;
                }
                g.setStroke(new BasicStroke((float) (w / resizeFactor)));
                g.drawLine(x, y, x, y + cellHeight);

            }
            // Right
            if (cellProps.hasRightBorder()) {
                g.setColor(cellProps.getBorderColorRight());
                int w = cellProps.getBorderWidthRight();
                w = (int) (w / resizeFactor);
                if (w < 1) {
                    w = 1;
                }
                g.setStroke(new BasicStroke((float) (w / resizeFactor)));
                g.drawLine(x + cellWidth, y, x + cellWidth, y + cellHeight);

            }
            // Top
            if (cellProps.hasTopBorder()) {
                g.setColor(cellProps.getBorderColorTop());
                int w = cellProps.getBorderWidthTop();
                w = (int) (w / resizeFactor);
                if (w < 1) {
                    w = 1;
                }
                g.setStroke(new BasicStroke((float) (w / resizeFactor)));
                g.drawLine(x, y, x + cellWidth, y);

            }
            // Bottom
            if (cellProps.hasBottomBorder()) {
                g.setColor(cellProps.getBorderColorBottom());
                int w = cellProps.getBorderWidthBottom();
                w = (int) (w / resizeFactor);
                if (w < 1) {
                    w = 1;
                }
                g.setStroke(new BasicStroke((float) (w / resizeFactor)));
                g.drawLine(x, y + cellHeight, x + cellWidth, y + cellHeight);

            }

        }

    }

}
