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

package org.jopendocument.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;

public class DummyFontMetrics extends FontMetrics {

    protected DummyFontMetrics(Font font) {
        super(font);

    }

    public FontRenderContext getFontRenderContext() {
        return null;
    }

    public int getLeading() {
        return 10;
    }

    public int getAscent() {
        return 0;
    }

    public int getDescent() {
        return 0;
    }

    public int getHeight() {
        return 10;
    }

    public int getMaxAscent() {
        return getAscent();
    }

    public int getMaxDescent() {
        return getDescent();
    }

    @Deprecated
    public int getMaxDecent() {
        return getMaxDescent();
    }

    public int getMaxAdvance() {
        return -1;
    }

    public int charWidth(int codePoint) {
        return 20;
    }

    public int charWidth(char ch) {
        if (ch < 256) {
            return getWidths()[ch];
        }
        char data[] = { ch };
        return charsWidth(data, 0, 1);
    }

    public int charsWidth(char data[], int off, int len) {
        return len * 10;
    }

    public int bytesWidth(byte data[], int off, int len) {
        return stringWidth(new String(data, 0, off, len));
    }

    public boolean hasUniformLineMetrics() {
        return true;
    }

}
