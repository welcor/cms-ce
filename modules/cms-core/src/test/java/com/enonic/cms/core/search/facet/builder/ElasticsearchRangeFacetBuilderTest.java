package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.range.RangeFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.facet.AbstractElasticsearchFacetTestBase;
import com.enonic.cms.core.search.facet.model.FacetRange;
import com.enonic.cms.core.search.facet.model.FacetRanges;
import com.enonic.cms.core.search.facet.model.RangeFacetModel;

import static org.junit.Assert.*;

public class ElasticsearchRangeFacetBuilderTest
    extends AbstractElasticsearchFacetTestBase
{
    @Test
    public void field()
        throws Exception
    {
        ElasticsearchRangeFacetBuilder facetBuilder = new ElasticsearchRangeFacetBuilder();

        String expectedJson =
            "{\"rangeFacet\":{\"range\":{\"field\":\"mydatefield.number\",\"ranges\":[{\"from\":\"0.0\",\"to\":\"9.0\"},{\"from\":\"10.0\",\"to\":\"19.0\"},{\"from\":\"20.0\",\"to\":\"29.0\"}]}}}";

        RangeFacetModel model = new RangeFacetModel();
        model.setName( "rangeFacet" );
        model.setIndex( "myDateField" );
        model.setSize( 10 );

        model.addFacetRange( new FacetRange( "0", "9" ) );
        model.addFacetRange( new FacetRange( "10", "19" ) );
        model.addFacetRange( new FacetRange( "20", "29" ) );


        final RangeFacetBuilder build = facetBuilder.build( model );

        final String json = getJson( build );

        assertEquals( expectedJson, json );
    }

    @Test
    public void key_field()
        throws Exception
    {
        ElasticsearchRangeFacetBuilder facetBuilder = new ElasticsearchRangeFacetBuilder();

        String expectedJson =
            "{\"rangeFacet\":{\"range\":{\"key_field\":\"keyField\",\"value_field\":\"valueField\",\"ranges\":[{\"from\":\"0.0\",\"to\":\"9.0\"},{\"from\":\"10.0\",\"to\":\"19.0\"},{\"from\":\"20.0\",\"to\":\"29.0\"}]}}}";

        RangeFacetModel model = new RangeFacetModel();
        model.setName( "rangeFacet" );
        model.setKeyField( "keyField" );
        model.setValueField( "valueField" );
        model.setSize( 10 );

        model.addFacetRange( new FacetRange( "0", "9" ) );
        model.addFacetRange( new FacetRange( "10", "19" ) );
        model.addFacetRange( new FacetRange( "20", "29" ) );

        final RangeFacetBuilder build = facetBuilder.build( model );

        final String json = getJson( build );

        assertEquals( expectedJson, json );
    }

}
