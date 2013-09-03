/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.adminweb;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertFalse;


public class TranslationReaderTest
{
    @Test
    public void testTranslate()
        throws Exception
    {
        Map<String, String> translations = new HashMap<String, String>();
        translations.put( "%__example__%", "alert" );

        final String xslFilename = getClass().getName().replace( '.', '/' ) + ".xsl";
        final InputStream xslInputStream = getClass().getClassLoader().getResourceAsStream( xslFilename );
        final Reader reader = new InputStreamReader( xslInputStream, Charset.forName( "UTF-8" ) );
        final TranslationReader translationReader = new TranslationReader( translations, reader );
        final int length = xslInputStream.available();
        final char[] xslBuffer = new char[length];
        translationReader.read( xslBuffer, 0, length );
        final String xslString = new String( xslBuffer );
        assertFalse( "The XSL contains untranslated string", xslString.contains( "%__example__%" ) );
    }
}
