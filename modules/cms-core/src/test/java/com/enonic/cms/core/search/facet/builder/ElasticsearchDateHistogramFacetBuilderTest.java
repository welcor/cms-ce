package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.datehistogram.DateHistogramFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.facet.AbstractElasticsearchFacetTestBase;
import com.enonic.cms.core.search.facet.model.DateHistogramFacetModel;

import static org.junit.Assert.*;

public class ElasticsearchDateHistogramFacetBuilderTest
    extends AbstractElasticsearchFacetTestBase
{

    @Test
    public void field()
        throws Exception
    {
        ElasticsearchDateHistogramFacetBuilder facetBuilder = new ElasticsearchDateHistogramFacetBuilder();

        String expectedJson =
            "{\"myDateHistogramFacet\":{\"date_histogram\":{\"field\":\"myfield.date\",\"interval\":\"1.5d\",\"pre_zone\":\"Europe/Oslo\",\"pre_zone_adjust_large_interval\":true}}}";

        DateHistogramFacetModel model = new DateHistogramFacetModel();
        model.setName( "myDateHistogramFacet" );
        model.setIndex( "myField" );
        model.setCount( 10 );

        model.setInterval( "1.5d" );

        final DateHistogramFacetBuilder dateHistogramFacetBuilder = facetBuilder.build( model );

        final String json = getJson( dateHistogramFacetBuilder );

        assertEquals( expectedJson, json );
    }

}
