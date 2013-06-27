/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.builder;

import java.util.Set;

import org.elasticsearch.search.facet.AbstractFacetBuilder;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.facet.model.DateHistogramFacetModel;
import com.enonic.cms.core.search.facet.model.FacetModel;
import com.enonic.cms.core.search.facet.model.FacetsModel;
import com.enonic.cms.core.search.facet.model.HistogramFacetModel;
import com.enonic.cms.core.search.facet.model.RangeFacetModel;
import com.enonic.cms.core.search.facet.model.TermsFacetModel;
import com.enonic.cms.core.search.facet.model.TermsStatsFacetModel;

public class ElasticsearchFacetBuilder
{
    private final ElasticsearchTermsFacetBuilder termFacetBuilder = new ElasticsearchTermsFacetBuilder();

    private final ElasticsearchRangeFacetBuilder rangeFacetBuilder = new ElasticsearchRangeFacetBuilder();

    private final ElasticsearchHistogramFacetBuilder histogramFacetBuilder = new ElasticsearchHistogramFacetBuilder();

    private final ElasticsearchDateHistogramFacetBuilder dateHistogramFacetBuilder = new ElasticsearchDateHistogramFacetBuilder();

    private final ElasticsearchTermsStatsFacetBuilder termsStatsFacetBuilder = new ElasticsearchTermsStatsFacetBuilder();

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
            else if ( model instanceof RangeFacetModel )
            {
                facetBuilders.add( rangeFacetBuilder.build( (RangeFacetModel) model ) );
            }
            else if ( model instanceof HistogramFacetModel )
            {
                facetBuilders.add( histogramFacetBuilder.build( (HistogramFacetModel) model ) );
            }
            else if ( model instanceof DateHistogramFacetModel )
            {
                facetBuilders.add( dateHistogramFacetBuilder.build( (DateHistogramFacetModel) model ) );
            }
            else if ( model instanceof TermsStatsFacetModel )
            {
                facetBuilders.add( termsStatsFacetBuilder.build( (TermsStatsFacetModel) model ) );
            }
        }

        return facetBuilders;
    }
}
