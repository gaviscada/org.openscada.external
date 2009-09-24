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

package org.jopendocument.io;

import java.util.Stack;


import org.jopendocument.model.office.OfficeAutomaticStyles;
import org.jopendocument.model.office.OfficeMasterStyles;
import org.jopendocument.model.office.OfficeStyles;
import org.jopendocument.model.style.StyleDefaultStyle;
import org.jopendocument.model.style.StyleMasterPage;
import org.jopendocument.model.style.StylePageLayout;
import org.jopendocument.model.style.StylePageLayoutProperties;
import org.jopendocument.model.style.StyleTextProperties;
import org.jopendocument.model.text.TextP;
import org.jopendocument.util.ValueHelper;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SaxStylesUnmarshaller extends DefaultHandler {
    private OfficeStyles styles = new OfficeStyles();
    private OfficeAutomaticStyles autoStyles= new OfficeAutomaticStyles();;
    private OfficeMasterStyles masterStyles= new OfficeMasterStyles();
    private Stack<Object> stack;

    private Object current;

    public SaxStylesUnmarshaller() {
        stack = new Stack<Object>();
    }

    public void startElement(String uri, String localName, String qName, Attributes attribs) {

        /*
         * for (int i = 0; i < this.stack.size(); i++) { System.out.print(" "); }
         * System.out.println("Start:" + qName + " " + localName + " +" + attribs.getLocalName(0));
         */// if next element is complex, push a new instance on the stack
        // if element has attributes, set them in the new instance
        if (qName.equals("office:styles")) {
            styles = new OfficeStyles();
            push(styles);
        } else if (qName.equals("style:default-style")) {
            StyleDefaultStyle defaultStyle = new StyleDefaultStyle();
            defaultStyle.setStyleFamily(attribs.getValue("style:family"));

            if (current instanceof OfficeStyles) {
                ((OfficeStyles) current).addDefaultStyle(defaultStyle);

            } else {
                System.err.println("Not OfficeStyles:" + current);
            }

            push(defaultStyle);

        }
        // <style:text-properties style:font-name="Arial"
        else if (qName.equals("style:text-properties")) {
            StyleTextProperties props = new StyleTextProperties();
            props.setFontName(attribs.getValue("style:font-name"));
            if (current instanceof StyleDefaultStyle) {
                ((StyleDefaultStyle) current).setStyleTextProperties(props);
            } else {
                System.err.println("Not StyleDefaultStyle:" + current);
            }

            push(props);

        } else if (qName.equals("office:automatic-styles")) {
            autoStyles = new OfficeAutomaticStyles();
            push(autoStyles);
        } else if (qName.equals("style:page-layout")) {
            StylePageLayout layout = new StylePageLayout();
            layout.setStyleName(attribs.getValue("style:name"));
            if (current instanceof OfficeAutomaticStyles) {
                autoStyles.addPageLayout(layout);
            } else {
                System.err.println("Not OfficeAutomaticStyles:" + current);
            }

            push(layout);

        } else if (qName.equals("style:page-layout-properties")) {
            StylePageLayoutProperties props = new StylePageLayoutProperties();
            props.setPageWidth(attribs.getValue("fo:page-width"));
            props.setPageHeight(attribs.getValue("fo:page-height"));
            props.setMarginTop(attribs.getValue("fo:margin-top"));
            props.setMarginBottom(attribs.getValue("fo:margin-bottom"));
            props.setMarginLeft(attribs.getValue("fo:margin-left"));
            props.setMarginRight(attribs.getValue("fo:margin-right"));
            props.setShadow(attribs.getValue("style:shadow"));
            props.setBackgroundColor(attribs.getValue("fo:background-color"));
            props.setScaleTo(attribs.getValue("style:scale-to"));
            props.setTableCentering(attribs.getValue("style:table-centering"));
            props.setWritingMode(attribs.getValue("style:writing-mode"));

            if (current instanceof StylePageLayout) {
                ((StylePageLayout) current).setPageLayoutProperties(props);
            } else {
                System.err.println("Not StylePageLayout:" + current);
            }

            push(props);

        } else if (qName.equals("office:master-styles")) {
            masterStyles = new OfficeMasterStyles();

            push(masterStyles);
        } else if (qName.equals("style:master-page")) {
            StyleMasterPage page = new StyleMasterPage();
            page.setStyleName(attribs.getValue("style:name"));
            page.setStylePageLayoutName(attribs.getValue("style:page-layout-name"));
            
           
            if (current instanceof OfficeMasterStyles) {
                masterStyles.addMasterPage(page);
            } else {
                System.err.println("Not OfficeMasterStyles:" + current);
            }

            push(page);

        }

        // if none of the above, it is an unexpected element
        else {
        	System.err.println("style.xml : ignoring :"+qName);
            push(uri);

        }
    }

    // -----

    private void push(Object o) {
        this.current = o;
        this.stack.push(o);

    }

    public void endElement(String uri, String localName, String qName) {
        /*
         * for (int i = 0; i < this.stack.size() - 1; i++) { System.out.print(" "); }
         * System.out.println("End: " + qName + " " + localName);
         */

        pop();

    }

    private void pop() {

        if (!stack.isEmpty()) {
            stack.pop();

        }
        if (!stack.isEmpty()) {
            current = stack.peek();
        }
    }

    // -----

    public void characters(char[] data, int start, int length) {

    }

    public OfficeStyles getStyles() {
        return styles;
    }

    public OfficeMasterStyles getMasterStyles() {
        return masterStyles;
    }

    public OfficeAutomaticStyles getAutomaticStyles() {
        return autoStyles;
    }
}
