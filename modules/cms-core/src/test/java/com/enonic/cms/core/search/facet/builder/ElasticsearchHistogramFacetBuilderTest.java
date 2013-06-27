/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.histogram.HistogramFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.facet.AbstractElasticsearchFacetTestBase;
import com.enonic.cms.core.search.facet.model.HistogramFacetModel;

import static org.junit.Assert.*;

public class ElasticsearchHistogramFacetBuilderTest
    extends AbstractElasticsearchFacetTestBase
{

    @Test
    public void field()
        throws Exception
    {
        ElasticsearchHistogramFacetBuilder facetBuilder = new ElasticsearchHistogramFacetBuilder();

        String expectedJson = "{\"myHistogramFacet\":{\"histogram\":{\"field\":\"myfield.number\",\"interval\":100}}}";

        HistogramFacetModel model = new HistogramFacetModel();
        model.setName( "myHistogramFacet" );
        model.setIndex( "myField" );
        model.setCount( 10 );

        model.setInterval( 100L );

        final HistogramFacetBuilder histogramFacetBuilder = facetBuilder.build( model );

        final String json = getJson( histogramFacetBuilder );

        assertEquals( expectedJson, json );
    }

    @Test
    public void key_valuefield()
        throws Exception
    {
        ElasticsearchHistogramFacetBuilder facetBuilder = new ElasticsearchHistogramFacetBuilder();

        String expectedJson =
            "{\"myHistogramFacet\":{\"histogram\":{\"key_field\":\"data_mykeyfield.number\",\"value_field\":\"data_myvaluefield.number\",\"interval\":100}}}";

        HistogramFacetModel model = new HistogramFacetModel();
        model.setName( "myHistogramFacet" );
        model.setCount( 10 );
        model.setInterval( 100L );
        model.setIndex( "data/myKeyField" );
        model.setValueIndex( "data/myValueField" );

        final HistogramFacetBuilder histogramFacetBuilder = facetBuilder.build( model );

        final String json = getJson( histogramFacetBuilder );

        assertEquals( expectedJson, json );
    }


}
