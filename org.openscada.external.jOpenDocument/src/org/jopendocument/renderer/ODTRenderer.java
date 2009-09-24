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

package org.jopendocument.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

import org.jopendocument.model.OpenDocument;
import org.jopendocument.model.PrintedPage;
import org.jopendocument.model.draw.DrawFrame;
import org.jopendocument.model.draw.DrawImage;
import org.jopendocument.model.office.OfficeBody;
import org.jopendocument.model.office.OfficeSpreadsheet;
import org.jopendocument.model.style.StylePageLayoutProperties;
import org.jopendocument.model.style.StyleStyle;
import org.jopendocument.model.style.StyleTableCellProperties;
import org.jopendocument.model.table.TableShapes;
import org.jopendocument.model.table.TableTable;
import org.jopendocument.model.table.TableTableCell;
import org.jopendocument.model.table.TableTableColumn;
import org.jopendocument.model.table.TableTableRow;
import org.jopendocument.util.ValueHelper;

public class ODTRenderer extends JPanel {

    private static final long serialVersionUID = -4903349568929293597L;

    private double resizeFactor;

    private OfficeBody body;

    private TableTable table;

    private int printHeightPixel;

    private int printWidthPixel;

    private int printWidth;

    private int printHeight;

    private static final ODTCellBackgroundRenderer backgroundRenderer = new ODTCellBackgroundRenderer();

    private static final ODTCellBorderRenderer borderRenderer = new ODTCellBorderRenderer();

    private static final ODTCellTextRenderer textRenderer = new ODTCellTextRenderer();

    private static final ODTCellImageRenderer imageRenderer = new ODTCellImageRenderer();

    private static final boolean DEBUG = false;

    // private final int BORDER_SIZE = 20 * 360;// mm
    private PrintedPage currentPage;

    private boolean paintMaxResolution;

    private boolean ignoreMargins;

    private OpenDocument od;

    public ODTRenderer(OpenDocument doc) {
        this.body = doc.getBody();
        this.setBackground(Color.WHITE);
        this.currentPage = doc.getPrintedPage(0);
        setResizeFactor(360);
        this.od = doc;
        // textRenderer.setStyles(doc.getStyles());
    }

    public void setCurrentPage(int i) {
        this.currentPage = od.getPrintedPage(i);
        repaint();
    }

    public void setCurrentPage(PrintedPage p) {
        this.currentPage = p;
        repaint();
    }

    public int getPrintedPagesNumber() {
        return this.od.getPrintedPageCount();
    }

    public synchronized void setResizeFactor(double resizeFactor) {
        System.err.println("setResizeFactor from" + this.resizeFactor + " to " + resizeFactor);
        this.resizeFactor = resizeFactor;
        List<OfficeSpreadsheet> l = this.body.getOfficeSpreadsheets();
        for (OfficeSpreadsheet sheet : l) {
            System.out.println(sheet);
            List<TableTable> tables = sheet.getTables();
            for (TableTable t : tables) {
                this.table = t;
                // +
                // 2 * BORDER_SIZE;
                if (ignoreMargins) {
                    printWidthPixel = t.getPrintWidth(resizeFactor);
                    printHeightPixel = t.getPrintHeight(resizeFactor);
                    printWidth = (int) t.getPrintWidth();
                    printHeight = (int) t.getPrintHeight();
                } else {
                    printWidth = t.getPrintWidth();
                    printHeight =  t.getPrintHeight();
                    if (t.getPageLayoutProperties() != null) {
                        printWidth += t.getPageLayoutProperties().getMarginLeft() + t.getPageLayoutProperties().getMarginRight();
                        printHeight = t.getPageLayoutProperties().getPageHeight();
                    }
                    printWidthPixel = (int) Math.round(printWidth / resizeFactor);
                    printHeightPixel = (int) Math.round(printHeight / resizeFactor);
                }

                System.out.println("Table p width:" + t.getPrintWidth() + " micrometer" + t.getPrintWidth(resizeFactor));
                System.out.println("Table p height:" + t.getPrintHeight() + " micrometer" + t.getPrintHeight(resizeFactor));

                System.out.println(printWidthPixel + "-" + printHeightPixel);
                setPreferredSize(new Dimension(printWidthPixel, printHeightPixel));
                break;
            }
        }
        repaint();
    }

    public int getPrintWidthInPixel() {
        return printWidthPixel;
    }

    public int getPrintHeightInPixel() {
        return printHeightPixel;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        if (DEBUG) {
            System.err.println("********************** RENDERING BACKGROUND *************");
        }
        drawODTBackground(g2);
        if (DEBUG) {
            System.err.println("********************** RENDERING BORDERS *************");
        }
        drawODTBorders(g2);
        if (DEBUG) {
            System.err.println("********************** RENDERING TEXTS *************");
        }
        drawODTText(g2);
        if (DEBUG) {
            System.err.println("********************** RENDERING IMAGES *************");
        }
        drawODTImages(g2);

    }

    /**
     * @param g2
     */
    private final void drawODTImages(Graphics2D g2) {

        if (!paintMaxResolution) {

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        } else {

            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        }
        // Images in Cells
        drawCells(g2, imageRenderer);

        // Image in Page
        final TableTable currentTable = this.table;
        TableShapes shapes = currentTable.getTableShapes();
        if (shapes == null) {
            return;
        }

        List<DrawFrame> frames = shapes.getDrawFrames();
        final double currentResizeFactor = this.resizeFactor;
        int borderLeft = (int) (currentTable.getPageLayoutProperties().getMarginLeft() / currentResizeFactor);
        int borderRight = (int) (currentTable.getPageLayoutProperties().getMarginRight() / currentResizeFactor);
        int borderTop = (int) (currentTable.getPageLayoutProperties().getMarginTop() / currentResizeFactor);
        int borderBottom = (int) (currentTable.getPageLayoutProperties().getMarginBottom() / currentResizeFactor);
        if (this.ignoreMargins) {
            borderLeft = 0;
            borderRight = 0;
            borderTop = 0;
            borderBottom = 0;
        }

        for (DrawFrame frame : frames) {
            DrawImage dIm = frame.getDrawImage();
            if (dIm != null) {

                double dx = borderLeft + ValueHelper.getLenth(frame.getSvgX()) / resizeFactor;
                double dy = borderTop + ValueHelper.getLenth(frame.getSvgY()) / resizeFactor;
                double w = ValueHelper.getLenth(frame.getSvgWidth()) / resizeFactor;
                double h = ValueHelper.getLenth(frame.getSvgHeight()) / resizeFactor;
                // System.err.println("drawODTImages:" + dIm + " x:" + dx + "
                // y:" + dy + " w:" + w +
                // " h:" + h);
                /*
                 * final String path = "doc/" + dIm.getXlinkHref(); System.err.println("path:" +
                 * path); ImageIcon i = new ImageIcon(path); if (i.getImageLoadStatus() !=
                 * java.awt.MediaTracker.COMPLETE) { throw new IllegalStateException("Unable to
                 * load:'" + path + "'"); }
                 */
                Image im = null;
                if (!paintMaxResolution) {
                    im = body.getDocument().getImage(dIm.getXlinkHref(), (int) w, (int) h);
                    g2.drawImage(im, (int) Math.round(dx), (int) Math.round(dy), null);

                } else {

                    im = body.getDocument().getImage(dIm.getXlinkHref());
                    g2.drawImage(im, (int) Math.round(dx), (int) Math.round(dy), (int) Math.round(w), (int) Math.round(h), null);

                }
                // g2.drawRect((int) Math.round(dx), (int) Math.round(dy), (int)
                // Math.round(w),
                // (int) Math.round(h));
            }
        }
    }

    /**
     * @param g2
     */
    private final void drawODTText(Graphics2D g2) {
        // Texts
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawCells(g2, textRenderer);
    }

    /**
     * @param g2
     */
    private final void drawODTBorders(Graphics2D g2) {
        // Borders
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        drawCells(g2, borderRenderer);
    }

    /**
     * @param g2
     */
    private final void drawODTBackground(Graphics2D g2) {
        // Background
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        drawCells(g2, backgroundRenderer);
    }

    private final void drawCells(final Graphics2D g, final ODTCellRenderer renderer) {
        // Constants
        final TableTable currentTable = this.table;

        final int printStartCol = currentTable.getPrintStartCol();
        final int printStopCol = currentTable.getPrintStopCol();
        final TableTableRow[] rows = this.currentPage.getRows();
        final int rowCount = rows.length;
        final double currentResizeFactor = this.resizeFactor;
        StylePageLayoutProperties pageLayoutProperties = currentTable.getPageLayoutProperties();
        int marginLeft = 0;
        int marginTop = 0;
        if (pageLayoutProperties != null) {
            marginLeft = pageLayoutProperties.getMarginLeft();
            marginTop = pageLayoutProperties.getMarginTop();
        }

        if (ignoreMargins) {
            marginLeft = 0;
            marginTop = 0;
        }

        final int borderLeft = (int) (marginLeft / currentResizeFactor);
        final TableTableColumn[] columns = table.getColumns().toArray(new TableTableColumn[0]);

        int y = (int) (marginTop / currentResizeFactor);

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            final TableTableRow row = rows[rowIndex];
            final int rowHeight = (int) (row.getHeight() / currentResizeFactor);

            // if(c==23) System.err.println("Row ============================="
            // + c + " height:" +
            // rowHeight);
            // c++;
            final TableTableCell[] cells = row.getCellsInRange(printStartCol, printStopCol);
            final int cellsSize = cells.length;

            int x = borderLeft;

            // les cells repeated sont deja clonées
            for (int i = 0; i < cellsSize; i++) {
                final TableTableCell cell = cells[i];
                final TableTableColumn col = columns[printStartCol + i];

                final StyleStyle style = cell.getStyle();
                int cellWidth = (int) (col.getWidth() / currentResizeFactor);

                final StyleTableCellProperties cellProps = style.getStyleTableCellProperties();
                int cellHeight = rowHeight;
                if (cell.getTableNumberRowsSpanned() > 1) {
                    for (int repeat = 1; repeat < cell.getTableNumberRowsSpanned(); repeat++) {
                        cellHeight += rows[rowIndex + repeat].getHeight() / currentResizeFactor;
                    }
                }
                if (cell.getTableNumberColumnsSpanned() > 1) {
                    for (int repeat = 1; repeat < cell.getTableNumberColumnsSpanned(); repeat++) {

                        final int colIndex = printStartCol + i + repeat;
                        final double w = (columns[colIndex].getWidth() / currentResizeFactor);
                        cellWidth += w;
                    }
                }
                try {
                    renderer.draw(g, x, y, cellWidth, cellHeight, currentResizeFactor, cell, cellProps);
                } catch (Exception e) {
                    // TODO: handle exception
                    System.err.println("Failed on x:" + x + " y:" + y + " Cell:" + cell);
                    e.printStackTrace();
                }
                // DEBUG
                // g.setColor(Color.red);
                // g.drawRect(x, y, width, rowHeight);
                // Borders

                x += cellWidth;
            }
            y += rowHeight;

        }
    }

    public double getPrintWidth() {
        return this.printWidth;
    }

    public double getPrintHeight() {
        return this.printHeight;
    }

    /**
     * Set the image rendering policy
     * 
     * @param b true if you need an extra definition (ex for printing)
     */
    public void setPaintMaxResolution(boolean b) {
        this.paintMaxResolution = b;
        imageRenderer.setPaintMaxResolution(b);

    }

    public void setIgnoreMargins(boolean b) {
        this.ignoreMargins = b;
        setResizeFactor(resizeFactor);
    }
}
