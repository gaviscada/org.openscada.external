/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAS Institute Inc. - initial API and implementation
 *     ILOG S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.albireo.internal;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class CleanResizeListener extends ControlAdapter
{
    private Rectangle oldRect = null;

    @Override
    public void controlResized ( final ControlEvent e )
    {
        assert e != null;
        assert Display.getCurrent () != null; // On SWT event thread

        // Prevent garbage from Swing lags during resize. Fill exposed areas 
        // with background color. 
        final Composite composite = (Composite)e.widget;
        final Rectangle newRect = composite.getClientArea ();
        if ( this.oldRect != null )
        {
            final int heightDelta = newRect.height - this.oldRect.height;
            final int widthDelta = newRect.width - this.oldRect.width;
            if ( heightDelta > 0 || widthDelta > 0 )
            {
                final GC gc = new GC ( composite );
                try
                {
                    gc.fillRectangle ( newRect.x, this.oldRect.height, newRect.width, heightDelta );
                    gc.fillRectangle ( this.oldRect.width, newRect.y, widthDelta, newRect.height );
                }
                finally
                {
                    gc.dispose ();
                }
            }
        }
        this.oldRect = newRect;
    }
}
