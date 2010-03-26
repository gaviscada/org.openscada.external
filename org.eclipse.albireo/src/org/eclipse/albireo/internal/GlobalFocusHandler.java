package org.eclipse.albireo.internal;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

// Handler for global focus events. Maintains global state information like current and 
// previous active widgets. When actions based on global state need to be done, they are
// implemented in this class. See also the FocusHandler class for additional handling based 
// on a single SwingControl.
public class GlobalFocusHandler
{
    private static final String SAVED_FOCUS_OWNER_KEY = "org.eclipse.albireo.savedFocusOwner";

    private final Display display;

    private final SwtEventFilter swtEventFilter;

    private final List listeners = new ArrayList ();

    private static final boolean verboseFocusEvents = FocusHandler.verboseFocusEvents;

    public GlobalFocusHandler ( final Display display )
    {
        this.display = display;
        this.swtEventFilter = new SwtEventFilter ();
        display.addFilter ( SWT.Activate, this.swtEventFilter );
        display.addFilter ( SWT.Deactivate, this.swtEventFilter );
        display.addFilter ( SWT.Traverse, this.swtEventFilter );
    }

    public int getCurrentSwtTraversal ()
    {
        assert Display.getCurrent () != null; // On SWT event thread
        return this.swtEventFilter.currentSwtTraversal;
    }

    public Widget getActiveWidget ()
    {
        assert Display.getCurrent () != null; // On SWT event thread
        return this.swtEventFilter.activeWidget;
    }

    public Shell getActiveShell ()
    {
        assert Display.getCurrent () != null; // On SWT event thread
        return this.swtEventFilter.activeShell;
    }

    public SwingControl getActiveEmbedded ()
    {
        assert Display.getCurrent () != null; // On SWT event thread
        return this.swtEventFilter.activeEmbedded;
    }

    public Widget getLastActiveWidget ()
    {
        assert Display.getCurrent () != null; // On SWT event thread
        return this.swtEventFilter.lastActiveWidget;
    }

    public SwingControl getLastActiveEmbedded ()
    {
        assert Display.getCurrent () != null; // On SWT event thread
        return this.swtEventFilter.lastActiveEmbedded;
    }

    public boolean getLastActiveFocusCleared ()
    {
        assert Display.getCurrent () != null; // On SWT event thread
        return this.swtEventFilter.lastActiveFocusCleared;
    }

    public void setLastActiveFocusCleared ( final boolean lastActiveFocusCleared )
    {
        assert Display.getCurrent () != null; // On SWT event thread
        this.swtEventFilter.lastActiveFocusCleared = lastActiveFocusCleared;
    }

    public void addEventFilter ( final Listener filter )
    {
        this.listeners.add ( filter );
    }

    public void removeEventFilter ( final Listener filter )
    {
        this.listeners.remove ( filter );
    }

    protected void fireEvent ( final Event event )
    {
        for ( final Iterator iterator = this.listeners.iterator (); iterator.hasNext (); )
        {
            final Listener listener = (Listener)iterator.next ();
            listener.handleEvent ( event );
        }
    }

    public void dispose ()
    {
        this.display.removeFilter ( SWT.Activate, this.swtEventFilter );
        this.display.removeFilter ( SWT.Deactivate, this.swtEventFilter );
        this.display.removeFilter ( SWT.Traverse, this.swtEventFilter );
    }

    protected boolean isBorderlessSwingControl ( final Widget widget )
    {
        return widget instanceof SwingControl && ( widget.getStyle () & SWT.EMBEDDED ) != 0;
    }

    protected void clearFocusOwner ( final SwingControl swingControl )
    {
        assert Display.getCurrent () != null; // On SWT event thread

        if ( !swingControl.isAWTPermanentFocusLossForced () )
        {
            return;
        }

        // It appears safe to call getFocusOwner on SWT thread
        final Component owner = ( (Frame)swingControl.getAWTHierarchyRoot () ).getFocusOwner ();
        if ( owner != null )
        {
            EventQueue.invokeLater ( new Runnable () {
                public void run ()
                {
                    // Clear the AWT focus owner so that a permanent focus lost event is 
                    // generated. Where possible, we use the KeyboardFocusManager, but
                    // it has no method to clear the focus owner within a particular frame, 
                    // if that frame is no longer active. In that case, we use a hack of 
                    // disabling and re-enabling the window's focus owner. The hack has
                    // the drawback of a brief visual movement of the cursor (or other 
                    // focus indicator), so it is good to avoid it whenever possible, as 
                    // we do here. 
                    final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager ();
                    if ( owner == kfm.getFocusOwner () )
                    {
                        if ( verboseFocusEvents )
                        {
                            trace ( "clearing focus thru kfm: " + owner );
                        }
                        kfm.clearGlobalFocusOwner ();
                    }
                    else
                    {
                        if ( verboseFocusEvents )
                        {
                            trace ( "clearing focus thru hack: " + owner );
                        }
                        owner.setEnabled ( false );
                        owner.setEnabled ( true );
                    }
                }
            } );
            swingControl.setData ( SAVED_FOCUS_OWNER_KEY, owner );
        }
    }

    protected void restoreFocusOwner ( final SwingControl swingControl )
    {
        assert Display.getCurrent () != null; // On SWT event thread

        final Component savedOwner = (Component)swingControl.getData ( SAVED_FOCUS_OWNER_KEY );
        if ( savedOwner != null )
        {
            swingControl.setData ( SAVED_FOCUS_OWNER_KEY, null );
            EventQueue.invokeLater ( new Runnable () {
                public void run ()
                {
                    // Restore focus to any AWT component that lost focus due to 
                    // clearFocusOwner(). 
                    if ( verboseFocusEvents )
                    {
                        trace ( "restoring focus: " + savedOwner );
                    }
                    savedOwner.requestFocus ();
                }
            } );
        }
    }

    private void trace ( final String msg )
    {
        System.err.println ( header () + ' ' + msg );
    }

    private String header ()
    {
        return "@" + System.currentTimeMillis () + " " + System.identityHashCode ( this );
    }

    protected class SwtEventFilter implements Listener
    {

        int currentSwtTraversal = SWT.TRAVERSE_NONE;

        Widget activeWidget;

        Shell activeShell;

        SwingControl activeEmbedded;

        Widget lastActiveWidget = null;

        SwingControl lastActiveEmbedded = null;

        boolean lastActiveFocusCleared = false;

        public SwtEventFilter ()
        {
            this.activeWidget = GlobalFocusHandler.this.display.getFocusControl ();
            this.activeShell = GlobalFocusHandler.this.display.getActiveShell ();
            if ( isBorderlessSwingControl ( this.activeWidget ) )
            {
                this.activeEmbedded = (SwingControl)this.activeWidget;
            }
        }

        public void handleEvent ( final Event event )
        {
            final Widget widget = event.widget;
            switch ( event.type )
            {
            case SWT.Activate:
                this.activeWidget = widget;

                // Track the currently active shell. This is more reliable than
                // depending on Display.getActiveShell() which sometimes returns an 
                // inactive shell. 
                if ( widget instanceof Shell )
                {
                    this.activeShell = (Shell)widget;
                }

                // If we have moved from a SwingControl to another control in the same
                // shell, clear its current focus owner so that a permanent focus
                // lost event is generated. 
                if ( this.lastActiveEmbedded != null && !this.lastActiveEmbedded.isDisposed () && this.lastActiveEmbedded != widget && !this.lastActiveFocusCleared && widget instanceof Control && // (need a getShell() method)
                this.lastActiveEmbedded.getShell () == ( (Control)widget ).getShell () )
                {
                    clearFocusOwner ( this.lastActiveEmbedded );
                    this.lastActiveFocusCleared = true;
                }

                // If we have moved to a SwingControl, restore the current focus owner
                // that was cleared above during a previous Activate event.
                if ( isBorderlessSwingControl ( widget ) )
                {
                    this.activeEmbedded = (SwingControl)widget;
                    restoreFocusOwner ( this.activeEmbedded );
                }
                break;

            case SWT.Deactivate:
                if ( this.activeWidget != null )
                {
                    this.lastActiveWidget = this.activeWidget;
                    this.activeWidget = null;
                }

                if ( event.widget instanceof Shell )
                {
                    this.activeShell = null;
                }

                if ( isBorderlessSwingControl ( widget ) )
                {
                    if ( this.activeEmbedded != null )
                    {
                        this.lastActiveEmbedded = this.activeEmbedded;
                        this.lastActiveFocusCleared = false;
                        this.activeEmbedded = null;
                    }
                }

                break;

            case SWT.Traverse:
                this.currentSwtTraversal = event.detail;

                break;
            }

            // Propagate to any listeners
            fireEvent ( event );

            // If there is a current traversal, it is now complete
            // with the activation of a control. Reset the value
            // to indicate no current traversal. 
            if ( event.type == SWT.Activate )
            {
                this.currentSwtTraversal = SWT.TRAVERSE_NONE;
            }
        }
    }
}
