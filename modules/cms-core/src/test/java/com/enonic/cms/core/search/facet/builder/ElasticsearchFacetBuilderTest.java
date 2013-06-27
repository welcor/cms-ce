/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.builder;

import java.util.Set;

import org.elasticsearch.search.facet.AbstractFacetBuilder;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.facet.model.FacetsModel;
import com.enonic.cms.core.search.facet.model.TermsFacetModel;

import static org.junit.Assert.*;

public class ElasticsearchFacetBuilderTest
{

    private ElasticsearchFacetBuilder elasticsearchFacetBuilder = new ElasticsearchFacetBuilder();

    @Test
    public void testEmptyFacetsModel()
    {
        FacetsModel facetsModel = new FacetsModel();
        final Set<AbstractFacetBuilder> build = elasticsearchFacetBuilder.build( facetsModel );
        assertEquals( 0, build.size() );
    }

    @Test
    public void testCreateTermFacetBuilder()
    {
        FacetsModel facetsModel = new FacetsModel();

        TermsFacetModel termFacetModel = new TermsFacetModel();
        termFacetModel.setName( "test" );
        termFacetModel.setIndexes( "test" );

        facetsModel.addFacet( termFacetModel );

        final Set<AbstractFacetBuilder> build = elasticsearchFacetBuilder.build( facetsModel );

        assertEquals( 1, build.size() );
        final AbstractFacetBuilder next = build.iterator().next();
        assertTrue( next instanceof TermsFacetBuilder );
    }


}
