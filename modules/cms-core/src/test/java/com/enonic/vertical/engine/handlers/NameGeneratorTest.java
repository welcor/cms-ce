package com.enonic.vertical.engine.handlers;

import junit.framework.TestCase;

import static com.enonic.vertical.engine.handlers.NameGenerator.simplifyString;

public class NameGeneratorTest
    extends TestCase
{

    public void testSimplifyStringLatin()
        throws Exception
    {
        assertEquals( "jorundvierskriubakken", simplifyString( "J\u00f8rund Vier Skriubakken" ) ); // ø
    }

    // for encoding use native2ascii -encoding utf-8 file.txt ,
    // where file.txt is saved in utf-8 !
    public void testSimplifyStringCyrillic()
        throws Exception
    {
        // Василий Щукин
        assertEquals( "vasilijschukin", simplifyString( "\u0412\u0430\u0441\u0438\u043b\u0438\u0439 \u0429\u0443\u043a\u0438\u043d" ) );
    }

    public void testSimplifyStringLatinAZ()
        throws Exception
    {
        byte[] characters = new byte[128];

        for ( int i = 0; i < 128; i++ )
        {
            characters[i] = (byte) ( i + 128 );
        }

        assertEquals( "szszycaaaaaaceeeeiiiidnooooouuuuythssaaaaaaaeceeeeiiiidnoooooouuuuythy",
                      simplifyString( new String( characters, "cp1252" ) ) );
    }

    public void testSimplifyStringCyrillicAZ()
        throws Exception
    {
        byte[] characters = new byte[128];

        for ( int i = 0; i < 128; i++ )
        {
            characters[i] = (byte) ( i + 128 );
        }

        assertEquals(
            "gjgjljnjkjdjljnjkjdjujujjjgjjocyeyiiigjjoyejjdzdzyiabvgdezhzijklmnoprstufhcchshschyehjujaabvgdezhzijklmnoprstufhcchshschyehjuja",
            simplifyString( new String( characters, "cp1251" ) ) );
    }


}
