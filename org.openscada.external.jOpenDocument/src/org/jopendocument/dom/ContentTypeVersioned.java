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

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ContentType} of a certain version.
 * 
 * @author Sylvain
 */
public enum ContentTypeVersioned {
    TEXT_V1(ContentType.TEXT, XMLVersion.OOo, "application/vnd.sun.xml.writer", "text", "sxw") {
    },
    GRAPHICS_V1(ContentType.GRAPHICS, XMLVersion.OOo, "application/vnd.sun.xml.draw", "drawing", "sxd") {
    },
    PRESENTATION_V1(ContentType.PRESENTATION, XMLVersion.OOo, "application/vnd.sun.xml.impress", "presentation", "sxi") {
    },
    SPREADSHEET_V1(ContentType.SPREADSHEET, XMLVersion.OOo, "application/vnd.sun.xml.calc", "spreadsheet", "sxc") {
    },
    TEXT(ContentType.TEXT, XMLVersion.OD, "application/vnd.oasis.opendocument.text", "text", "odt") {
    },
    TEXT_TEMPLATE(ContentType.TEXT, XMLVersion.OD, "application/vnd.oasis.opendocument.text-template", "text", "ott") {
    },
    GRAPHICS(ContentType.GRAPHICS, XMLVersion.OD, "application/vnd.oasis.opendocument.graphics", "drawing", "odg") {
    },
    GRAPHICS_TEMPLATE(ContentType.GRAPHICS, XMLVersion.OD, "application/vnd.oasis.opendocument.graphics-template", "drawing", "otg") {
    },
    PRESENTATION(ContentType.PRESENTATION, XMLVersion.OD, "application/vnd.oasis.opendocument.presentation", "presentation", "odp") {
    },
    PRESENTATION_TEMPLATE(ContentType.PRESENTATION, XMLVersion.OD, "application/vnd.oasis.opendocument.presentation-template", "presentation", "otp") {
    },
    SPREADSHEET(ContentType.SPREADSHEET, XMLVersion.OD, "application/vnd.oasis.opendocument.spreadsheet", "spreadsheet", "ods") {
    },
    SPREADSHEET_TEMPLATE(ContentType.SPREADSHEET, XMLVersion.OD, "application/vnd.oasis.opendocument.spreadsheet-template", "spreadsheet", "ots") {
    };

    private final ContentType type;
    private final XMLVersion version;
    private final String mimeType;
    // either office:class of the root element for V1
    // or the name of the child of office:body for V2
    private final String shortName;
    private final String extension;

    private ContentTypeVersioned(ContentType type, XMLVersion version, String mimeType, String bodyChildName, String extension) {
        this.type = type;
        this.mimeType = mimeType;
        this.version = version;
        this.shortName = bodyChildName;
        this.extension = extension;
    }

    public final XMLVersion getVersion() {
        return this.version;
    }

    public final String getShortName() {
        return this.shortName;
    }

    public final String getMimeType() {
        return this.mimeType;
    }

    public final ContentType getType() {
        return this.type;
    }

    public final String getExtension() {
        return this.extension;
    }

    // *** static

    static public ContentTypeVersioned fromType(ContentType type, XMLVersion version) {
        for (final ContentTypeVersioned t : fromType(type))
            if (t.getVersion() == version)
                return t;
        return null;
    }

    static public Set<ContentTypeVersioned> fromType(ContentType type) {
        final Set<ContentTypeVersioned> res = new HashSet<ContentTypeVersioned>();
        for (final ContentTypeVersioned t : ContentTypeVersioned.values())
            if (t.getType().equals(type))
                res.add(t);
        return res;
    }

    static public ContentTypeVersioned fromMime(String mime) {
        for (final ContentTypeVersioned t : ContentTypeVersioned.values())
            if (t.getMimeType().equals(mime))
                return t;
        return null;
    }

    static ContentTypeVersioned fromClass(String name) {
        return fromShortName(XMLVersion.OOo, name);
    }

    static ContentTypeVersioned fromBody(String name) {
        return fromShortName(XMLVersion.OD, name);
    }

    static private ContentTypeVersioned fromShortName(XMLVersion version, String name) {
        if (name == null)
            throw new NullPointerException();

        for (final ContentTypeVersioned t : ContentTypeVersioned.values())
            if (t.shortName.equals(name) && t.getVersion() == version)
                return t;
        return null;
    }
}
