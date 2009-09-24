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

package org.jopendocument.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jopendocument.model.OpenDocument;
import org.jopendocument.model.PrintedPage;
import org.jopendocument.print.DocumentPrinter;
import org.jopendocument.renderer.ODTRenderer;

public class ODSViewerPanel extends JPanel {
    ODTRenderer renderer;
    int mode;
    int zoom = 100;
    private static final int MODE_PAGE = 0;
    private static final int MODE_WIDTH = 1;
    private static final int MODE_ZOOM = 2;
    JScrollPane scroll;
    JPanel viewer = new JPanel();
    final JTextField textFieldZoomValue = new JTextField(5);
    int currentPageIndex = 0;

    public ODSViewerPanel(final OpenDocument doc) {
        this(doc, null, true);
    }

    public ODSViewerPanel(final OpenDocument doc, final boolean ignoreMargin) {
        this(doc, null, ignoreMargin);
    }

    public ODSViewerPanel(final OpenDocument doc, final DocumentPrinter printListener) {
        this(doc, printListener, true);
    }

    public ODSViewerPanel(final OpenDocument doc, final DocumentPrinter printListener, final boolean ignoreMargin) {

        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        this.setOpaque(false);
        renderer = new ODTRenderer(doc);
        renderer.setIgnoreMargins(ignoreMargin);
        updateMode(MODE_ZOOM, this.zoom);
        JPanel tools = new JPanel();

        final JButton buttonFullPage = new JButton("Fit page");
        buttonFullPage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                updateMode(MODE_PAGE, 100);

            }
        });
        tools.add(buttonFullPage);
        final JButton buttonFullWidth = new JButton("Fit width");
        buttonFullWidth.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                updateMode(MODE_WIDTH, 100);

            }
        });
        tools.add(buttonFullWidth);

        final JButton buttonZoomOut = new JButton("-");
        buttonZoomOut.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (zoom > 30) {
                    updateMode(MODE_ZOOM, zoom - 20);
                }
            }
        });
        tools.add(buttonZoomOut);
        textFieldZoomValue.setEditable(false);
        tools.add(textFieldZoomValue);
        final JButton buttonZoomIn = new JButton("+");
        buttonZoomIn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int z = zoom + 20;
                if (z > 400)
                    z = 400;
                updateMode(MODE_ZOOM, z);

            }
        });
        tools.add(buttonZoomIn);

        // Viewer
        // viewer.setRenderer(renderer);
        viewer.setOpaque(false);
        viewer.setBackground(Color.DARK_GRAY);
        viewer.setLayout(null);
        renderer.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        viewer.add(renderer);
        this.setLayout(new BorderLayout());
        this.add(tools, BorderLayout.NORTH);
        scroll = new JScrollPane(viewer);
        scroll.setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getHorizontalScrollBar().setUnitIncrement(30);
        scroll.getVerticalScrollBar().setUnitIncrement(30);
        ((JComponent) scroll.getViewport().getView()).setOpaque(false);
        this.add(scroll, BorderLayout.CENTER);
        updateMode(MODE_ZOOM, this.zoom);
        this.addComponentListener(new ComponentListener() {

            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            public void componentResized(ComponentEvent e) {
                updateMode(mode, zoom);

            }

            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub

            }
        });
        if (doc.getPrintedPageCount() > 1) {
            final JTextField page = new JTextField(5);
            JButton previousButton = new JButton("<");
            previousButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (currentPageIndex > 0) {
                        currentPageIndex--;
                        updatePage(doc.getPrintedPage(currentPageIndex));
                        updatePageCount(doc, page);
                    }

                }

            });
            JButton nextButton = new JButton(">");
            nextButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (currentPageIndex < doc.getPrintedPageCount() - 1) {
                        currentPageIndex++;
                        updatePage(doc.getPrintedPage(currentPageIndex));
                        updatePageCount(doc, page);
                    }

                }

            });
            tools.add(previousButton);
            tools.add(page);
            tools.add(nextButton);
        }
        if (printListener != null) {

            final JButton buttonPrint = new JButton("Print");
            buttonPrint.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    printListener.print(doc);

                }
            });
            tools.add(buttonPrint);
        }
    }

    /**
     * @param doc
     * @param page
     */
    private void updatePageCount(final OpenDocument doc, final JTextField page) {
        page.setText((currentPageIndex + 1) + "/" + doc.getPrintedPageCount());
    }

    protected void updatePage(PrintedPage p) {
        this.renderer.setCurrentPage(p);
    }

    private void updateMode(int m, int zoom_value) {
        System.out.println("ODSViewerPanel.updateMode():mode:" + m + " z:" + zoom_value);
        this.mode = m;
        this.zoom = zoom_value;
        int w = renderer.getPrintWidthInPixel();
        int h = renderer.getPrintHeightInPixel();

        switch (m) {
        case MODE_PAGE: {
            int height = this.getHeight() - scroll.getHorizontalScrollBar().getHeight();
            System.out.println("Panel width:" + height + " Renderer W:" + renderer.getPrintWidth());
            final double resizeH = renderer.getPrintHeight() / height;
            System.out.println("resize to:" + resizeH);
            renderer.setResizeFactor(resizeH);
            zoom = (int) Math.round((100 * 360) / resizeH);

            int posx = 0;
            int posy = 0;
            if (scroll != null) {
                posx = (scroll.getViewportBorderBounds().width - w) / 2;

            }

            if (posx < 0)
                posx = 0;
            if (posy < 0)
                posy = 0;
            renderer.setLocation(posx, posy);

            break;
        }
        case MODE_WIDTH: {
            int width = this.getWidth() - scroll.getVerticalScrollBar().getWidth();
            System.out.println("Panel width:" + width + " Renderer W:" + renderer.getPrintWidth());
            final double resizeW = renderer.getPrintWidth() / width;
            System.out.println("resize to:" + resizeW);
            renderer.setResizeFactor(resizeW);
            zoom = (int) Math.round((100 * 360) / resizeW);
            renderer.setLocation(0, 0);

            break;
        }
        case MODE_ZOOM: {
            renderer.setResizeFactor(((100 * 360) / zoom_value));
            System.out.println("w:" + renderer.getPrintWidthInPixel());

            int posx = 0;
            int posy = 0;
            if (scroll != null) {
                posx = (scroll.getViewportBorderBounds().width - w) / 2;
                posy = (scroll.getViewportBorderBounds().height - h) / 2;
            }
            if (posy > 10)
                posy = 10;
            System.out.println("ppppossss:" + posx);
            if (posx < 0)
                posx = 0;
            if (posy < 0)
                posy = 0;
            renderer.setLocation(posx, posy);

            if (posx > 0 || posy > 0) {
                renderer.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            } else {
                renderer.setBorder(null);
            }

            break;
        }
        default:
            break;
        }

        this.textFieldZoomValue.setText(zoom + " %");
        final Dimension size = new Dimension(renderer.getPrintWidthInPixel(), renderer.getPrintHeightInPixel());
        renderer.setSize(renderer.getPrintWidthInPixel(), renderer.getPrintHeightInPixel());
        viewer.setPreferredSize(size);
        viewer.revalidate();
        repaint();
    }

    public ODTRenderer getRenderer() {
        return renderer;
    }
}
