package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

public class ContentIndexServiceImpl_facetTermsFacetTest
    extends ContentIndexServiceTestBase
{


    @Test
    public void simple_querying()
    {
        // Setup standard values
        setUpStandardTestValues();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacetDefinition( "<facets>\n" +
                                      "    <terms name=\"mySimpleTermFacet\">\n" +
                                      "        <field>data/person/gender</field>\n" +
                                      "    </terms>\n" +
                                      "</facets>" );

        final ContentResultSet result = contentIndexService.query( query );
    }


}
