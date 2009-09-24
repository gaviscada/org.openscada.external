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

package org.jopendocument.dom.spreadsheet;

import org.jopendocument.dom.ODDocument;
import org.jopendocument.dom.ODValueType;
import org.jopendocument.dom.spreadsheet.BytesProducer.ByteArrayProducer;
import org.jopendocument.dom.spreadsheet.BytesProducer.ImageProducer;
import org.jopendocument.util.FileUtils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

/**
 * A cell whose value can be changed.
 * 
 * @author Sylvain
 * @param <D> type of document
 */
public class MutableCell<D extends ODDocument> extends Cell<D> {

    static private final DateFormat TextPDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    static private final NumberFormat TextPFloatFormat = new DecimalFormat(",##0.00");

    private final int x;

    // plain Cell have multiple index (repeated) but we are unique,
    // so we can have an index
    MutableCell(Row<D> parent, Element elem, final int x) {
        super(parent, elem);
        this.x = x;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.getRow().getY();
    }

    // *** setValue

    private void setValueAttributes(ODValueType type, Object val) {
        if (type == null) {
            final Attribute valueTypeAttr = this.getElement().getAttribute("value-type", getValueNS());
            if (valueTypeAttr != null) {
                valueTypeAttr.detach();
                this.getElement().removeAttribute(ODValueType.get(valueTypeAttr.getValue()).getValueAttribute(), getValueNS());
            }
        } else {
            this.getElement().setAttribute("value-type", type.getName(), getValueNS());
            this.getElement().setAttribute(type.getValueAttribute(), type.format(val), getValueNS());
        }
    }

    private void setTextP(String value) {
        if (value == null)
            this.getElement().removeContent();
        else {
            final Element t = new Element("p", getNS().getTEXT());
            t.addContent(value);
            this.getElement().setContent(t);
        }
    }

    private void setValue(ODValueType type, Object value, String textP) {
        this.setValueAttributes(type, value);
        this.setTextP(textP);
    }

    public void clearValue() {
        this.setValue(null, null, null);
    }

    public void setValue(Object obj) {
        // FIXME use arbitrary textP format, should use the cell format
        // TODO handle all type of objects as in ODUserDefinedMeta
        // setValue(Object o, final ODValueType vt)
        if (obj instanceof Number)
            // 5.2
            this.setValue(ODValueType.FLOAT, obj, TextPFloatFormat.format(obj));
        else if (obj instanceof Date)
            this.setValue(ODValueType.DATE, obj, TextPDateFormat.format(obj));
        else
            this.setValue(null, null, obj.toString());
    }

    public void replaceBy(String oldValue, String newValue) {
        replaceContentBy(this.getElement(), oldValue, newValue);
    }

    private void replaceContentBy(Element l, String oldValue, String newValue) {
        final List content = l.getContent();
        for (int i = 0; i < content.size(); i++) {
            final Object obj = content.get(i);
            if (obj instanceof Text) {
                // System.err.println(" Text --> " + obj.toString());
                final Text t = (Text) obj;
                t.setText(t.getText().replaceAll(oldValue, newValue));
            } else if (obj instanceof Element) {
                replaceContentBy((Element) obj, oldValue, newValue);
            }
        }
    }

    @Override
    public final String getStyleName() {
        return this.getRow().getSheet().getStyleNameAt(this.getX(), this.getY());
    }

    public void setStyleName(String style) {
        this.getElement().setAttribute("style-name", style, getNS().getTABLE());
    }

    public void setImage(final File pic) throws IOException {
        this.setImage(pic, false);
    }

    public void setImage(final File pic, boolean keepRatio) throws IOException {
        this.setImage(pic.getName(), new ByteArrayProducer(FileUtils.readBytes(pic), keepRatio));
    }

    public void setImage(final String name, final Image img) throws IOException {
        this.setImage(name, img == null ? null : new ImageProducer(img, true));
    }

    private void setImage(final String name, final BytesProducer data) {
        final Namespace draw = this.getNS().getNS("draw");
        final Element frame = this.getElement().getChild("frame", draw);
        final Element imageElem = frame == null ? null : frame.getChild("image", draw);

        if (imageElem != null) {
            final Attribute refAttr = imageElem.getAttribute("href", this.getNS().getNS("xlink"));
            this.getODDocument().getPackage().putFile(refAttr.getValue(), null);

            if (data == null)
                frame.detach();
            else {
                refAttr.setValue("Pictures/" + name + (data.getFormat() != null ? "." + data.getFormat() : ""));
                this.getODDocument().getPackage().putFile(refAttr.getValue(), data.getBytes(frame));
            }
        } else if (data != null)
            throw new IllegalStateException("this cell doesn't contain an image: " + this);
    }
}