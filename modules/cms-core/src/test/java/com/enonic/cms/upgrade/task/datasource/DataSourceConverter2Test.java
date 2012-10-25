package com.enonic.cms.upgrade.task.datasource;

import org.junit.Test;

public class DataSourceConverter2Test
    extends AbstractDataSourceConverterTest
{
    public DataSourceConverter2Test()
    {
        super( new DataSourceConverter2( 0, null ) );
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
    public void testConvert_extension()
        throws Exception
    {
        testConvert( "task2/extension" );
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

    @Test
    public void testConvert_getFormattedDate()
        throws Exception
    {
        testConvert( "task2/getFormattedDate" );
    }

    @Test
    public void testConvert_getIndexValues()
        throws Exception
    {
        testConvert( "task2/getIndexValues" );
    }

    @Test
    public void testConvert_getAggregatedIndexValues()
        throws Exception
    {
        testConvert( "task2/getAggregatedIndexValues" );
    }

    @Test
    public void testConvert_getMenu()
        throws Exception
    {
        testConvert( "task2/getMenu" );
    }

    @Test
    public void testConvert_getMenuData()
        throws Exception
    {
        testConvert( "task2/getMenuData" );
    }

    @Test
    public void testConvert_getMenuItem()
        throws Exception
    {
        testConvert( "task2/getMenuItem" );
    }

    @Test
    public void testConvert_getSubMenu()
        throws Exception
    {
        testConvert( "task2/getSubMenu" );
    }

    @Test
    public void testConvert_getMenuBranch()
        throws Exception
    {
        testConvert( "task2/getMenuBranch" );
    }

    @Test
    public void testConvert_getSuperCategoryNames()
        throws Exception
    {
        testConvert( "task2/getSuperCategoryNames" );
    }

    @Test
    public void testConvert_getContentByQuery()
        throws Exception
    {
        testConvert( "task2/getContentByQuery" );
    }

    @Test
    public void testConvert_getRelatedContent()
        throws Exception
    {
        testConvert( "task2/getRelatedContent" );
    }

    @Test
    public void testConvert_getRandomContentBySection()
        throws Exception
    {
        testConvert( "task2/getRandomContentBySection" );
    }

    @Test
    public void testConvert_getRandomContentByCategory()
        throws Exception
    {
        testConvert( "task2/getRandomContentByCategory" );
    }

    @Test
    public void testConvert_getContentByCategory()
        throws Exception
    {
        testConvert( "task2/getContentByCategory" );
    }
}
