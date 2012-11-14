package com.enonic.cms.core.search.facet.builder;

import java.util.Set;

import org.elasticsearch.search.facet.AbstractFacetBuilder;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.facet.model.FacetModel;
import com.enonic.cms.core.search.facet.model.FacetsModel;
import com.enonic.cms.core.search.facet.model.TermsFacetModel;

public class ElasticsearchFacetBuilder
{
    ElasticsearchTermsFacetBuilder termFacetBuilder = new ElasticsearchTermsFacetBuilder();

    public Set<AbstractFacetBuilder> build( FacetsModel facetsModel )
    {
        Set<AbstractFacetBuilder> facetBuilders = Sets.newHashSet();

        final Set<FacetModel> facetModels = facetsModel.getFacetModels();

        for ( FacetModel model : facetModels )
        {
            if ( model instanceof TermsFacetModel )
            {
                facetBuilders.add( termFacetBuilder.build( (TermsFacetModel) model ) );
            }
        }

        return facetBuilders;
    }
}
