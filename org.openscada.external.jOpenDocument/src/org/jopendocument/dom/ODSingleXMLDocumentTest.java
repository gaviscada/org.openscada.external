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

package org.jopendocument.dom;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import org.jopendocument.dom.spreadsheet.Table;
import org.jopendocument.dom.text.Heading;
import org.jopendocument.dom.text.Paragraph;
import org.jopendocument.dom.text.ParagraphStyle;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jdom.Element;
import org.jdom.xpath.XPath;

public class ODSingleXMLDocumentTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreate() throws Exception {
        final ODSingleXMLDocument single = new ODPackage(this.getClass().getResourceAsStream("test.odt")).toSingle();
        assertNull(single.isValid());
        assertNull(single.checkStyles());
    }

    public void testAdd() throws Exception {
        final ODSingleXMLDocument single = new ODPackage(this.getClass().getResourceAsStream("empty.odt")).toSingle();
        // really empty
        single.getBody().removeContent();
        final ODSingleXMLDocument single2 = new ODPackage(this.getClass().getResourceAsStream("styles.odt")).toSingle();
        single.add(single2);
        assertNull(single.isValid());
        assertNull(single.checkStyles());
    }

    public void testAddParagraph() throws Exception {
        final ODSingleXMLDocument single = new ODPackage(this.getClass().getResourceAsStream("styles.odt")).toSingle();

        final Paragraph p = new Paragraph();
        p.setStyle("testPragraph");
        p.addContent("Hello");
        p.addTab();
        p.addStyledContent("World", "testChar");
        // add at the end
        assertNull(p.getElement().getDocument());
        p.setDocument(single);
        assertSame(single.getDocument(), p.getElement().getDocument());

        final Heading h = new Heading();
        h.setStyle("inexistantt");
        h.addContent("Heading text");
        try {
            h.setDocument(single);
            fail("should throw since style doesn't exist");
        } catch (Exception e) {
            // ok
        }
        h.setStyle("testPragraph");
        // add before p
        final Element pParent = p.getElement().getParentElement();
        single.add(h, pParent, pParent.indexOf(p.getElement()));

        assertNull(single.isValid());
        assertNull(single.checkStyles());

        // rm
        p.setDocument(null);
        assertNull(p.getElement().getDocument());
    }

    public void testTable() throws Exception {
        final ODSingleXMLDocument single = new ODPackage(this.getClass().getResourceAsStream("test.odt")).toSingle();
        assertNull(single.getDescendantByName("draw:text-box", "inexistant"));
        final Element table = single.getDescendantByName("table:table", "JODTestTable");
        assertNotNull(table);
        final Table<ODSingleXMLDocument> t = new Table<ODSingleXMLDocument>(single, table);

        assertEquals(1, t.getHeaderRowCount());
        assertEquals(0, t.getHeaderColumnCount());

        final Calendar c = Calendar.getInstance();
        c.clear();
        c.set(2005, 0, 12, 12, 35);
        assertEquals(c.getTime(), t.getValueAt(2, 1));

        // 11.91cm
        assertEquals(119.06f, t.getWidth());
        t.setColumnCount(6, -1, true);
        t.setValueAt(3.14, 5, 0);
        assertTableWidth(t, 119.06f);
        final float ratio = t.getColumn(0).getWidth() / t.getColumn(1).getWidth();
        t.setColumnCount(2, 1, true);
        // ratio is kept
        assertEquals(ratio, t.getColumn(0).getWidth() / t.getColumn(1).getWidth());
        assertTableWidth(t, 119.06f);
        // table changes width
        final float width = t.getColumn(0).getWidth();
        t.setColumnCount(4, 0, false);
        assertTableWidth(t, 119.06f + 2 * width);
        t.setColumnCount(1, 123, false);
        assertEquals(1, t.getColumnCount());
        assertTableWidth(t, width);

        assertNull(single.isValid());
        assertNull(single.checkStyles());

        t.detach();
        assertNull(single.getDescendantByName("table:table", "JODTestTable"));
    }

    private void assertTableWidth(Table<?> t, float w) {
        assertEquals(w, t.getWidth());
        float total = 0;
        for (int i = 0; i < t.getColumnCount(); i++) {
            total += t.getColumn(i).getWidth();
        }
        assertEquals(round(w), round(total));
    }

    private long round(float w) {
        return Math.round(w * 100.0) / 100;
    }

    public void testFrame() throws Exception {
        final ODPackage pkg = new ODPackage(this.getClass().getResourceAsStream("test.odt"));
        final Element frameElem = pkg.toSingle().getDescendantByName("draw:frame", "Cadre1");

        final ODFrame<ODSingleXMLDocument> frame = new ODFrame<ODSingleXMLDocument>(pkg.toSingle(), frameElem);
        assertEquals(new BigDecimal("42.72"), frame.getWidth());
        // height depends on the content
        assertNull(frame.getHeight());

        assertEquals("right", frame.getStyle().getGraphicProperties().getHorizontalPosition());
        assertEquals("paragraph", frame.getStyle().getGraphicProperties().getHorizontalRelation());

        assertEquals(asList("position"), frame.getStyle().getGraphicProperties().getProtected());
        assertTrue(frame.getStyle().getGraphicProperties().isContentPrinted());
    }

    public void testStyle() throws Exception {
        {
            final ODPackage pkg = new ODPackage(this.getClass().getResourceAsStream("test.odt"));

            // test that multiple attributes may refer to paragraph styles
            final XPath ellipseXPath = pkg.getContent().getXPath("//draw:ellipse[@draw:name = 'Ellipse']");
            final Element ellipse = (Element) ellipseXPath.selectSingleNode(pkg.getContent().getDocument());
            final String drawTextStyleName = ellipse.getAttributeValue("text-style-name", ellipse.getNamespace("draw"));
            final ParagraphStyle ellipseTextStyle = ParagraphStyle.DESC.findStyle(pkg, pkg.getContent().getDocument(), drawTextStyleName);
            assertEquals(singletonList(ellipse), ellipseTextStyle.getReferences());

            // P1 exists both in content and styles but doesn't denote the same style
            final ODXMLDocument styles = pkg.getXMLFile("styles.xml");
            final XPath headerXPath = styles.getXPath("//text:p[string() = 'Header']");
            final Element headerElem = (Element) headerXPath.selectSingleNode(styles.getDocument());
            final String styleName = new Paragraph(headerElem).getStyleName();

            final Element styleP1 = pkg.getStyle(pkg.getDocument("styles.xml"), "paragraph", styleName);
            final Element contentP1 = pkg.getStyle(pkg.getDocument("content.xml"), "paragraph", styleName);
            assertNotNull(styleP1);
            assertNotNull(contentP1);
            assertNotSame(styleP1, contentP1);

            // styleP1 can only be referenced from styles.xml
            assertEquals(singletonList(headerElem), StyleStyle.warp(pkg, styleP1).getReferences());
            // contentP1 can only be referenced from content.xml
            assertFalse(StyleStyle.warp(pkg, contentP1).getReferences().contains(headerElem));

            // when merging styles.xml
            pkg.toSingle();
            // the content doesn't change
            assertSame(contentP1, pkg.getStyle(pkg.getDocument("content.xml"), "paragraph", styleName));
            final String mergedStyleName = new Paragraph((Element) headerXPath.selectSingleNode(pkg.getContent().getDocument())).getStyleName();
            assertNotNull(mergedStyleName);
            assertFalse(styleName.equals(mergedStyleName));
        }

        final ODPackage pkg = new ODPackage(this.getClass().getResourceAsStream("test.odt"));
        final Element heading = (Element) pkg.getContent().getXPath("//text:h[string() = 'Titre 2']").selectSingleNode(pkg.getContent().getDocument());
        final String styleName = heading.getAttributeValue("style-name", heading.getNamespace());
        // common styles are not in content.xml
        assertNull(pkg.getContent().getStyle("paragraph", styleName));
        // but in styles.xml
        testStyleElem(pkg.getXMLFile("styles.xml").getStyle("paragraph", styleName));
        testStyleElem(pkg.getStyle("paragraph", styleName));
        // except if we merge the two
        pkg.toSingle();
        testStyleElem(pkg.getContent().getStyle("paragraph", styleName));
        testStyleElem(pkg.getStyle("paragraph", styleName));
    }

    private void testStyleElem(final Element styleElem) {
        assertNotNull(styleElem);
        assertEquals("2", styleElem.getAttributeValue("default-outline-level", styleElem.getNamespace()));
        assertEquals("Heading", styleElem.getAttributeValue("parent-style-name", styleElem.getNamespace()));
    }

    public void testMeta() throws Exception {
        final ODPackage pkg = new ODPackage(this.getClass().getResourceAsStream("test.odt"));
        final ODMeta meta = pkg.getMeta();
        assertEquals("firstInfo", meta.getUserMeta("Info 1").getValue());
        assertEquals("", meta.getUserMeta("secondName").getValue());

        final List<String> expected = Arrays.asList("Info 1", "secondName", "Info 3", "Info 4");
        assertEquals(expected, meta.getUserMetaNames());

        // does not exist
        assertNull(meta.getUserMeta("toto"));
        // now it does
        assertNotNull(meta.getUserMeta("toto", true));
        meta.removeUserMeta("toto");
        // now it was removed
        assertNull(meta.getUserMeta("toto"));
        final ODUserDefinedMeta toto = meta.getUserMeta("toto", true);
        toto.setValue(3.5);
        assertEquals(ODValueType.FLOAT, toto.getValueType());
        assertEquals(3.5f, ((BigDecimal) toto.getValue()).floatValue());
        final TimeZone marquisesTZ = TimeZone.getTimeZone("Pacific/Marquesas");
        final TimeZone pstTZ = TimeZone.getTimeZone("PST");
        final Calendar cal = Calendar.getInstance(pstTZ);
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);

        {
            toto.setValue(cal, ODValueType.TIME);
            Calendar actual = (Calendar) toto.getValue();
            assertEquals(hour, actual.get(Calendar.HOUR_OF_DAY));
            assertEquals(minute, actual.get(Calendar.MINUTE));
            // for TIME the time zone is important, the same date has not the same value
            cal.setTimeZone(marquisesTZ);
            toto.setValue(cal, ODValueType.TIME);
            actual = (Calendar) toto.getValue();
            // +1h30
            assertFalse(hour == actual.get(Calendar.HOUR_OF_DAY));
            assertFalse(minute == actual.get(Calendar.MINUTE));
        }

        {
            cal.setTimeZone(pstTZ);
            toto.setValue(cal, ODValueType.DATE);
            assertEquals(cal.getTime(), toto.getValue());
            // on the contrary for DATE the timezone is irrelevant
            cal.setTimeZone(marquisesTZ);
            toto.setValue(cal, ODValueType.DATE);
            assertEquals(cal.getTime(), toto.getValue());
        }
    }
}
