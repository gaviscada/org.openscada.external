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
package org.odftoolkit.odfdom.dom.element.text;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;

/**
 * DOM implementation of OpenDocument element  {@odf.element text:dde-connection-decls}.
 *
 */
public class TextDdeConnectionDeclsElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.TEXT, "dde-connection-decls");

	/**
	 * Create the instance of <code>TextDdeConnectionDeclsElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public TextDdeConnectionDeclsElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element text:dde-connection-decls}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Create child element {@odf.element text:dde-connection-decl}.
	 *
	 * @param officeDdeApplicationValue  the <code>String</code> value of <code>OfficeDdeApplicationAttribute</code>, see {@odf.attribute  office:dde-application} at specification
	 * @param officeDdeItemValue  the <code>String</code> value of <code>OfficeDdeItemAttribute</code>, see {@odf.attribute  office:dde-item} at specification
	 * @param officeDdeTopicValue  the <code>String</code> value of <code>OfficeDdeTopicAttribute</code>, see {@odf.attribute  office:dde-topic} at specification
	 * @param officeNameValue  the <code>String</code> value of <code>OfficeNameAttribute</code>, see {@odf.attribute  office:name} at specification
	 * @return the element {@odf.element text:dde-connection-decl}
	 */
	 public TextDdeConnectionDeclElement newTextDdeConnectionDeclElement(String officeDdeApplicationValue, String officeDdeItemValue, String officeDdeTopicValue, String officeNameValue) {
		TextDdeConnectionDeclElement textDdeConnectionDecl = ((OdfFileDom) this.ownerDocument).newOdfElement(TextDdeConnectionDeclElement.class);
		textDdeConnectionDecl.setOfficeDdeApplicationAttribute(officeDdeApplicationValue);
		textDdeConnectionDecl.setOfficeDdeItemAttribute(officeDdeItemValue);
		textDdeConnectionDecl.setOfficeDdeTopicAttribute(officeDdeTopicValue);
		textDdeConnectionDecl.setOfficeNameAttribute(officeNameValue);
		this.appendChild(textDdeConnectionDecl);
		return textDdeConnectionDecl;
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
