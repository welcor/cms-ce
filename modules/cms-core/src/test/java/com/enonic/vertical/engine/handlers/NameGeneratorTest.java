/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.engine.handlers;

import junit.framework.TestCase;

import static com.enonic.vertical.engine.handlers.NameGenerator.transcribeName;

public class NameGeneratorTest
    extends TestCase
{

    public void testCreateLatinUsernameFromDiacritics()
        throws Exception
    {
        assertEquals( "jorundvierskriubakken", transcribeName( "J\u00f8rund Vier Skriubakken" ) ); // ø
    }

    // for encoding use native2ascii -encoding utf-8 file.txt ,
    // where file.txt is saved in utf-8 !
    public void testCreateLatinUsernameFromCyrillic()
        throws Exception
    {
        // Василий Щукин
        assertEquals( "vasilijschukin", transcribeName( "\u0412\u0430\u0441\u0438\u043b\u0438\u0439 \u0429\u0443\u043a\u0438\u043d" ) );
    }

    // for encoding use native2ascii -encoding utf-8 file.txt ,
    // where file.txt is saved in utf-8 !
    public void testCreateLatinUsernameFromGreek()
        throws Exception
    {
        char[] characters = new char[25];

        for ( int i = 0; i < characters.length; i++ )
        {
            characters[i] = (char) ( i + 0x391 );   // small alpha
        }

        // alpha beta ...
        assertEquals( "abgdezethiclmnxoprstyphchpso", transcribeName( new String( characters ) ) );

        for ( int i = 0; i < characters.length; i++ )
        {
            characters[i] = (char) ( i + 0x3B1 );   // big alpha
        }

        // alpha beta ...
        assertEquals( "abgdezethiclmnxoprstyphchpso", transcribeName( new String( characters ) ) );
    }

    public void testCreateLatinUsernameFromDiacriticsAZ()
        throws Exception
    {
        byte[] characters = new byte[128];

        for ( int i = 0; i < 128; i++ )
        {
            characters[i] = (byte) ( i + 128 );
        }

        assertEquals( "szszycaaaaaaceeeeiiiidnooooouuuuythssaaaaaaaeceeeeiiiidnoooooouuuuythy",
                      transcribeName( new String( characters, "cp1252" ) ) );
    }

    public void testCreateLatinUsernameFromCyrillicAZ()
        throws Exception
    {
        byte[] characters = new byte[128];

        for ( int i = 0; i < 128; i++ )
        {
            characters[i] = (byte) ( i + 128 );
        }

        assertEquals(
            "gjgjljnjkjdjljnjkjdjujujjjgjjocyeyiiigjjoyejjdzdzyiabvgdezhzijklmnoprstufhcchshschyehjujaabvgdezhzijklmnoprstufhcchshschyehjuja",
            transcribeName( new String( characters, "cp1251" ) ) );
    }

    public void testCreateLatinUsernameFromEmptyString()
        throws Exception
    {
        final String user = transcribeName( "" );
        assertTrue( user.matches( "user\\d{4}" ) );
    }

}
