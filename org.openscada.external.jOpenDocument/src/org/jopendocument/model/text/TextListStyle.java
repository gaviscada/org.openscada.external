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

package org.jopendocument.model.text;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class TextListStyle {

    protected String styleName;
    protected String textConsecutiveNumbering;
    protected List<Object> textListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage;

    /**
     * Gets the value of the styleName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getStyleName() {
        return this.styleName;
    }

    /**
     * Gets the value of the textConsecutiveNumbering property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTextConsecutiveNumbering() {
        if (this.textConsecutiveNumbering == null) {
            return "false";
        } else {
            return this.textConsecutiveNumbering;
        }
    }

    /**
     * Gets the value of the
     * textListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is
     * why there is not a <CODE>set</CODE> method for the
     * textListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getTextListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link TextListLevelStyleNumber }
     * {@link TextListLevelStyleBullet } {@link TextListLevelStyleImage }
     * 
     * 
     */
    public List<Object> getTextListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage() {
        if (this.textListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage == null) {
            this.textListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage = new ArrayList<Object>();
        }
        return this.textListLevelStyleNumberOrTextListLevelStyleBulletOrTextListLevelStyleImage;
    }

    /**
     * Sets the value of the styleName property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setStyleName(final String value) {
        this.styleName = value;
    }

    /**
     * Sets the value of the textConsecutiveNumbering property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setTextConsecutiveNumbering(final String value) {
        this.textConsecutiveNumbering = value;
    }

}
