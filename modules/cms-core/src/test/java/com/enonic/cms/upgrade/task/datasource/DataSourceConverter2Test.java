package com.enonic.cms.upgrade.task.datasource;

import org.junit.Test;

public class DataSourceConverter2Test
    extends AbstractDataSourceConverterTest
{
    public DataSourceConverter2Test()
    {
        super( new DataSourceConverter2() );
    }

    @Test
    public void testConvert_none()
        throws Exception
    {
        testConvert( "task2/none" );
    }

    @Test
    public void testConvert_context()
        throws Exception
    {
        testConvert( "task2/context" );
    }

    @Test
    public void testConvert_getLocales()
        throws Exception
    {
        testConvert( "task2/getLocales" );
    }

    @Test
    public void testConvert_getTimeZones()
        throws Exception
    {
        testConvert( "task2/getTimeZones" );
    }

    @Test
    public void testConvert_getPreferences()
        throws Exception
    {
        testConvert( "task2/getPreferences" );
    }

    @Test
    public void testConvert_getUserStore()
        throws Exception
    {
        testConvert( "task2/getUserStore" );
    }

    @Test
    public void testConvert_getCountries()
        throws Exception
    {
        testConvert( "task2/getCountries" );
    }

    @Test
    public void testConvert_getCalendar()
        throws Exception
    {
        testConvert( "task2/getCalendar" );
    }

    @Test
    public void testConvert_getContentVersion()
        throws Exception
    {
        testConvert( "task2/getContentVersion" );
    }

    @Test
    public void testConvert_getUrlAsText()
        throws Exception
    {
        testConvert( "task2/getUrlAsText" );
    }

    @Test
    public void testConvert_getUrlAsXml()
        throws Exception
    {
        testConvert( "task2/getUrlAsXml" );
    }
}

