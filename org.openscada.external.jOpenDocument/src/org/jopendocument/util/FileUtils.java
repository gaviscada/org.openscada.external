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

package org.jopendocument.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jopendocument.util.cc.ExnTransformer;
import org.jopendocument.util.cc.IClosure;

public final class FileUtils
{

    private FileUtils ()
    {
        // all static
    }

    /**
     * All the files (see {@link File#isFile()}) contained in the passed dir.
     * 
     * @param dir the root directory to search.
     * @return a List of String.
     */
    public static List<String> listR ( final File dir )
    {
        return listR_rec ( dir, "." );
    }

    private static List<String> listR_rec ( final File dir, final String prefix )
    {
        if ( !dir.isDirectory () )
        {
            return null;
        }

        final List<String> res = new ArrayList<String> ();
        final File[] children = dir.listFiles ();
        for ( int i = 0; i < children.length; i++ )
        {
            final String newPrefix = prefix + "/" + children[i].getName ();
            if ( children[i].isFile () )
            {
                // MAYBE add a way to restrict added files
                res.add ( newPrefix );
            }
            else if ( children[i].isDirectory () )
            {
                res.addAll ( listR_rec ( children[i], newPrefix ) );
            }
        }
        return res;
    }

    public static void walk ( final File dir, final IClosure<File> c )
    {
        walk ( dir, c, RecursionType.BREADTH_FIRST );
    }

    public static void walk ( final File dir, final IClosure<File> c, final RecursionType type )
    {
        if ( type == RecursionType.BREADTH_FIRST )
        {
            c.execute ( dir );
        }
        if ( dir.isDirectory () )
        {
            for ( final File child : dir.listFiles () )
            {
                walk ( child, c, type );
            }
        }
        if ( type == RecursionType.DEPTH_FIRST )
        {
            c.execute ( dir );
        }
    }

    public static final List<File> list ( final File root, final int depth )
    {
        return list ( root, depth, null );
    }

    /**
     * Finds all files at the specified depth below <code>root</code>.
     * 
     * @param root the base directory
     * @param depth the depth of the returned files.
     * @param ff a filter, can be <code>null</code>.
     * @return a list of files <code>depth</code> levels beneath <code>root</code>.
     */
    public static final List<File> list ( final File root, final int depth, final FileFilter ff )
    {
        if ( !root.exists () )
        {
            return Collections.<File> emptyList ();
        }
        if ( depth == 0 )
        {
            return ff.accept ( root ) ? Collections.singletonList ( root ) : Collections.<File> emptyList ();
        }
        else if ( depth == 1 )
        {
            final File[] listFiles = root.listFiles ( ff );
            if ( listFiles == null )
            {
                throw new IllegalStateException ( "cannot list " + root );
            }
            return Arrays.asList ( listFiles );
        }
        else
        {
            final File[] childDirs = root.listFiles ( DIR_FILTER );
            if ( childDirs == null )
            {
                throw new IllegalStateException ( "cannot list " + root );
            }
            final List<File> res = new ArrayList<File> ();
            for ( final File child : childDirs )
            {
                res.addAll ( list ( child, depth - 1, ff ) );
            }
            return res;
        }
    }

    /**
     * Returns the relative path from one file to another in the same filesystem tree.
     * 
     * @param fromDir the starting directory, eg /a/b/.
     * @param to the file to get to, eg /a/x/y.txt.
     * @return the relative path, eg "../x/y.txt".
     * @throws IOException if an error occurs while canonicalizing the files.
     * @throws IllegalArgumentException if fromDir is not directory, or the files have no common
     *         ancestors (for example on Windows on 2 different letters).
     */
    public static final String relative ( final File fromDir, final File to ) throws IOException
    {
        if ( !fromDir.isDirectory () )
        {
            throw new IllegalArgumentException ( fromDir + " is not a directory" );
        }

        final File fromF = fromDir.getCanonicalFile ();
        final File toF = to.getCanonicalFile ();
        final List<File> toPath = getAncestors ( toF );
        final List<File> fromPath = getAncestors ( fromF );

        if ( !toPath.get ( 0 ).equals ( fromPath.get ( 0 ) ) )
        {
            throw new IllegalArgumentException ( "'" + fromF + "' and '" + toF + "' have no common ancestor" );
        }

        int commonIndex = Math.min ( toPath.size (), fromPath.size () ) - 1;
        boolean found = false;
        while ( commonIndex >= 0 && !found )
        {
            found = fromPath.get ( commonIndex ).equals ( toPath.get ( commonIndex ) );
            if ( !found )
            {
                commonIndex--;
            }
        }

        // on remonte jusqu'à l'ancêtre commun
        final List<String> complete = new ArrayList<String> ( Collections.nCopies ( fromPath.size () - 1 - commonIndex, ".." ) );
        if ( complete.isEmpty () )
        {
            complete.add ( "." );
        }
        // puis on descend vers 'to'
        for ( final File f : toPath.subList ( commonIndex + 1, toPath.size () ) )
        {
            complete.add ( f.getName () );
        }

        return CollectionUtils.join ( complete, File.separator );
    }

    // return each ancestor of f (including itself)
    // eg [/, /folder, /folder/dir] for /folder/dir
    public final static List<File> getAncestors ( final File f )
    {
        final List<File> path = new ArrayList<File> ();
        File currentF = f;
        while ( currentF != null )
        {
            path.add ( 0, currentF );
            currentF = currentF.getParentFile ();
        }
        return path;
    }

    public final static File addSuffix ( final File f, final String suffix )
    {
        return new File ( f.getParentFile (), f.getName () + suffix );
    }

    // ** shell

    /**
     * Behave like the 'mv' unix utility, ie handle cross filesystems mv and <code>dest</code>
     * being a directory.
     * 
     * @param f the source file.
     * @param dest the destination file or directory.
     * @return the error or <code>null</code> if there was none.
     */
    public static String mv ( final File f, final File dest )
    {
        final File canonF;
        File canonDest;
        try
        {
            canonF = f.getCanonicalFile ();
            canonDest = dest.getCanonicalFile ();
        }
        catch ( final IOException e )
        {
            return ExceptionUtils.getStackTrace ( e );
        }
        if ( canonF.equals ( canonDest ) )
        {
            // nothing to do
            return null;
        }
        if ( canonDest.isDirectory () )
        {
            canonDest = new File ( canonDest, canonF.getName () );
        }

        final File destF;
        if ( canonDest.exists () )
        {
            return canonDest + " exists";
        }
        else if ( !canonDest.getParentFile ().exists () )
        {
            return "parent of " + canonDest + " does not exist";
        }
        else
        {
            destF = canonDest;
        }
        if ( !canonF.renameTo ( destF ) )
        {
            try
            {
                copyDirectory ( canonF, destF );
                if ( destF.exists () )
                {
                    rmR ( canonF );
                }
            }
            catch ( final IOException e )
            {
                return ExceptionUtils.getStackTrace ( e );
            }
        }
        return null;
    }

    public static void copyFile ( final File in, final File out ) throws IOException
    {
        final FileChannel sourceChannel = new FileInputStream ( in ).getChannel ();
        final FileChannel destinationChannel = new FileOutputStream ( out ).getChannel ();
        sourceChannel.transferTo ( 0, sourceChannel.size (), destinationChannel );
        sourceChannel.close ();
        destinationChannel.close ();
    }

    public static void copyDirectory ( final File in, final File out ) throws IOException
    {
        if ( in.isDirectory () )
        {
            if ( !out.exists () )
            {
                out.mkdir ();
            }

            final String[] children = in.list ();
            for ( int i = 0; i < children.length; i++ )
            {
                copyDirectory ( new File ( in, children[i] ), new File ( out, children[i] ) );
            }
        }
        else
        {
            if ( !in.getName ().equals ( "Thumbs.db" ) )
            {
                copyFile ( in, out );
            }
        }
    }

    /**
     * Delete recursively the passed directory. If a deletion fails, the method stops attempting to
     * delete and returns false.
     * 
     * @param dir the dir to be deleted.
     * @return <code>true</code> if all deletions were successful.
     */
    public static boolean rmR ( final File dir )
    {
        if ( dir.isDirectory () )
        {
            final File[] children = dir.listFiles ();
            for ( int i = 0; i < children.length; i++ )
            {
                final boolean success = rmR ( children[i] );

                if ( !success )
                {

                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete ();
    }

    public static final File mkdir_p ( final File dir ) throws IOException
    {
        if ( !dir.exists () )
        {
            if ( !dir.mkdirs () )
            {
                throw new IOException ( "cannot create directory " + dir );
            }
        }
        return dir;
    }

    // **io

    /**
     * Read a file line by line with the default encoding and returns the concatenation of these.
     * 
     * @param f the file to read.
     * @return the content of f.
     * @throws IOException if a pb occur while reading.
     */
    public static final String read ( final File f ) throws IOException
    {
        return read ( f, null );
    }

    /**
     * Read a file line by line and returns the concatenation of these.
     * 
     * @param f the file to read.
     * @param charset the encoding of <code>f</code>, <code>null</code> means default encoding.
     * @return the content of f.
     * @throws IOException if a pb occur while reading.
     */
    public static final String read ( final File f, final String charset ) throws IOException
    {
        return read ( new FileInputStream ( f ), charset );
    }

    public static final String read ( final InputStream ins, final String charset ) throws IOException
    {
        final Reader reader;
        if ( charset == null )
        {
            reader = new InputStreamReader ( ins );
        }
        else
        {
            reader = new InputStreamReader ( ins, charset );
        }
        return read ( reader );
    }

    public static final String read ( final Reader reader ) throws IOException
    {
        final BufferedReader in = new BufferedReader ( reader );
        String line;
        String res = "";
        while ( ( line = in.readLine () ) != null )
        {
            res += line + "\n";
        }
        in.close ();
        return res;
    }

    /**
     * Read the whole content of a file.
     * 
     * @param f the file to read.
     * @return its content.
     * @throws IOException if a pb occur while reading.
     * @throws IllegalArgumentException if f is longer than <code>Integer.MAX_VALUE</code>.
     */
    public static final byte[] readBytes ( final File f ) throws IOException
    {
        // no need for a Buffer since we read everything at once
        final InputStream in = new FileInputStream ( f );
        if ( f.length () > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException ( "file longer than Integer.MAX_VALUE" + f.length () );
        }
        final byte[] res = new byte[(int)f.length ()];
        in.read ( res );
        in.close ();
        return res;
    }

    public static void write ( final String s, final File f ) throws IOException
    {
        write ( s, f, false );
    }

    public static void write ( final String s, final File f, final boolean append ) throws IOException
    {
        final BufferedWriter w = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( f, append ) ) );
        try
        {
            w.write ( s );
        }
        finally
        {
            w.close ();
        }
    }

    /**
     * Execute the passed transformer with the lock on the passed file.
     * 
     * @param <T> return type.
     * @param f the file to lock.
     * @param transf what to do on the file.
     * @return what <code>transf</code> returns.
     * @throws Exception if an error occurs.
     */
    public static final <T> T doWithLock ( final File f, final ExnTransformer<RandomAccessFile, T, ?> transf ) throws Exception
    {
        // don't use FileOutputStream : it truncates the file on creation
        RandomAccessFile out = null;
        try
        {
            mkdir_p ( f.getParentFile () );
            // we need write to obtain lock
            out = new RandomAccessFile ( f, "rw" );
            out.getChannel ().lock ();
            final T res = transf.transformChecked ( out );
            // this also release the lock
            out.close ();
            out = null;
            return res;
        }
        catch ( final Exception e )
        {
            // if anything happens, try to close
            // don't use finally{close()} otherwise if it raise an exception
            // the original error is discarded
            Exception toThrow = e;
            if ( out != null )
            {
                try
                {
                    out.close ();
                }
                catch ( final IOException e2 )
                {
                    // too bad, just add the error
                    toThrow = ExceptionUtils.createExn ( IOException.class, "couldn't close: " + e2.getMessage (), e );
                }
            }
            throw toThrow;
        }
    }

    /**
     * Tries to open the passed file as if it were graphicaly opened by the current user (respect
     * user's "open with"). If a native way to open the file can't be found, tries the passed list
     * of executables.
     * 
     * @param f the file to open.
     * @param executables a list of exectuables to try, eg ["ooffice", "soffice"].
     * @throws IOException if the file can't be opened.
     */
    public static final void open ( final File f, final String[] executables ) throws IOException
    {
        try
        {
            openNative ( f );
        }
        catch ( final IOException exn )
        {
            for ( int i = 0; i < executables.length; i++ )
            {
                final String executable = executables[i];
                try
                {
                    Runtime.getRuntime ().exec ( new String[] { executable, f.getCanonicalPath () } );
                    return;
                }
                catch ( final IOException e )
                {
                    // try the next one
                }
            }
            throw new IOException ( "unable to open " + f + " with: " + Arrays.asList ( executables ) );
        }
    }

    /**
     * Open the passed file as if it were graphicaly opened by the current user (user's "open
     * with").
     * 
     * @param f the file to open.
     * @throws IOException if f couldn't be opened.
     */
    private static final void openNative ( final File f ) throws IOException
    {
        final String os = System.getProperty ( "os.name" );
        if ( os.startsWith ( "Windows" ) )
        {
            Runtime.getRuntime ().exec ( new String[] { "cmd", "/c", "start", "\"\"", f.getCanonicalPath () } );
        }
        else if ( os.startsWith ( "Mac OS" ) )
        {
            Runtime.getRuntime ().exec ( new String[] { "open", f.getCanonicalPath () } );
        }
        else if ( os.startsWith ( "Linux" ) && gnomeRunning () )
        {
            Runtime.getRuntime ().exec ( new String[] { "gnome-open", f.getCanonicalPath () } );
        }
        else
        {
            throw new IOException ( "unknown way to open " + f );
        }
    }

    private static final boolean gnomeRunning ()
    {
        try
        {
            return Runtime.getRuntime ().exec ( new String[] { "pgrep", "-u", System.getProperty ( "user.name" ), "nautilus" } ).waitFor () == 0;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private static final Map<String, String> ext2mime;
    static
    {
        ext2mime = new HashMap<String, String> ();
        ext2mime.put ( ".xml", "text/xml" );
        ext2mime.put ( ".jpg", "image/jpeg" );
        ext2mime.put ( ".png", "image/png" );
        ext2mime.put ( ".tiff", "image/tiff" );
    }

    /**
     * Try to guess the media type of the passed file name (see <a
     * href="http://www.iana.org/assignments/media-types">iana</a>).
     * 
     * @param fname a file name.
     * @return its mime type.
     */
    public static final String findMimeType ( final String fname )
    {
        for ( final Map.Entry<String, String> e : ext2mime.entrySet () )
        {
            if ( fname.toLowerCase ().endsWith ( e.getKey () ) )
            {
                return e.getValue ();
            }
        }
        return null;
    }

    public static final FileFilter DIR_FILTER = new FileFilter () {
        public boolean accept ( final File f )
        {
            return f.isDirectory ();
        }
    };

    public static final FileFilter REGULAR_FILE_FILTER = new FileFilter () {
        public boolean accept ( final File f )
        {
            return f.isFile ();
        }
    };

    /**
     * Return a filter that select regular files ending in <code>ext</code>.
     * 
     * @param ext the end of the name, eg ".xml".
     * @return the corresponding filter.
     */
    public static final FileFilter createEndFileFilter ( final String ext )
    {
        return new FileFilter () {

            public boolean accept ( final File f )
            {
                return f.isFile () && f.getName ().endsWith ( ext );
            }
        };
    }
}
