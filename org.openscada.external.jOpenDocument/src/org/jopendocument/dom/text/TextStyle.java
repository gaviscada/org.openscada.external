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

package org.jopendocument.dom.text;

import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.StyleProperties;
import org.jopendocument.dom.StyleStyle;

import java.awt.Color;

import org.jdom.Element;

public class TextStyle extends StyleStyle {

    static public final String STYLE_FAMILY = "text";

    private SyleTextProperties textProps;

    public TextStyle(final ODPackage pkg, Element tableColElem) {
        super(pkg, tableColElem);
        this.textProps = null;
    }

    public final SyleTextProperties getTextProperties() {
        if (this.textProps == null)
            this.textProps = new SyleTextProperties(this);
        return this.textProps;
    }

    public final Color getColor() {
        return getTextProperties().getColor();
    }

    public final Color getBackgroundColor() {
        return getTextProperties().getBackgroundColor();
    }

    // cf style-text-properties-content-strict in the relaxNG
    public static class SyleTextProperties extends StyleProperties {

        public SyleTextProperties(StyleStyle style) {
            super(style, STYLE_FAMILY);
        }

        public final Color getColor() {
            return OOUtils.decodeRGB(this.getElement().getAttributeValue("color", this.getNS("fo")));
        }

        public final void setColor(Color color) {
            this.getElement().setAttribute("color", OOUtils.encodeRGB(color), this.getNS("fo"));
        }

        public final String getFontName() {
            return this.getElement().getAttributeValue("font-name", this.getElement().getNamespace("style"));
        }

        public final String getLanguage() {
            return this.getElement().getAttributeValue("language", this.getNS("fo"));
        }

        public final String getCountry() {
            return this.getElement().getAttributeValue("country", this.getNS("fo"));
        }

        public final String getWeight() {
            return this.getElement().getAttributeValue("font-weight", this.getNS("fo"));
        }
    }
}
