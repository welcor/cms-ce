package com.enonic.cms.core.portal.datasource2.xml;

import java.net.URL;
import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import com.enonic.cms.core.portal.datasource2.DataSourceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DataSourceXmlParserTest
{
    @Test
    public void testParse_empty1()
        throws Exception
    {
        final DataSourcesElement result = parse( "empty1" );
        assertNotNull( result );
        assertNull( result.getResultElement() );
        assertFalse( result.isHttpContext() );
        assertFalse( result.isCookieContext() );
        assertFalse( result.isSessionContext() );
        assertTrue( result.getList().isEmpty() );
    }

    @Test
    public void testParse_empty2()
        throws Exception
    {
        final DataSourcesElement result = parse( "empty2" );
        assertNotNull( result );
        assertEquals( "dummy", result.getResultElement() );
        assertTrue( result.isHttpContext() );
        assertTrue( result.isCookieContext() );
        assertTrue( result.isSessionContext() );
        assertTrue( result.getList().isEmpty() );
    }

    @Test
    public void testParse_simple1()
        throws Exception
    {
        final DataSourcesElement result = parse( "simple1" );
        assertNotNull( result );

        final List<DataSourceElement> list = result.getList();
        assertNotNull( list );
        assertEquals( 1, list.size() );

        final DataSourceElement element = list.get( 0 );
        assertNotNull( element );
        assertEquals( "dummy", element.getName() );
        assertEquals( false, element.isCache() );
        assertNull( element.getResultElement() );
        assertNull( element.getCondition() );
        assertTrue( element.getParameters().isEmpty() );
    }

    @Test
    public void testParse_simple2()
        throws Exception
    {
        final DataSourcesElement result = parse( "simple2" );
        assertNotNull( result );

        final List<DataSourceElement> list = result.getList();
        assertNotNull( list );
        assertEquals( 1, list.size() );

        final DataSourceElement element = list.get( 0 );
        assertNotNull( element );
        assertEquals( "dummy", element.getName() );
        assertEquals( true, element.isCache() );
        assertEquals( "result", element.getResultElement() );
        assertEquals( "true()", element.getCondition() );
        assertTrue( element.getParameters().isEmpty() );
    }

    @Test
    public void testParse_params1()
        throws Exception
    {
        final DataSourcesElement result = parse( "params1" );

        final DataSourceElement element = result.getList().get( 0 );
        assertNotNull( element );
        assertEquals( "dummy", element.getName() );

        final List<ParameterElement> params = element.getParameters();
        assertNotNull( params );
        assertEquals( 1, params.size() );

        final ParameterElement param1 = params.get( 0 );
        assertNotNull( param1 );
        assertEquals( "param1", param1.getName() );
        assertEquals( "one", param1.getValue() );
    }

    @Test
    public void testParse_params2()
        throws Exception
    {
        final DataSourcesElement result = parse( "params2" );

        final DataSourceElement element = result.getList().get( 0 );
        assertNotNull( element );
        assertEquals( "dummy", element.getName() );

        final List<ParameterElement> params = element.getParameters();
        assertNotNull( params );
        assertEquals( 2, params.size() );

        final ParameterElement param1 = params.get( 0 );
        assertNotNull( param1 );
        assertEquals( "param1", param1.getName() );
        assertEquals( "one", param1.getValue() );

        final ParameterElement param2 = params.get( 1 );
        assertNotNull( param2 );
        assertEquals( "param2", param2.getName() );
        assertEquals( "<custom><xml /></custom>", param2.getValue() );
    }

    @Test
    public void testParse_multiple()
        throws Exception
    {
        final DataSourcesElement result = parse( "multiple" );

        final List<DataSourceElement> list = result.getList();
        assertNotNull( list );
        assertEquals( 3, list.size() );

        final DataSourceElement element1 = list.get( 0 );
        assertNotNull( element1 );
        assertEquals( "dummy1", element1.getName() );
        assertTrue( element1.getParameters().isEmpty() );

        final DataSourceElement element2 = list.get( 1 );
        assertNotNull( element2 );
        assertEquals( "dummy2", element2.getName() );
        assertNotNull( element2.getParameters() );
        assertEquals( 1, element2.getParameters().size() );

        final DataSourceElement element3 = list.get( 2 );
        assertNotNull( element3 );
        assertEquals( "dummy3", element3.getName() );
        assertNotNull( element3.getParameters() );
        assertEquals( 2, element3.getParameters().size() );
    }

    @Test(expected = DataSourceException.class)
    public void testParse_illegal()
        throws Exception
    {
        parse( "illegal" );
    }

    private DataSourcesElement parse( final String name )
        throws Exception
    {
        return new DataSourceXmlParser().parse( readDoc( name + ".xml" ) );
    }

    private Document readDoc( final String name )
        throws Exception
    {
        final URL url = getClass().getResource( name );
        assertNotNull( "Document [" + name + "] not found", url );

        final SAXBuilder builder = new SAXBuilder();
        return builder.build( url );
    }
}
