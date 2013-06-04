/* jcifs smb client library in Java
 * Copyright (C) 2002  "Michael B. Allen" <jcifs at samba dot org>
 *                  "Eric Glass" <jcifs at samba dot org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package jcifs.smb;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Random;

import jcifs.Config;
import jcifs.util.DES;
import jcifs.util.HMACT64;
import jcifs.util.LogStream;

import org.bouncycastle.crypto.digests.MD4Digest;

/**
 * This class stores and encrypts NTLM user credentials. The default
 * credentials are retrieved from the <tt>jcifs.smb.client.domain</tt>,
 * <tt>jcifs.smb.client.username</tt>, and <tt>jcifs.smb.client.password</tt>
 * properties.
 * <p>
 * Read <a href="../../../authhandler.html">jCIFS Exceptions and
 * NtlmAuthenticator</a> for related information.
 */

public final class NtlmPasswordAuthentication implements Principal, Serializable
{

    private static final int LM_COMPATIBILITY = Config.getInt ( "jcifs.smb.lmCompatibility", 0 );

    private static final Random RANDOM = new Random ();

    private static LogStream log = LogStream.getInstance ();

    // KGS!@#$%
    private static final byte[] S8 = { (byte)0x4b, (byte)0x47, (byte)0x53, (byte)0x21, (byte)0x40, (byte)0x23, (byte)0x24, (byte)0x25 };

    private static void E ( final byte[] key, final byte[] data, final byte[] e )
    {
        final byte[] key7 = new byte[7];
        final byte[] e8 = new byte[8];

        for ( int i = 0; i < key.length / 7; i++ )
        {
            System.arraycopy ( key, i * 7, key7, 0, 7 );
            final DES des = new DES ( key7 );
            des.encrypt ( data, e8 );
            System.arraycopy ( e8, 0, e, i * 8, 8 );
        }
    }

    static String DEFAULT_DOMAIN;

    static String DEFAULT_USERNAME;

    static String DEFAULT_PASSWORD;

    static final String BLANK = "";

    static void initDefaults ()
    {
        if ( DEFAULT_DOMAIN != null )
        {
            return;
        }
        DEFAULT_DOMAIN = Config.getProperty ( "jcifs.smb.client.domain", "?" );
        DEFAULT_USERNAME = Config.getProperty ( "jcifs.smb.client.username", "GUEST" );
        DEFAULT_PASSWORD = Config.getProperty ( "jcifs.smb.client.password", BLANK );
    }

    /**
     * Generate the ANSI DES hash for the password associated with these
     * credentials.
     */
    static public byte[] getPreNTLMResponse ( final String password, final byte[] challenge )
    {
        final byte[] p14 = new byte[14];
        final byte[] p21 = new byte[21];
        final byte[] p24 = new byte[24];
        byte[] passwordBytes;
        try
        {
            passwordBytes = password.toUpperCase ().getBytes ( SmbConstants.OEM_ENCODING );
        }
        catch ( final UnsupportedEncodingException uee )
        {
            throw new RuntimeException ( "Try setting jcifs.encoding=US-ASCII", uee );
        }
        int passwordLength = passwordBytes.length;

        // Only encrypt the first 14 bytes of the password for Pre 0.12 NT LM
        if ( passwordLength > 14 )
        {
            passwordLength = 14;
        }
        System.arraycopy ( passwordBytes, 0, p14, 0, passwordLength );
        E ( p14, S8, p21 );
        E ( p21, challenge, p24 );
        return p24;
    }

    /**
     * Generate the Unicode MD4 hash for the password associated with these
     * credentials.
     */
    static public byte[] getNTLMResponse ( final String password, final byte[] challenge )
    {
        byte[] uni = null;
        final byte[] p21 = new byte[21];
        final byte[] p24 = new byte[24];

        try
        {
            uni = password.getBytes ( "UnicodeLittleUnmarked" );
        }
        catch ( final UnsupportedEncodingException uee )
        {
            if ( LogStream.level > 0 )
            {
                uee.printStackTrace ( log );
            }
        }

        final MD4Digest md4 = new MD4Digest ();

        md4.update ( uni, 0, uni.length );
        try
        {
            md4.doFinal ( p21, 0 );
        }
        catch ( final Exception ex )
        {
            if ( LogStream.level > 0 )
            {
                ex.printStackTrace ( log );
            }
        }
        E ( p21, challenge, p24 );
        return p24;
    }

    /**
     * Creates the LMv2 response for the supplied information.
     * 
     * @param domain
     *            The domain in which the username exists.
     * @param user
     *            The username.
     * @param password
     *            The user's password.
     * @param challenge
     *            The server challenge.
     * @param clientChallenge
     *            The client challenge (nonce).
     */
    public static byte[] getLMv2Response ( final String domain, final String user, final String password, final byte[] challenge, final byte[] clientChallenge )
    {
        try
        {
            final byte[] hash = new byte[16];
            final byte[] response = new byte[24];

            final MD4Digest md4 = new MD4Digest ();
            final byte[] bytes = password.getBytes ( "UnicodeLittleUnmarked" );
            md4.update ( bytes, 0, bytes.length );
            final byte[] digest = new byte[md4.getDigestSize ()];
            md4.doFinal ( digest, 0 );

            HMACT64 hmac = new HMACT64 ( digest );
            hmac.update ( user.toUpperCase ().getBytes ( "UnicodeLittleUnmarked" ) );
            hmac.update ( domain.toUpperCase ().getBytes ( "UnicodeLittleUnmarked" ) );
            hmac = new HMACT64 ( hmac.digest () );
            hmac.update ( challenge );
            hmac.update ( clientChallenge );
            hmac.digest ( response, 0, 16 );
            System.arraycopy ( clientChallenge, 0, response, 16, 8 );
            return response;
        }
        catch ( final Exception ex )
        {
            if ( LogStream.level > 0 )
            {
                ex.printStackTrace ( log );
            }
            return null;
        }
    }

    static final NtlmPasswordAuthentication NULL = new NtlmPasswordAuthentication ( "", "", "" );

    static final NtlmPasswordAuthentication GUEST = new NtlmPasswordAuthentication ( "?", "GUEST", "" );

    static final NtlmPasswordAuthentication DEFAULT = new NtlmPasswordAuthentication ( null );

    String domain;

    String username;

    String password;

    byte[] ansiHash;

    byte[] unicodeHash;

    boolean hashesExternal = false;

    byte[] clientChallenge = null;

    byte[] challenge = null;

    /**
     * Create an <tt>NtlmPasswordAuthentication</tt> object from the userinfo
     * component of an SMB URL like "<tt>domain;user:pass</tt>". This
     * constructor
     * is used internally be jCIFS when parsing SMB URLs.
     */

    public NtlmPasswordAuthentication ( String userInfo )
    {
        this.domain = this.username = this.password = null;

        if ( userInfo != null )
        {
            try
            {
                userInfo = unescape ( userInfo );
            }
            catch ( final UnsupportedEncodingException uee )
            {
            }
            int i, u, end;
            char c;

            end = userInfo.length ();
            for ( i = 0, u = 0; i < end; i++ )
            {
                c = userInfo.charAt ( i );
                if ( c == ';' )
                {
                    this.domain = userInfo.substring ( 0, i );
                    u = i + 1;
                }
                else if ( c == ':' )
                {
                    this.password = userInfo.substring ( i + 1 );
                    break;
                }
            }
            this.username = userInfo.substring ( u, i );
        }

        initDefaults ();

        if ( this.domain == null )
        {
            this.domain = DEFAULT_DOMAIN;
        }
        if ( this.username == null )
        {
            this.username = DEFAULT_USERNAME;
        }
        if ( this.password == null )
        {
            this.password = DEFAULT_PASSWORD;
        }
    }

    /**
     * Create an <tt>NtlmPasswordAuthentication</tt> object from a
     * domain, username, and password. Parameters that are <tt>null</tt> will be
     * substituted with <tt>jcifs.smb.client.domain</tt>,
     * <tt>jcifs.smb.client.username</tt>, <tt>jcifs.smb.client.password</tt>
     * property values.
     */
    public NtlmPasswordAuthentication ( final String domain, final String username, final String password )
    {
        this.domain = domain;
        this.username = username;
        this.password = password;

        initDefaults ();

        if ( domain == null )
        {
            this.domain = DEFAULT_DOMAIN;
        }
        if ( username == null )
        {
            this.username = DEFAULT_USERNAME;
        }
        if ( password == null )
        {
            this.password = DEFAULT_PASSWORD;
        }
    }

    /**
     * Create an <tt>NtlmPasswordAuthentication</tt> object with raw password
     * hashes. This is used exclusively by the <tt>jcifs.http.NtlmSsp</tt> class
     * which is in turn used by NTLM HTTP authentication functionality.
     */
    public NtlmPasswordAuthentication ( final String domain, final String username, final byte[] challenge, final byte[] ansiHash, final byte[] unicodeHash )
    {
        if ( domain == null || username == null || ansiHash == null || unicodeHash == null )
        {
            throw new IllegalArgumentException ( "External credentials cannot be null" );
        }
        this.domain = domain;
        this.username = username;
        this.password = null;
        this.challenge = challenge;
        this.ansiHash = ansiHash;
        this.unicodeHash = unicodeHash;
        this.hashesExternal = true;
    }

    /**
     * Returns the domain.
     */
    public String getDomain ()
    {
        return this.domain;
    }

    /**
     * Returns the username.
     */
    public String getUsername ()
    {
        return this.username;
    }

    /**
     * Returns the password in plain text or <tt>null</tt> if the raw password
     * hashes were used to construct this <tt>NtlmPasswordAuthentication</tt>
     * object which will be the case when NTLM HTTP Authentication is
     * used. There is no way to retrieve a users password in plain text unless
     * it is supplied by the user at runtime.
     */
    public String getPassword ()
    {
        return this.password;
    }

    /**
     * Return the domain and username in the format: <tt>domain\\username</tt>.
     * This is equivalent to <tt>toString()</tt>.
     */
    @Override
    public String getName ()
    {
        final boolean d = this.domain.length () > 0 && this.domain.equals ( "?" ) == false;
        return d ? this.domain + "\\" + this.username : this.username;
    }

    /**
     * Computes the 24 byte ANSI password hash given the 8 byte server
     * challenge.
     */
    public byte[] getAnsiHash ( final byte[] challenge )
    {
        if ( this.hashesExternal )
        {
            return this.ansiHash;
        }
        switch ( LM_COMPATIBILITY )
        {
            case 0:
            case 1:
                return getPreNTLMResponse ( this.password, challenge );
            case 2:
                return getNTLMResponse ( this.password, challenge );
            case 3:
            case 4:
            case 5:
                if ( this.clientChallenge == null )
                {
                    this.clientChallenge = new byte[8];
                    RANDOM.nextBytes ( this.clientChallenge );
                }
                return getLMv2Response ( this.domain, this.username, this.password, challenge, this.clientChallenge );
            default:
                return getPreNTLMResponse ( this.password, challenge );
        }
    }

    /**
     * Computes the 24 byte Unicode password hash given the 8 byte server
     * challenge.
     */
    public byte[] getUnicodeHash ( final byte[] challenge )
    {
        if ( this.hashesExternal )
        {
            return this.unicodeHash;
        }
        switch ( LM_COMPATIBILITY )
        {
            case 0:
            case 1:
            case 2:
                return getNTLMResponse ( this.password, challenge );
            case 3:
            case 4:
            case 5:
                /*
                if( clientChallenge == null ) {
                    clientChallenge = new byte[8];
                    RANDOM.nextBytes( clientChallenge );
                }
                return getNTLMv2Response(domain, username, password, null,
                        challenge, clientChallenge);
                */
                return new byte[0];
            default:
                return getNTLMResponse ( this.password, challenge );
        }
    }

    /**
     * Returns the effective user session key.
     * 
     * @param challenge
     *            The server challenge.
     * @return A <code>byte[]</code> containing the effective user session key,
     *         used in SMB MAC signing and NTLMSSP signing and sealing.
     */
    public byte[] getUserSessionKey ( final byte[] challenge )
    {
        if ( this.hashesExternal )
        {
            return null;
        }
        final byte[] key = new byte[16];
        try
        {
            getUserSessionKey ( challenge, key, 0 );
        }
        catch ( final Exception ex )
        {
            if ( LogStream.level > 0 )
            {
                ex.printStackTrace ( log );
            }
        }
        return key;
    }

    /**
     * Calculates the effective user session key.
     * 
     * @param challenge
     *            The server challenge.
     * @param dest
     *            The destination array in which the user session key will be
     *            placed.
     * @param offset
     *            The offset in the destination array at which the
     *            session key will start.
     */
    void getUserSessionKey ( final byte[] challenge, final byte[] dest, final int offset ) throws Exception
    {
        if ( this.hashesExternal )
        {
            return;
        }
        final MD4Digest md4 = new MD4Digest ();
        final byte[] bytes = this.password.getBytes ( "UnicodeLittleUnmarked" );
        md4.update ( bytes, 0, bytes.length );
        switch ( LM_COMPATIBILITY )
        {
            case 0:
            case 1:
            case 2:
                redigest ( dest, offset, md4 );
                break;
            case 3:
            case 4:
            case 5:
                if ( this.clientChallenge == null )
                {
                    this.clientChallenge = new byte[8];
                    RANDOM.nextBytes ( this.clientChallenge );
                }

                final byte[] digest = new byte[md4.getDigestSize ()];
                md4.doFinal ( digest, 0 );
                HMACT64 hmac = new HMACT64 ( digest );
                hmac.update ( this.username.toUpperCase ().getBytes ( "UnicodeLittleUnmarked" ) );
                hmac.update ( this.domain.toUpperCase ().getBytes ( "UnicodeLittleUnmarked" ) );
                final byte[] ntlmv2Hash = hmac.digest ();
                hmac = new HMACT64 ( ntlmv2Hash );
                hmac.update ( challenge );
                hmac.update ( this.clientChallenge );
                final HMACT64 userKey = new HMACT64 ( ntlmv2Hash );
                userKey.update ( hmac.digest () );
                userKey.digest ( dest, offset, 16 );
                break;
            default:
                redigest ( dest, offset, md4 );
                break;
        }
    }

    private void redigest ( final byte[] dest, final int offset, final MD4Digest md4 )
    {
        final byte[] digest = new byte[md4.getDigestSize ()];
        md4.doFinal ( digest, 0 );
        md4.reset ();
        md4.update ( digest, 0, digest.length );
        md4.doFinal ( digest, 0 );
        System.arraycopy ( digest, 0, dest, offset, 16 );
    }

    /**
     * Compares two <tt>NtlmPasswordAuthentication</tt> objects for
     * equality. Two <tt>NtlmPasswordAuthentication</tt> objects are equal if
     * their caseless domain and username fields are equal and either both
     * hashes are external and they are equal or both internally supplied
     * passwords are equal. If one <tt>NtlmPasswordAuthentication</tt> object
     * has external hashes (meaning negotiated via NTLM HTTP Authentication) and
     * the other does not they will not be equal. This is technically not
     * correct however the server 8 byte challage would be required to compute
     * and compare the password hashes but that it not available with this
     * method.
     */
    @Override
    public boolean equals ( final Object obj )
    {
        if ( obj instanceof NtlmPasswordAuthentication )
        {
            final NtlmPasswordAuthentication ntlm = (NtlmPasswordAuthentication)obj;
            if ( ntlm.domain.toUpperCase ().equals ( this.domain.toUpperCase () ) && ntlm.username.toUpperCase ().equals ( this.username.toUpperCase () ) )
            {
                if ( this.hashesExternal && ntlm.hashesExternal )
                {
                    return Arrays.equals ( this.ansiHash, ntlm.ansiHash ) && Arrays.equals ( this.unicodeHash, ntlm.unicodeHash );
                    /* This still isn't quite right. If one npa object does not have external
                     * hashes and the other does then they will not be considered equal even
                     * though they may be.
                     */
                }
                else if ( !this.hashesExternal && this.password.equals ( ntlm.password ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the upcased username hash code.
     */
    @Override
    public int hashCode ()
    {
        return getName ().toUpperCase ().hashCode ();
    }

    /**
     * Return the domain and username in the format: <tt>domain\\username</tt>.
     * This is equivalent to <tt>getName()</tt>.
     */
    @Override
    public String toString ()
    {
        return getName ();
    }

    static String unescape ( final String str ) throws NumberFormatException, UnsupportedEncodingException
    {
        char ch;
        int i, j, state, len;
        char[] out;
        final byte[] b = new byte[1];

        if ( str == null )
        {
            return null;
        }

        len = str.length ();
        out = new char[len];
        state = 0;
        for ( i = j = 0; i < len; i++ )
        {
            switch ( state )
            {
                case 0:
                    ch = str.charAt ( i );
                    if ( ch == '%' )
                    {
                        state = 1;
                    }
                    else
                    {
                        out[j++] = ch;
                    }
                    break;
                case 1:
                    /* Get ASCII hex value and convert to platform dependant
                     * encoding like EBCDIC perhaps
                     */
                    b[0] = (byte) ( Integer.parseInt ( str.substring ( i, i + 2 ), 16 ) & 0xFF );
                    out[j++] = new String ( b, 0, 1, "ASCII" ).charAt ( 0 );
                    i++;
                    state = 0;
            }
        }

        return new String ( out, 0, j );
    }

}
