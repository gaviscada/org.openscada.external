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
package org.odftoolkit.odfdom.dom.element.svg;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;

/**
 * DOM implementation of OpenDocument element  {@odf.element svg:font-face-src}.
 *
 */
public class SvgFontFaceSrcElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.SVG, "font-face-src");

	/**
	 * Create the instance of <code>SvgFontFaceSrcElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public SvgFontFaceSrcElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element svg:font-face-src}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Create child element {@odf.element svg:font-face-name}.
	 *
	 * @return the element {@odf.element svg:font-face-name}
	 */
	public SvgFontFaceNameElement newSvgFontFaceNameElement() {
		SvgFontFaceNameElement svgFontFaceName = ((OdfFileDom) this.ownerDocument).newOdfElement(SvgFontFaceNameElement.class);
		this.appendChild(svgFontFaceName);
		return svgFontFaceName;
	}

	/**
	 * Create child element {@odf.element svg:font-face-uri}.
	 *
	 * @param xlinkHrefValue  the <code>String</code> value of <code>XlinkHrefAttribute</code>, see {@odf.attribute  xlink:href} at specification
	 * @param xlinkTypeValue  the <code>String</code> value of <code>XlinkTypeAttribute</code>, see {@odf.attribute  xlink:type} at specification
	 * @return the element {@odf.element svg:font-face-uri}
	 */
	 public SvgFontFaceUriElement newSvgFontFaceUriElement(String xlinkHrefValue, String xlinkTypeValue) {
		SvgFontFaceUriElement svgFontFaceUri = ((OdfFileDom) this.ownerDocument).newOdfElement(SvgFontFaceUriElement.class);
		svgFontFaceUri.setXlinkHrefAttribute(xlinkHrefValue);
		svgFontFaceUri.setXlinkTypeAttribute(xlinkTypeValue);
		this.appendChild(svgFontFaceUri);
		return svgFontFaceUri;
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
