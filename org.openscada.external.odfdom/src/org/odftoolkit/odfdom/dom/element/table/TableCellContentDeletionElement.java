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
package org.odftoolkit.odfdom.dom.element.table;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.attribute.table.TableIdAttribute;

/**
 * DOM implementation of OpenDocument element  {@odf.element table:cell-content-deletion}.
 *
 */
public class TableCellContentDeletionElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.TABLE, "cell-content-deletion");

	/**
	 * Create the instance of <code>TableCellContentDeletionElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public TableCellContentDeletionElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element table:cell-content-deletion}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>TableIdAttribute</code> , See {@odf.attribute table:id}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTableIdAttribute() {
		TableIdAttribute attr = (TableIdAttribute) getOdfAttribute(OdfDocumentNamespace.TABLE, "id");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>TableIdAttribute</code> , See {@odf.attribute table:id}
	 *
	 * @param tableIdValue   The type is <code>String</code>
	 */
	public void setTableIdAttribute(String tableIdValue) {
		TableIdAttribute attr = new TableIdAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(tableIdValue);
	}

	/**
	 * Create child element {@odf.element table:cell-address}.
	 *
	 * @param tableColumnValue  the <code>Integer</code> value of <code>TableColumnAttribute</code>, see {@odf.attribute  table:column} at specification
	 * @param tableRowValue  the <code>Integer</code> value of <code>TableRowAttribute</code>, see {@odf.attribute  table:row} at specification
	 * @param tableTableValue  the <code>Integer</code> value of <code>TableTableAttribute</code>, see {@odf.attribute  table:table} at specification
	 * @return the element {@odf.element table:cell-address}
	 */
	 public TableCellAddressElement newTableCellAddressElement(int tableColumnValue, int tableRowValue, int tableTableValue) {
		TableCellAddressElement tableCellAddress = ((OdfFileDom) this.ownerDocument).newOdfElement(TableCellAddressElement.class);
		tableCellAddress.setTableColumnAttribute(tableColumnValue);
		tableCellAddress.setTableRowAttribute(tableRowValue);
		tableCellAddress.setTableTableAttribute(tableTableValue);
		this.appendChild(tableCellAddress);
		return tableCellAddress;
	}

	/**
	 * Create child element {@odf.element table:change-track-table-cell}.
	 *
	 * @param officeValueValue  the <code>Double</code> value of <code>OfficeValueAttribute</code>, see {@odf.attribute  office:value} at specification
	 * @param officeValueTypeValue  the <code>String</code> value of <code>OfficeValueTypeAttribute</code>, see {@odf.attribute  office:value-type} at specification
	 * @return the element {@odf.element table:change-track-table-cell}
	 */
	 public TableChangeTrackTableCellElement newTableChangeTrackTableCellElement(double officeValueValue, String officeValueTypeValue) {
		TableChangeTrackTableCellElement tableChangeTrackTableCell = ((OdfFileDom) this.ownerDocument).newOdfElement(TableChangeTrackTableCellElement.class);
		tableChangeTrackTableCell.setOfficeValueAttribute(officeValueValue);
		tableChangeTrackTableCell.setOfficeValueTypeAttribute(officeValueTypeValue);
		this.appendChild(tableChangeTrackTableCell);
		return tableChangeTrackTableCell;
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
