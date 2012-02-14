package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/14/12
 * Time: 1:00 PM
 */
public class ContentIndexServiceImplTest_wildcards
    extends ContentIndexServiceTestBase
{


    @Test
    public void testWildcardQuery_userdata_numeric()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "data/* = 38" );

        ContentResultSet resultSet = contentIndexService.query( query );

        assertEquals( 1, resultSet.getTotalCount() );
    }


    @Test
    public void testWildcardQuery_userdata_string()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "data/* = '38'" );

        ContentResultSet resultSet = contentIndexService.query( query );

        assertEquals( 1, resultSet.getTotalCount() );
    }


    @Test
    public void testWildcardQuery_title_not_in_all_query()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "data/* = 'Homer'" );

        ContentResultSet resultSet = contentIndexService.query( query );

        assertEquals( 0, resultSet.getTotalCount() );
    }




}
