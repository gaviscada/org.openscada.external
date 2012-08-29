/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.dom.element.form;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.attribute.form.FormControlImplementationAttribute;
import org.odftoolkit.odfdom.dom.attribute.form.FormLabelAttribute;
import org.odftoolkit.odfdom.dom.attribute.form.FormNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.form.FormTextStyleNameAttribute;

/**
 * DOM implementation of OpenDocument element  {@odf.element form:column}.
 *
 */
public class FormColumnElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.FORM, "column");

	/**
	 * Create the instance of <code>FormColumnElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public FormColumnElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element form:column}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>FormControlImplementationAttribute</code> , See {@odf.attribute form:control-implementation}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getFormControlImplementationAttribute() {
		FormControlImplementationAttribute attr = (FormControlImplementationAttribute) getOdfAttribute(OdfDocumentNamespace.FORM, "control-implementation");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>FormControlImplementationAttribute</code> , See {@odf.attribute form:control-implementation}
	 *
	 * @param formControlImplementationValue   The type is <code>String</code>
	 */
	public void setFormControlImplementationAttribute(String formControlImplementationValue) {
		FormControlImplementationAttribute attr = new FormControlImplementationAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(formControlImplementationValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>FormLabelAttribute</code> , See {@odf.attribute form:label}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getFormLabelAttribute() {
		FormLabelAttribute attr = (FormLabelAttribute) getOdfAttribute(OdfDocumentNamespace.FORM, "label");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>FormLabelAttribute</code> , See {@odf.attribute form:label}
	 *
	 * @param formLabelValue   The type is <code>String</code>
	 */
	public void setFormLabelAttribute(String formLabelValue) {
		FormLabelAttribute attr = new FormLabelAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(formLabelValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>FormNameAttribute</code> , See {@odf.attribute form:name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getFormNameAttribute() {
		FormNameAttribute attr = (FormNameAttribute) getOdfAttribute(OdfDocumentNamespace.FORM, "name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>FormNameAttribute</code> , See {@odf.attribute form:name}
	 *
	 * @param formNameValue   The type is <code>String</code>
	 */
	public void setFormNameAttribute(String formNameValue) {
		FormNameAttribute attr = new FormNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(formNameValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>FormTextStyleNameAttribute</code> , See {@odf.attribute form:text-style-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getFormTextStyleNameAttribute() {
		FormTextStyleNameAttribute attr = (FormTextStyleNameAttribute) getOdfAttribute(OdfDocumentNamespace.FORM, "text-style-name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>FormTextStyleNameAttribute</code> , See {@odf.attribute form:text-style-name}
	 *
	 * @param formTextStyleNameValue   The type is <code>String</code>
	 */
	public void setFormTextStyleNameAttribute(String formTextStyleNameValue) {
		FormTextStyleNameAttribute attr = new FormTextStyleNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(formTextStyleNameValue);
	}

	/**
	 * Create child element {@odf.element form:checkbox}.
	 *
	 * @param formImagePositionValue  the <code>String</code> value of <code>FormImagePositionAttribute</code>, see {@odf.attribute  form:image-position} at specification
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:checkbox}
	 */
	 public FormCheckboxElement newFormCheckboxElement(String formImagePositionValue, String xmlIdValue) {
		FormCheckboxElement formCheckbox = ((OdfFileDom) this.ownerDocument).newOdfElement(FormCheckboxElement.class);
		formCheckbox.setFormImagePositionAttribute(formImagePositionValue);
		formCheckbox.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formCheckbox);
		return formCheckbox;
	}

	/**
	 * Create child element {@odf.element form:combobox}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:combobox}
	 */
	 public FormComboboxElement newFormComboboxElement(String xmlIdValue) {
		FormComboboxElement formCombobox = ((OdfFileDom) this.ownerDocument).newOdfElement(FormComboboxElement.class);
		formCombobox.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formCombobox);
		return formCombobox;
	}

	/**
	 * Create child element {@odf.element form:date}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:date}
	 */
	 public FormDateElement newFormDateElement(String xmlIdValue) {
		FormDateElement formDate = ((OdfFileDom) this.ownerDocument).newOdfElement(FormDateElement.class);
		formDate.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formDate);
		return formDate;
	}

	/**
	 * Create child element {@odf.element form:formatted-text}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:formatted-text}
	 */
	 public FormFormattedTextElement newFormFormattedTextElement(String xmlIdValue) {
		FormFormattedTextElement formFormattedText = ((OdfFileDom) this.ownerDocument).newOdfElement(FormFormattedTextElement.class);
		formFormattedText.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formFormattedText);
		return formFormattedText;
	}

	/**
	 * Create child element {@odf.element form:listbox}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:listbox}
	 */
	 public FormListboxElement newFormListboxElement(String xmlIdValue) {
		FormListboxElement formListbox = ((OdfFileDom) this.ownerDocument).newOdfElement(FormListboxElement.class);
		formListbox.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formListbox);
		return formListbox;
	}

	/**
	 * Create child element {@odf.element form:number}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:number}
	 */
	 public FormNumberElement newFormNumberElement(String xmlIdValue) {
		FormNumberElement formNumber = ((OdfFileDom) this.ownerDocument).newOdfElement(FormNumberElement.class);
		formNumber.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formNumber);
		return formNumber;
	}

	/**
	 * Create child element {@odf.element form:text}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:text}
	 */
	 public FormTextElement newFormTextElement(String xmlIdValue) {
		FormTextElement formText = ((OdfFileDom) this.ownerDocument).newOdfElement(FormTextElement.class);
		formText.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formText);
		return formText;
	}

	/**
	 * Create child element {@odf.element form:textarea}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * @return the element {@odf.element form:textarea}
	 */
	 public FormTextareaElement newFormTextareaElement(String xmlIdValue) {
		FormTextareaElement formTextarea = ((OdfFileDom) this.ownerDocument).newOdfElement(FormTextareaElement.class);
		formTextarea.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formTextarea);
		return formTextarea;
	}

	/**
	 * Create child element {@odf.element form:time}.
	 *
	 * @param xmlIdValue  the <code>String</code> value of <code>XmlIdAttribute</code>, see {@odf.attribute  xml:id} at specification
	 * Child element is new in Odf 1.2
	 *
	 * @return the element {@odf.element form:time}
	 */
	 public FormTimeElement newFormTimeElement(String xmlIdValue) {
		FormTimeElement formTime = ((OdfFileDom) this.ownerDocument).newOdfElement(FormTimeElement.class);
		formTime.setXmlIdAttribute(xmlIdValue);
		this.appendChild(formTime);
		return formTime;
	}

	@Override
	public void accept(ElementVisitor visitor) {
		if (visitor instanceof DefaultElementVisitor) {
			DefaultElementVisitor defaultVisitor = (DefaultElementVisitor) visitor;
			defaultVisitor.visit(this);
		} else {
			visitor.visit(this);
		}
	}
}
