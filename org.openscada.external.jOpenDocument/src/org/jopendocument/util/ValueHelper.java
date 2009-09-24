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

package org.jopendocument.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jopendocument.dom.ODFrame;

public final class ValueHelper {

    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    // Cache for Color resolution
    private static final Map<String, Color> colors = new HashMap<String, Color>();

    public static final int getLenth(String value) {
        return Math.round(ODFrame.parseLength(value) * 1000);
    }

    public static final boolean getBoolean(String value) {
        if (value.equals("true")) {
            return (true);
        } else if (value.equals("false")) {
            return (false);
        } else {
            throw new IllegalArgumentException(value + " not a boolean value");
        }
    }

    public final static Color getColor(final String value) {
        if (value == null)
            return null;

        Color c = colors.get(value);
        if (c != null) {
            return c;
        }
        if (value.startsWith("#")) {
            int red = Integer.parseInt(value.substring(1, 3), 16);
            int green = Integer.parseInt(value.substring(3, 5), 16);
            int blue = Integer.parseInt(value.substring(5, 7), 16);

            c = new Color(red, green, blue);
        } else if (value.startsWith("transparent")) {
            c = TRANSPARENT;

        } else {
            System.err.println("unknown color:" + value);
        }
        colors.put(value, c);
        return c;
    }
}
