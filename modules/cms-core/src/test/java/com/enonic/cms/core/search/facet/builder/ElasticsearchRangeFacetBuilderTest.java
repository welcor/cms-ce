package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.range.RangeFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.facet.AbstractElasticsearchFacetTestBase;
import com.enonic.cms.core.search.facet.model.FacetRange;
import com.enonic.cms.core.search.facet.model.FacetRanges;
import com.enonic.cms.core.search.facet.model.RangeFacetModel;

public class ElasticsearchRangeFacetBuilderTest
    extends AbstractElasticsearchFacetTestBase
{

    @Test
    public void testBuildRangeFacet()
        throws Exception
    {
        ElasticsearchRangeFacetBuilder facetBuilder = new ElasticsearchRangeFacetBuilder();

        RangeFacetModel model = new RangeFacetModel();
        model.setName( "rangeFacet" );
        model.setField( "myDateField" );
        model.setSize( 10 );

        FacetRanges ranges = new FacetRanges();

        ranges.addFacetRange( new FacetRange( "0", "9" ) );
        ranges.addFacetRange( new FacetRange( "10", "19" ) );
        ranges.addFacetRange( new FacetRange( "20", "29" ) );

        model.setFacetRanges( ranges );

        final RangeFacetBuilder build = facetBuilder.build( model );

        System.out.println( getJson( build ) );

    }


}
