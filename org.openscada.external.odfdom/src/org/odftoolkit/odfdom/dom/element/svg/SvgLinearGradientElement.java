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
import org.odftoolkit.odfdom.dom.attribute.draw.DrawDisplayNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgGradientTransformAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgGradientUnitsAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgSpreadMethodAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgX1Attribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgX2Attribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgY1Attribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgY2Attribute;

/**
 * DOM implementation of OpenDocument element  {@odf.element svg:linearGradient}.
 *
 */
public class SvgLinearGradientElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.SVG, "linearGradient");

	/**
	 * Create the instance of <code>SvgLinearGradientElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public SvgLinearGradientElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element svg:linearGradient}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawDisplayNameAttribute</code> , See {@odf.attribute draw:display-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawDisplayNameAttribute() {
		DrawDisplayNameAttribute attr = (DrawDisplayNameAttribute) getOdfAttribute(OdfDocumentNamespace.DRAW, "display-name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawDisplayNameAttribute</code> , See {@odf.attribute draw:display-name}
	 *
	 * @param drawDisplayNameValue   The type is <code>String</code>
	 */
	public void setDrawDisplayNameAttribute(String drawDisplayNameValue) {
		DrawDisplayNameAttribute attr = new DrawDisplayNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(drawDisplayNameValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawNameAttribute</code> , See {@odf.attribute draw:name}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawNameAttribute() {
		DrawNameAttribute attr = (DrawNameAttribute) getOdfAttribute(OdfDocumentNamespace.DRAW, "name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawNameAttribute</code> , See {@odf.attribute draw:name}
	 *
	 * @param drawNameValue   The type is <code>String</code>
	 */
	public void setDrawNameAttribute(String drawNameValue) {
		DrawNameAttribute attr = new DrawNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(drawNameValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgGradientTransformAttribute</code> , See {@odf.attribute svg:gradientTransform}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgGradientTransformAttribute() {
		SvgGradientTransformAttribute attr = (SvgGradientTransformAttribute) getOdfAttribute(OdfDocumentNamespace.SVG, "gradientTransform");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgGradientTransformAttribute</code> , See {@odf.attribute svg:gradientTransform}
	 *
	 * @param svgGradientTransformValue   The type is <code>String</code>
	 */
	public void setSvgGradientTransformAttribute(String svgGradientTransformValue) {
		SvgGradientTransformAttribute attr = new SvgGradientTransformAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgGradientTransformValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgGradientUnitsAttribute</code> , See {@odf.attribute svg:gradientUnits}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgGradientUnitsAttribute() {
		SvgGradientUnitsAttribute attr = (SvgGradientUnitsAttribute) getOdfAttribute(OdfDocumentNamespace.SVG, "gradientUnits");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return SvgGradientUnitsAttribute.DEFAULT_VALUE;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgGradientUnitsAttribute</code> , See {@odf.attribute svg:gradientUnits}
	 *
	 * @param svgGradientUnitsValue   The type is <code>String</code>
	 */
	public void setSvgGradientUnitsAttribute(String svgGradientUnitsValue) {
		SvgGradientUnitsAttribute attr = new SvgGradientUnitsAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgGradientUnitsValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgSpreadMethodAttribute</code> , See {@odf.attribute svg:spreadMethod}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgSpreadMethodAttribute() {
		SvgSpreadMethodAttribute attr = (SvgSpreadMethodAttribute) getOdfAttribute(OdfDocumentNamespace.SVG, "spreadMethod");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return SvgSpreadMethodAttribute.DEFAULT_VALUE;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgSpreadMethodAttribute</code> , See {@odf.attribute svg:spreadMethod}
	 *
	 * @param svgSpreadMethodValue   The type is <code>String</code>
	 */
	public void setSvgSpreadMethodAttribute(String svgSpreadMethodValue) {
		SvgSpreadMethodAttribute attr = new SvgSpreadMethodAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgSpreadMethodValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgX1Attribute</code> , See {@odf.attribute svg:x1}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgX1Attribute() {
		SvgX1Attribute attr = (SvgX1Attribute) getOdfAttribute(OdfDocumentNamespace.SVG, "x1");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return SvgX1Attribute.DEFAULT_VALUE;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgX1Attribute</code> , See {@odf.attribute svg:x1}
	 *
	 * @param svgX1Value   The type is <code>String</code>
	 */
	public void setSvgX1Attribute(String svgX1Value) {
		SvgX1Attribute attr = new SvgX1Attribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgX1Value);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgX2Attribute</code> , See {@odf.attribute svg:x2}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgX2Attribute() {
		SvgX2Attribute attr = (SvgX2Attribute) getOdfAttribute(OdfDocumentNamespace.SVG, "x2");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return SvgX2Attribute.DEFAULT_VALUE;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgX2Attribute</code> , See {@odf.attribute svg:x2}
	 *
	 * @param svgX2Value   The type is <code>String</code>
	 */
	public void setSvgX2Attribute(String svgX2Value) {
		SvgX2Attribute attr = new SvgX2Attribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgX2Value);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgY1Attribute</code> , See {@odf.attribute svg:y1}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgY1Attribute() {
		SvgY1Attribute attr = (SvgY1Attribute) getOdfAttribute(OdfDocumentNamespace.SVG, "y1");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return SvgY1Attribute.DEFAULT_VALUE;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgY1Attribute</code> , See {@odf.attribute svg:y1}
	 *
	 * @param svgY1Value   The type is <code>String</code>
	 */
	public void setSvgY1Attribute(String svgY1Value) {
		SvgY1Attribute attr = new SvgY1Attribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgY1Value);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgY2Attribute</code> , See {@odf.attribute svg:y2}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgY2Attribute() {
		SvgY2Attribute attr = (SvgY2Attribute) getOdfAttribute(OdfDocumentNamespace.SVG, "y2");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return SvgY2Attribute.DEFAULT_VALUE;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgY2Attribute</code> , See {@odf.attribute svg:y2}
	 *
	 * @param svgY2Value   The type is <code>String</code>
	 */
	public void setSvgY2Attribute(String svgY2Value) {
		SvgY2Attribute attr = new SvgY2Attribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgY2Value);
	}

	/**
	 * Create child element {@odf.element svg:stop}.
	 *
	 * @param svgOffsetValue  the <code>String</code> value of <code>SvgOffsetAttribute</code>, see {@odf.attribute  svg:offset} at specification
	 * @return the element {@odf.element svg:stop}
	 */
	 public SvgStopElement newSvgStopElement(String svgOffsetValue) {
		SvgStopElement svgStop = ((OdfFileDom) this.ownerDocument).newOdfElement(SvgStopElement.class);
		svgStop.setSvgOffsetAttribute(svgOffsetValue);
		this.appendChild(svgStop);
		return svgStop;
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
