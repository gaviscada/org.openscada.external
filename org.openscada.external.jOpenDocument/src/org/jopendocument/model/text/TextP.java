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

package org.jopendocument.model.text;

import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class TextP {

    protected String textCondStyleName;

    private final List<TextSpan> textSpans = new Vector<TextSpan>();

    protected String textStyleName;

    public void addTextSpan(final TextSpan p) {
        if (p == null) {
            throw new IllegalArgumentException("null argument");
        }
        this.textSpans.add(p);
    }

    public void addToLastTextSpan(final String string) {

        if (this.isEmpty()) {
            final TextSpan s = new TextSpan();
            s.setValue(string);
            this.textSpans.add(s);
        } else {
            final TextSpan s = this.textSpans.get(this.textSpans.size() - 1);
            s.setValue(s.getValue() + string);
        }
    }

    public String getFullText() {
        String result = "";
        for (final TextSpan t : this.textSpans) {
            result += t.getValue();
        }
        return result;
    }

    /**
     * Gets the value of the textCondStyleName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTextCondStyleName() {
        return this.textCondStyleName;
    }

    public List<TextSpan> getTextSpans() {
        return this.textSpans;
    }

    /**
     * Gets the value of the textStyleName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTextStyleName() {
        return this.textStyleName;
    }

    public boolean isEmpty() {
        return this.textSpans.isEmpty();
    }

    /**
     * Sets the value of the textCondStyleName property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setTextCondStyleName(final String value) {
        this.textCondStyleName = value;
    }

    /**
     * Sets the value of the textStyleName property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setTextStyleName(final String value) {
        this.textStyleName = value;
    }

    @Override
    public String toString() {

        return "TextP:" + this.textSpans;
    }
}
