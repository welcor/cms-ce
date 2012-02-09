package com.enonic.cms.itest.search;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/9/12
 * Time: 12:27 PM
 */
public class ContentIndexServiceImplTest_indexValueQuery
    extends ContentIndexServiceTestBase
{

    @Test
    public void testStuff()
    {
        setUpStandardTestValues();

        //printAllIndexContent();

        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 100,\n" +
            "\"fields\" : [\"title\"],\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchResponse result = contentIndexService.query( termQuery );

        for ( SearchHit hit : result.getHits() )
        {
            final Map<String, SearchHitField> fields = hit.getFields();

            System.out.println( "ID: " + hit.getId() );

            for ( String field : fields.keySet() )
            {
                System.out.println( "Field: " + field + " - Value: " + fields.get( field ).value() );
            }

        }

    }


}
