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
package org.odftoolkit.odfdom.dom.element.config;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.attribute.config.ConfigNameAttribute;

/**
 * DOM implementation of OpenDocument element  {@odf.element config:config-item-set}.
 *
 */
public class ConfigConfigItemSetElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.CONFIG, "config-item-set");

	/**
	 * Create the instance of <code>ConfigConfigItemSetElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public ConfigConfigItemSetElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element config:config-item-set}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>ConfigNameAttribute</code> , See {@odf.attribute config:name}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getConfigNameAttribute() {
		ConfigNameAttribute attr = (ConfigNameAttribute) getOdfAttribute(OdfDocumentNamespace.CONFIG, "name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>ConfigNameAttribute</code> , See {@odf.attribute config:name}
	 *
	 * @param configNameValue   The type is <code>String</code>
	 */
	public void setConfigNameAttribute(String configNameValue) {
		ConfigNameAttribute attr = new ConfigNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(configNameValue);
	}

	/**
	 * Create child element {@odf.element config:config-item}.
	 *
	 * @param configNameValue  the <code>String</code> value of <code>ConfigNameAttribute</code>, see {@odf.attribute  config:name} at specification
	 * @param configTypeValue  the <code>String</code> value of <code>ConfigTypeAttribute</code>, see {@odf.attribute  config:type} at specification
	 * @return the element {@odf.element config:config-item}
	 */
	 public ConfigConfigItemElement newConfigConfigItemElement(String configNameValue, String configTypeValue) {
		ConfigConfigItemElement configConfigItem = ((OdfFileDom) this.ownerDocument).newOdfElement(ConfigConfigItemElement.class);
		configConfigItem.setConfigNameAttribute(configNameValue);
		configConfigItem.setConfigTypeAttribute(configTypeValue);
		this.appendChild(configConfigItem);
		return configConfigItem;
	}

	/**
	 * Create child element {@odf.element config:config-item-map-indexed}.
	 *
	 * @param configNameValue  the <code>String</code> value of <code>ConfigNameAttribute</code>, see {@odf.attribute  config:name} at specification
	 * @return the element {@odf.element config:config-item-map-indexed}
	 */
	 public ConfigConfigItemMapIndexedElement newConfigConfigItemMapIndexedElement(String configNameValue) {
		ConfigConfigItemMapIndexedElement configConfigItemMapIndexed = ((OdfFileDom) this.ownerDocument).newOdfElement(ConfigConfigItemMapIndexedElement.class);
		configConfigItemMapIndexed.setConfigNameAttribute(configNameValue);
		this.appendChild(configConfigItemMapIndexed);
		return configConfigItemMapIndexed;
	}

	/**
	 * Create child element {@odf.element config:config-item-map-named}.
	 *
	 * @param configNameValue  the <code>String</code> value of <code>ConfigNameAttribute</code>, see {@odf.attribute  config:name} at specification
	 * @return the element {@odf.element config:config-item-map-named}
	 */
	 public ConfigConfigItemMapNamedElement newConfigConfigItemMapNamedElement(String configNameValue) {
		ConfigConfigItemMapNamedElement configConfigItemMapNamed = ((OdfFileDom) this.ownerDocument).newOdfElement(ConfigConfigItemMapNamedElement.class);
		configConfigItemMapNamed.setConfigNameAttribute(configNameValue);
		this.appendChild(configConfigItemMapNamed);
		return configConfigItemMapNamed;
	}

	/**
	 * Create child element {@odf.element config:config-item-set}.
	 *
	 * @param configNameValue  the <code>String</code> value of <code>ConfigNameAttribute</code>, see {@odf.attribute  config:name} at specification
	 * Child element is mandatory.
	 *
	 * @return the element {@odf.element config:config-item-set}
	 */
	 public ConfigConfigItemSetElement newConfigConfigItemSetElement(String configNameValue) {
		ConfigConfigItemSetElement configConfigItemSet = ((OdfFileDom) this.ownerDocument).newOdfElement(ConfigConfigItemSetElement.class);
		configConfigItemSet.setConfigNameAttribute(configNameValue);
		this.appendChild(configConfigItemSet);
		return configConfigItemSet;
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
