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

import org.jopendocument.dom.NS;
import org.jopendocument.dom.ODDocument;
import org.jopendocument.dom.StyleStyle;
import org.jopendocument.util.CollectionUtils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * A single sheet in a spreadsheet.
 * 
 * @author Sylvain
 * @param <D> type of table parent
 */
public class Table<D extends ODDocument> extends TableCalcNode<TableStyle, D> {

    static Element createEmpty(NS ns) {
        return new Element("table", ns.getTABLE());
    }

    // ATTN Row have their index as attribute
    private final List<Row<D>> rows;
    private final List<Column<D>> cols;

    @SuppressWarnings("unchecked")
    public Table(D parent, Element local) {
        super(parent, local);

        this.rows = (List<Row<D>>) flatten(false);
        this.cols = (List<Column<D>>) flatten(true);
    }

    @SuppressWarnings("unchecked")
    private List<? extends CalcNode> flatten(boolean col) {
        final String childName = getName(col);
        final List<Element> children = this.getElement().getChildren("table-" + childName, getTABLE());
        // not final, since iter.add() does not work repeatedly (it returns the added items), and
        // thus we must recreate an iterator each time
        ListIterator<Element> iter = children.listIterator();
        while (iter.hasNext()) {
            final Element row = iter.next();
            final Attribute repeatedAttr = row.getAttribute("number-" + childName + "s-repeated", getTABLE());
            if (repeatedAttr != null) {
                row.removeAttribute(repeatedAttr);
                final int index = iter.previousIndex();
                int repeated = Integer.parseInt(repeatedAttr.getValue());
                if (repeated > 60000) {
                    repeated = 10;
                }
                // -1 : we keep the original row
                for (int i = 0; i < repeated - 1; i++) {
                    final Element clone = (Element) row.clone();
                    iter.add(clone);
                }
                // restart after the added rows
                iter = children.listIterator(index + repeated);
            }
        }

        final List<CalcNode> res = new ArrayList<CalcNode>(children.size());
        for (final Element clone : children) {
            // have to cast otherwise javac complains !!!
            res.add(col ? new Column(this, clone) : (CalcNode) new Row(this, clone, res.size()));
        }
        return res;
    }

    private final String getName(boolean col) {
        return col ? "column" : "row";
    }

    public final Object getPrintRanges() {
        return this.getElement().getAttributeValue("print-ranges", this.getTABLE());
    }

    public final void setPrintRanges(String s) {
        this.getElement().setAttribute("print-ranges", s, this.getTABLE());
    }

    public final void removePrintRanges() {
        this.getElement().removeAttribute("print-ranges", this.getTABLE());
    }

    public final synchronized void duplicateFirstRows(int nbFirstRows, int nbDuplicate) {
        this.duplicateRows(0, nbFirstRows, nbDuplicate);
    }

    public final synchronized void insertDuplicatedRows(int rowDuplicated, int nbDuplicate) {
        this.duplicateRows(rowDuplicated, 1, nbDuplicate);
    }

    /**
     * Clone a range of rows. Eg if you want to copy once rows 2 through 5, you call
     * <code>duplicateRows(2, 4, 1)</code>.
     * 
     * @param start the first row to clone.
     * @param count the number of rows after <code>start</code> to clone.
     * @param copies the number of copies of the range to make.
     */
    @SuppressWarnings("unchecked")
    public final synchronized void duplicateRows(int start, int count, int copies) {
        final int stop = start + count;
        final List<Element> children = this.getElement().getChildren("table-row", getTABLE());
        // clone xml elements and add them to our tree
        final List<Element> clones = new ArrayList<Element>(count * copies);
        for (int i = 0; i < copies; i++) {
            for (int l = start; l < stop; l++) {
                final Element r = this.rows.get(l).getElement();
                clones.add((Element) r.clone());
            }
        }
        children.addAll(stop, clones);

        // synchronize our rows with our new tree
        this.rows.clear();
        for (final Element clone : children) {
            addRow(clone);
        }
    }

    private synchronized void addRow(Element child) {
        this.rows.add(new Row<D>(this, child, this.rows.size()));
    }

    public final Point resolveHint(String ref) {
        if (isCellRef(ref)) {
            return resolve(ref);
        } else
            throw new IllegalArgumentException(ref + " is not a cell ref, if it's a named range, you must use it on a SpreadSheet.");
    }

    // *** set cell

    public final boolean isCellValid(int x, int y) {
        if (x > this.getColumnCount())
            return false;
        else if (y > this.getRowCount())
            return false;
        else
            return this.getImmutableCellAt(x, y).isValid();
    }

    public final MutableCell<D> getCellAt(int x, int y) {
        return this.getRow(y).getMutableCellAt(x);
    }

    public final MutableCell<D> getCellAt(String ref) {
        final Point p = resolveHint(ref);
        return this.getCellAt(p.x, p.y);
    }

    /**
     * Sets the value at the specified coordinates.
     * 
     * @param val the new value, <code>null</code> will be treated as "".
     * @param x the column.
     * @param y the row.
     */
    public final void setValueAt(Object val, int x, int y) {
        if (val == null)
            val = "";
        // ne pas casser les repeated pour rien
        if (!val.equals(this.getValueAt(x, y)))
            this.getCellAt(x, y).setValue(val);
    }

    // *** get cell

    protected final Cell<D> getImmutableCellAt(int x, int y) {
        return this.getRow(y).getCellAt(x);
    }

    protected final Cell<D> getImmutableCellAt(String ref) {
        final Point p = resolveHint(ref);
        return this.getImmutableCellAt(p.x, p.y);
    }

    /**
     * @param row la ligne (0 a lineCount-1)
     * @param column la colonnee (0 a colonneCount-1)
     * @return la valeur de la cellule spécifiée.
     */
    public final Object getValueAt(int column, int row) {
        return this.getImmutableCellAt(column, row).getValue();
    }

    /**
     * Find the style name for the specified cell.
     * 
     * @param column column index.
     * @param row row index.
     * @return the style name, can be <code>null</code>.
     */
    public final String getStyleNameAt(int column, int row) {
        // first the cell
        String cellStyle = this.getImmutableCellAt(column, row).getStyleAttr();
        if (cellStyle != null)
            return cellStyle;
        // then the row (as specified in §2 of section 8.1)
        cellStyle = this.getRow(row).getElement().getAttributeValue("default-cell-style-name", getTABLE());
        if (cellStyle != null)
            return cellStyle;
        // and finally the column
        return this.getColumn(column).getElement().getAttributeValue("default-cell-style-name", getTABLE());
    }

    public final CellStyle getStyleAt(int column, int row) {
        return StyleStyle.findStyle(this.getODDocument().getPackage(), CellStyle.class, this.getStyleNameAt(column, row));
    }

    /**
     * Retourne la valeur de la cellule spécifiée.
     * 
     * @param ref une référence de la forme "A3".
     * @return la valeur de la cellule spécifiée.
     */
    public final Object getValueAt(String ref) {
        return this.getImmutableCellAt(ref).getValue();
    }

    // *** get count

    private Row<D> getRow(int index) {
        return this.rows.get(index);
    }

    public final Column<D> getColumn(int i) {
        return this.cols.get(i);
    }

    public final int getRowCount() {
        return this.rows.size();
    }

    public final int getColumnCount() {
        return this.cols.size();
    }

    // *** set count

    /**
     * Assure that this sheet as at least newSize columns.
     * 
     * @param newSize the new column count.
     */
    public final void setColumnCount(int newSize) {
        this.setColumnCount(newSize, -1, false);
    }

    public final void ensureColumnCount(int newSize) {
        if (newSize > this.getColumnCount())
            this.setColumnCount(newSize);
    }

    /**
     * Changes the column count. If newSize is less than getColumnCount() extra cells will be choped
     * off. Otherwise empty cells will be created.
     * 
     * @param newSize the new column count.
     * @param colIndex the index of the column to be copied, -1 for empty column (ie default style).
     * @param keepTableWidth <code>true</code> if the table should be same width after the column
     *        change.
     */
    public final void setColumnCount(int newSize, int colIndex, final boolean keepTableWidth) {
        final int toGrow = newSize - this.getColumnCount();
        if (toGrow < 0) {
            setCount(true, newSize);
        } else {
            // the list of columns cannot be mixed with other elements
            // so just keep adding after the last one
            final int indexOfLastCol;
            if (this.getColumnCount() == 0)
                // from section 8.1.1 the only possible elements after cols are rows
                // but there can't be rows w/o columns, so just add to the end
                indexOfLastCol = this.getElement().getContentSize() - 1;
            else
                indexOfLastCol = this.getElement().getContent().indexOf(this.getColumn(this.getColumnCount() - 1).getElement());

            final Element elemToClone;
            if (colIndex < 0) {
                elemToClone = Column.createEmpty(getODDocument().getNS(), this.createDefaultColStyle());
            } else {
                elemToClone = getColumn(colIndex).getElement();
            }
            for (int i = 0; i < toGrow; i++) {
                final Element newElem = (Element) elemToClone.clone();
                this.getElement().addContent(indexOfLastCol + 1 + i, newElem);
                this.cols.add(new Column<D>(this, newElem));
            }
        }
        // now update widths
        final Float currentWidth = getWidth();
        float newWidth = 0;
        // columns are flattened in ctor: no repeated
        for (final Column col : this.cols) {
            newWidth += col.getWidth();
        }
        // remove all rel-column-width, simpler and Spreadsheet doesn't use them
        // SpreadSheets have no table width
        if (keepTableWidth && currentWidth != null) {
            // compute column-width from table width
            final float ratio = currentWidth / newWidth;
            // once per style not once per col, otherwise if multiple columns with same styles they
            // all will be affected multiple times
            final Set<ColumnStyle> colStyles = new HashSet<ColumnStyle>();
            for (final Column<?> col : this.cols) {
                colStyles.add(col.getStyle());
            }
            for (final ColumnStyle colStyle : colStyles) {
                colStyle.setWidth(colStyle.getWidth() * ratio);
            }
        } else {
            // compute table width from column-width
            if (this.getStyle() != null)
                this.getStyle().setWidth(newWidth);
            for (final Column<?> col : this.cols) {
                col.getStyle().rmRelWidth();
            }
        }

        for (final Row r : this.rows) {
            r.columnCountChanged();
        }
    }

    /**
     * Table width.
     * 
     * @return the table width, can be <code>null</code> (table has no style or style has no
     *         width, eg in SpreadSheet).
     */
    public final Float getWidth() {
        final TableStyle style = this.getStyle();
        return style == null ? null : style.getWidth();
    }

    private final ColumnStyle createDefaultColStyle() {
        final ColumnStyle colStyle = StyleStyle.create(this.getODDocument(), ColumnStyle.class, "defaultCol");
        colStyle.setWidth(20.0f);
        this.getContent().addAutoStyle(colStyle.getElement());
        return colStyle;
    }

    private final void setCount(final boolean col, final int newSize) {
        CollectionUtils.delete(col ? this.cols : this.rows, newSize);
        CollectionUtils.delete(this.getElement().getChildren("table-" + this.getName(col), getTABLE()), newSize);
    }

    public final void ensureRowCount(int newSize) {
        if (newSize > this.getRowCount())
            this.setRowCount(newSize);
    }

    public final void setRowCount(int newSize) {
        this.setRowCount(newSize, -1);
    }

    public final void setRowCount(int newSize, int rowIndex) {
        final Element elemToClone;
        if (rowIndex < 0) {
            elemToClone = Row.createEmpty(this.getODDocument().getNS());
            // each row MUST have the same number of columns
            elemToClone.addContent(Cell.createEmpty(this.getODDocument().getNS(), this.getColumnCount()));
        } else
            elemToClone = getRow(rowIndex).getElement();
        final int toGrow = newSize - this.getRowCount();
        if (toGrow < 0) {
            setCount(false, newSize);
        } else {
            for (int i = 0; i < toGrow; i++) {
                final Element newElem = (Element) elemToClone.clone();
                // as per section 8.1.1 rows are the last elements inside a table
                this.getElement().addContent(newElem);
                addRow(newElem);
            }
        }
    }

    // *** table models

    public final TableModel getTableModel(final int column, final int row) {
        return new SheetTableModel(row, column);
    }

    public final TableModel getTableModel(final int column, final int row, int lastCol, int lastRow) {
        return new SheetTableModel(row, column, lastRow, lastCol);
    }

    public final TableModel getMutableTableModel(final int column, final int row) {
        return new MutableTableModel(row, column);
    }

    public final void merge(TableModel t, final int column, final int row) {
        this.merge(t, column, row, false);
    }

    /**
     * Merges t into this sheet at the specified point.
     * 
     * @param t the data to be merged.
     * @param column the columnn t will be merged at.
     * @param row the row t will be merged at.
     * @param includeColNames if <code>true</code> the column names of t will also be merged.
     */
    public final void merge(TableModel t, final int column, final int row, final boolean includeColNames) {
        final int offset = (includeColNames ? 1 : 0);
        // the columns must be first, see section 8.1.1 of v1.1
        this.ensureColumnCount(column + t.getColumnCount());
        this.ensureRowCount(row + t.getRowCount() + offset);
        final TableModel thisModel = this.getMutableTableModel(column, row);
        if (includeColNames) {
            for (int x = 0; x < t.getColumnCount(); x++) {
                thisModel.setValueAt(t.getColumnName(x), 0, x);
            }
        }
        for (int y = 0; y < t.getRowCount(); y++) {
            for (int x = 0; x < t.getColumnCount(); x++) {
                final Object value = t.getValueAt(y, x);
                thisModel.setValueAt(value, y + offset, x);
            }
        }
    }

    private class SheetTableModel extends AbstractTableModel {
        protected final int row;
        protected final int column;
        protected final int lastRow;
        protected final int lastCol;

        private SheetTableModel(int row, int column) {
            this(row, column, Table.this.getRowCount(), Table.this.getColumnCount());
        }

        /**
         * Creates a new instance.
         * 
         * @param row the first row, inclusive.
         * @param column the first column, inclusive.
         * @param lastRow the last row, exclusive.
         * @param lastCol the last column, exclusive.
         */
        private SheetTableModel(int row, int column, int lastRow, int lastCol) {
            super();
            this.row = row;
            this.column = column;
            this.lastRow = lastRow;
            this.lastCol = lastCol;
        }

        public int getColumnCount() {
            return this.lastCol - this.column;
        }

        public int getRowCount() {
            return this.lastRow - this.row;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return Table.this.getValueAt(this.column + columnIndex, this.row + rowIndex);
        }
    }

    private final class MutableTableModel extends SheetTableModel {

        private MutableTableModel(int row, int column) {
            super(row, column);
        }

        public void setValueAt(Object obj, int rowIndex, int columnIndex) {
            Table.this.setValueAt(obj, this.column + columnIndex, this.row + rowIndex);
        }
    }

    // *** static

    private static final Pattern REF_PATTERN = Pattern.compile("([\\p{Alpha}]+)([\\p{Digit}]+)");

    static final boolean isCellRef(String ref) {
        return REF_PATTERN.matcher(ref).matches();
    }

    // "AA34" => (26,33)
    static final Point resolve(String ref) {
        final Matcher matcher = REF_PATTERN.matcher(ref);
        if (!matcher.matches())
            throw new IllegalArgumentException(ref + " illegal");
        final String letters = matcher.group(1);
        final String digits = matcher.group(2);
        return new Point(toInt(letters), Integer.parseInt(digits) - 1);
    }

    // "AA" => 26
    static final int toInt(String col) {
        if (col.length() < 1)
            throw new IllegalArgumentException("x cannot be empty");
        col = col.toUpperCase();

        int x = 0;
        for (int i = 0; i < col.length(); i++) {
            x = x * 26 + (col.charAt(i) - 'A' + 1);
        }

        // zero based
        return x - 1;
    }

}