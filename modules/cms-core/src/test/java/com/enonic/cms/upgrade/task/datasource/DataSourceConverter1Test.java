package com.enonic.cms.upgrade.task.datasource;

import org.junit.Test;

public class DataSourceConverter1Test
    extends AbstractDataSourceConverterTest
{
    public DataSourceConverter1Test()
    {
        super( new DataSourceConverter1() );
    }

    @Test
    public void testConvert_none()
        throws Exception
    {
        testConvert( "task1/none" );
    }

    @Test
    public void testConvert_simple()
        throws Exception
    {
        testConvert( "task1/simple" );
    }

    @Test
    public void testConvert_case()
        throws Exception
    {
        testConvert( "task1/case" );
    }

    @Test
    public void testConvert_override()
        throws Exception
    {
        testConvert( "task1/override" );
    }

    @Test
    public void testConvert_multiple()
        throws Exception
    {
        testConvert( "task1/multiple" );
    }
}
