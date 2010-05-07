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

/**
 * 
 */
public class TextH {

    protected String textCondStyleName;
    protected String textLevel;
    protected String textStyleName;
    protected String value;

    /**
     * Gets the value of the textCondStyleName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTextCondStyleName() {
        return this.textCondStyleName;
    }

    /**
     * Gets the value of the textLevel property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTextLevel() {
        if (this.textLevel == null) {
            return "1";
        } else {
            return this.textLevel;
        }
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

    /**
     * Gets the value of the value property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getvalue() {
        return this.value;
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
     * Sets the value of the textLevel property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setTextLevel(final String value) {
        this.textLevel = value;
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

    /**
     * Sets the value of the value property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setvalue(final String value) {
        this.value = value;
    }

}
