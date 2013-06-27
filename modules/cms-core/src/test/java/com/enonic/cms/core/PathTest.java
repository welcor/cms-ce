/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class PathTest
{
    @Test
    public void testCreate()
    {
        Path emptyPath = new Path( "" );
        assertEquals( "", emptyPath.toString() );

        Path slashPath = new Path( "/" );
        assertEquals( "/", slashPath.toString() );

        assertEquals( "/nyheter/innenriks", new Path( "/nyheter/innenriks" ).toString() );
    }

    @Test
    public void testCreateWithFragment()
    {
        Path path = new Path( "/nyheter/innenriks#bottom" );

        assertEquals( "/nyheter/innenriks#bottom", path.toString() );
        assertEquals( "/nyheter/innenriks#bottom", path.getPathAsString() );
        assertEquals( 25, path.length() );
    }

    @Test
    public void testGetFragment()
    {
        assertEquals( "bottom", new Path( "/nyheter/innenriks#bottom" ).getFragment() );
        assertNull( new Path( "/nyheter/innenriks" ).getFragment() );
    }

    @Test
    public void testHasFragment()
    {
        assertFalse( new Path( "/nyheter/innenriks" ).hasFragment() );
        assertTrue( new Path( "/nyheter/innenriks#bottom" ).hasFragment() );
    }

    @Test
    public void testCreatePathWithQuestionMarkAllowed()
    {
        Path emptyPath = new Path( "Hjelp/Reparere bilen?" );
        assertEquals( "Hjelp/Reparere bilen?", emptyPath.toString() );
    }

    @Test
    public void testNumberOfElements()
    {

        assertEquals( 0, new Path( "" ).numberOfElements() );
        assertEquals( 0, new Path( "/" ).numberOfElements() );
        assertEquals( 1, new Path( "/nyheter" ).numberOfElements() );
        assertEquals( 1, new Path( "nyheter" ).numberOfElements() );
        assertEquals( 2, new Path( "/nyheter/innenriks" ).numberOfElements() );
        assertEquals( 2, new Path( "/nyheter/innenriks/" ).numberOfElements() );
    }

    @Test
    public void testAppendPath()
    {

        assertEquals( "nyheter", new Path( "" ).appendPath( new Path( "nyheter" ) ).toString() );
        assertEquals( "/nyheter", new Path( "/" ).appendPath( new Path( "nyheter" ) ).toString() );
        assertEquals( "/nyheter/innenriks/oppland", new Path( "/nyheter/innenriks" ).appendPath( new Path( "oppland" ) ).toString() );
        assertEquals( "/nyheter/innenriks/oppland", new Path( "/nyheter/innenriks" ).appendPath( new Path( "/oppland" ) ).toString() );
        assertEquals( "nyheter/innenriks/oppland", new Path( "nyheter/innenriks" ).appendPath( new Path( "/oppland" ) ).toString() );
        assertEquals( "nyheter/innenriks/oppland", new Path( "nyheter/innenriks" ).appendPath( new Path( "oppland" ) ).toString() );
    }

    @Test
    public void testSubtractPath()
    {

        assertEquals( "images/image.gif", new Path( "/innenriks/oslo/images/image.gif" ).subtractPath( "/innenriks/oslo/" ).toString() );
        assertEquals( "/images/image.gif", new Path( "/innenriks/oslo/images/image.gif" ).subtractPath( "/innenriks/oslo" ).toString() );
        assertEquals( "/images/image.gif", new Path( "innenriks/oslo/images/image.gif" ).subtractPath( "innenriks/oslo" ).toString() );

        assertEquals( "/utenriks/images/image.gif", new Path( "/utenriks/images/image.gif" ).subtractPath( "/ballerusk" ).toString() );
        assertEquals( "", new Path( "/utenriks/images/image.gif" ).subtractPath( "/utenriks/images/image.gif" ).toString() );
    }

    @Test
    public void testCointains()
    {
        assertTrue( new Path( "/innenriks/oslo/images/image.gif" ).contains( "/innenriks/oslo/" ) );
        assertFalse( new Path( "/innenriks/oslo/images/image.gif" ).contains( "/innenriks/bergen/" ) );
        assertFalse( new Path( "/innenriks/oslo/images/image.gif#bottom" ).contains( "bottom" ) );
        assertFalse( new Path( Lists.newArrayList( "image.gif" ), true, "#bottom" ).contains( "bottom" ) );
    }

    @Test
    public void testEndsWitth()
    {
        assertFalse( new Path( "/mypage#/page" ).endsWith( "/page" ) );
    }

    @Test
    public void testGetAsUrlEncoded()
        throws UnsupportedEncodingException
    {

        Path path;
        String encoded;

        path = new Path( URLDecoder.decode( "/J%C3%B8rund", "UTF-8" ) );
        encoded = path.getAsUrlEncoded( false, "UTF-8" );
        assertEquals( "/J%C3%B8rund", encoded );

        path = new Path( URLDecoder.decode( "/J%C3%B8rund/", "UTF-8" ) );
        encoded = path.getAsUrlEncoded( false, "UTF-8" );
        assertEquals( "/J%C3%B8rund/", encoded );

        path = new Path( URLDecoder.decode( "/J%C3%B8rund/Vier/Skriubakken", "UTF-8" ) );
        encoded = path.getAsUrlEncoded( false, "UTF-8" );
        assertEquals( "/J%C3%B8rund/Vier/Skriubakken", encoded );

        path = new Path( URLDecoder.decode( "/J%C3%B8rund/Vier/Skriubakken/", "UTF-8" ) );
        encoded = path.getAsUrlEncoded( false, "UTF-8" );
        assertEquals( "/J%C3%B8rund/Vier/Skriubakken/", encoded );
    }

    @Test
    public void testGetAsUrlEncodedWithFragment()
        throws UnsupportedEncodingException
    {
        Path path;
        String encoded;

        path = new Path( "/Runar/tester/test#fragment" );
        encoded = path.getAsUrlEncoded( true, "UTF-8" );
        assertEquals( "/Runar/tester/test#fragment", encoded );

        path = new Path( "/Runar/tester/test#fragment" );
        encoded = path.getAsUrlEncoded( false, "UTF-8" );
        assertEquals( "/Runar/tester/test", encoded );

        path = new Path( "/Runar/tester/test/" + URLDecoder.decode( "/J%C3%B8rund", "UTF-8" ) + "#fragment" );
        encoded = path.getAsUrlEncoded( true, "UTF-8" );
        assertEquals( "/Runar/tester/test/J%C3%B8rund#fragment", encoded );

        path =
            new Path( "/Runar/tester/test/" + URLDecoder.decode( "/J%C3%B8rund", "UTF-8" ) + "#" + URLDecoder.decode( "%C3%B8", "UTF-8" ) );
        encoded = path.getAsUrlEncoded( true, "UTF-8" );
        assertEquals( "/Runar/tester/test/J%C3%B8rund#%C3%B8", encoded );


    }

    @Test
    public void testSubPath()
    {
        Path path = new Path( "/nyheter/innenriks/oslo" );

        assertEquals( "nyheter/innenriks/oslo", path.subPath( 0, 3 ) );

        assertEquals( "innenriks", path.subPath( 1, 2 ) );

        assertEquals( "innenriks/oslo", path.subPath( 1, 3 ) );

        assertEquals( "oslo", path.subPath( 2, 3 ) );
    }

    @Test
    public void testIndexOf()
    {
        assertEquals( -1, new Path( "oldpage" ).indexOf( "contentpage" ) );

        assertEquals( 0, new Path( "oldpage" ).indexOf( "oldpage" ) );

        assertEquals( 2, new Path( "/nyheter/innenriks/oslo" ).indexOf( "oslo" ) );
        assertEquals( 1, new Path( "/nyheter/innenriks/oslo" ).indexOf( "innenriks" ) );
        assertEquals( 0, new Path( "/nyheter/innenriks/oslo" ).indexOf( "nyheter" ) );
    }

    @Test
    public void testGetLastPathElement()
    {
        assertEquals( "news", new Path( "/home/news" ).getLastPathElement() );
        assertEquals( "news", new Path( "/home/news/" ).getLastPathElement() );

        assertEquals( "home", new Path( "/home" ).getLastPathElement() );
        assertEquals( "home", new Path( "/home/" ).getLastPathElement() );
    }

    @Test
    public void testAppendPathElement()
    {
        assertEquals( "/home/news/politics", new Path( "/home/news" ).appendPathElement( "politics" ).getPathAsString() );
        assertEquals( "/home/news/politics", new Path( "/home/news/" ).appendPathElement( "politics" ).getPathAsString() );
    }

    @Test
    public void testAppendPathElementOnPathWithFragment()
    {
        final Path home_news_with_fragment = new Path( "/home/news#fragment" );
        final Path actualPath = home_news_with_fragment.appendPathElement( "politics" );

        assertEquals( "fragment", actualPath.getFragment() );
        assertEquals( "/home/news/politics#fragment", actualPath.getPathAsString() );
        assertEquals( "/home/news/politics", actualPath.getPathWithoutFragmentAsString() );
    }

    @Test
    public void testSubractLastPathElement()
    {
        assertEquals( "/home/news", new Path( "/home/news/politics" ).substractLastPathElement().getPathAsString() );
        assertEquals( "home/news", new Path( "home/news/politics" ).substractLastPathElement().getPathAsString() );
    }

    @Test
    public void testSubractLastPathElementFromPathWithFragment()
    {
        final Path path = new Path( "/home/news/politics#fragment" );

        final Path actualPath = path.substractLastPathElement();
        assertEquals( "fragment", actualPath.getFragment() );
        assertEquals( "/home/news#fragment", actualPath.getPathAsString() );
    }

    @Test
    public void testGetPathElementsDoNotContainFragment()
    {
        final Path path = new Path( "/home/news/politics#fragment" );

        final String actualLastPathElement = path.getLastPathElement();
        assertEquals( "politics", actualLastPathElement );
    }

    @Test
    public void testRemovedTrailingSlash()
    {
        Path pathWithSlashAtEnd = new Path( "/home/news/politics/" );
        assertTrue( pathWithSlashAtEnd.endsWithSlash() );

        Path pathWithRemovedTrailingSlash = pathWithSlashAtEnd.removeTrailingSlash();

        assertEquals( new Path( "/home/news/politics" ), pathWithRemovedTrailingSlash );
        assertEquals( "/home/news/politics", pathWithRemovedTrailingSlash.toString() );
    }

    @Test
    public void testRemovingTrailingSlashFromPathWithoutTrailingSlashChangesNothing()
    {
        Path pathWithoutSlashAtEnd = new Path( "/home/news/politics" );
        assertFalse( pathWithoutSlashAtEnd.endsWithSlash() );

        Path pathWithRemovedTrailingSlash = pathWithoutSlashAtEnd.removeTrailingSlash();

        assertEquals( pathWithoutSlashAtEnd, pathWithRemovedTrailingSlash );
        assertEquals( "/home/news/politics", pathWithRemovedTrailingSlash.toString() );
    }

    @Test
    public void testRemovingTrailingSlashFromPathWithFragmentButWithoutTrailingSlashChangesNothing()
    {
        Path pathWithoutSlashAtEnd = new Path( "/home/news/politics#myfragment" );
        assertFalse( pathWithoutSlashAtEnd.endsWithSlash() );

        Path pathWithRemovedTrailingSlash = pathWithoutSlashAtEnd.removeTrailingSlash();

        assertEquals( pathWithoutSlashAtEnd, pathWithRemovedTrailingSlash );
        assertEquals( pathWithoutSlashAtEnd.getFragment(), pathWithRemovedTrailingSlash.getFragment() );
        assertEquals( pathWithoutSlashAtEnd.getPathAsString(), pathWithRemovedTrailingSlash.getPathAsString() );
        assertEquals( "/home/news/politics#myfragment", pathWithRemovedTrailingSlash.toString() );
    }

    @Test
    public void testRemovedTrailingSlashWhenPathContainsFragment()
    {
        Path pathWithSlashAtEnd = new Path( "/home/news/politics/#myfragment" );
        assertTrue( pathWithSlashAtEnd.endsWithSlash() );

        Path pathWithRemovedTrailingSlash = pathWithSlashAtEnd.removeTrailingSlash();

        assertFalse( pathWithRemovedTrailingSlash.endsWithSlash() );
        assertEquals( new Path( "/home/news/politics#myfragment" ), pathWithRemovedTrailingSlash );
        assertEquals( "/home/news/politics#myfragment", pathWithRemovedTrailingSlash.toString() );
    }

    @Test
    public void testEnforcePathStartsWithSlash()
    {
        Path path = new Path( "en/home", true );
        assertEquals( "/en/home", path.getPathAsString() );
    }

    @Test
    public void containsSubPath()
    {
        assertTrue( new Path( "/en/_itrace/resources/my.gif" ).containsSubPath( "_itrace", "resources" ) );
        assertTrue( new Path( "/en/_itrace/resources/" ).containsSubPath( "_itrace", "resources" ) );
        assertTrue( new Path( "/_itrace/resources/my.gif" ).containsSubPath( "_itrace", "resources" ) );
        assertTrue( new Path( "/_itrace/resources" ).containsSubPath( "_itrace", "resources" ) );
        assertFalse( new Path( "/_itrace/" ).containsSubPath( "_itrace", "resources" ) );
        assertFalse( new Path( "/" ).containsSubPath( "_itrace", "resources" ) );

        assertTrue( new Path( "/en/_itrace/resources/my.gif" ).containsSubPath( "_itrace" ) );
        assertTrue( new Path( "/_itrace/resources/my.gif" ).containsSubPath( "_itrace" ) );
        assertTrue( new Path( "/_itrace/resources" ).containsSubPath( "_itrace" ) );
        assertTrue( new Path( "/_itrace/" ).containsSubPath( "_itrace" ) );
        assertFalse( new Path( "/" ).containsSubPath( "_itrace" ) );
    }

    @Test
    public void getPathWithoutFragmentAsString()
    {
        Path path = new Path( Lists.newArrayList( "my", "path" ), true, "fragment" );
        assertEquals( "/my/path", path.getPathWithoutFragmentAsString() );
    }

    @Test
    public void getPathElementAfter()
    {
        assertEquals( "123", new Path( "/en/home/_itrace/info/123/" ).getPathElementAfter( "_itrace", "info" ) );
        assertEquals( "123", new Path( "/en/home/_itrace/info/123" ).getPathElementAfter( "_itrace", "info" ) );
        assertEquals( null, new Path( "/en/home/_itrace/info/" ).getPathElementAfter( "_itrace", "info" ) );
        assertEquals( null, new Path( "/en/home/_itrace/info" ).getPathElementAfter( "_itrace", "info" ) );
        assertEquals( null, new Path( "/non/matching/path" ).getPathElementAfter( "_itrace", "info" ) );
        assertEquals( null, new Path( "/" ).getPathElementAfter( "_itrace", "info" ) );
        assertEquals( null, new Path( "" ).getPathElementAfter( "_itrace", "info" ) );
    }
}


