/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource;

import org.junit.Test;

public class DataSourceConverterUpgradeModel206Test
    extends AbstractDataSourceConverterTest
{
    public DataSourceConverterUpgradeModel206Test()
    {
        super( new DataSourceConverterUpgradeModel206( null ) );
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

    @Test
    public void testSpecialCharacter()
        throws Exception
    {
        testConvert( "task1/special_character" );
    }
}
