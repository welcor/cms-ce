/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet;

import java.util.Set;

import org.elasticsearch.search.facet.AbstractFacetBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.facet.builder.ElasticsearchFacetBuilder;
import com.enonic.cms.core.search.facet.model.FacetsModel;
import com.enonic.cms.core.search.facet.model.FacetsModelFactory;

public class FacetBuilderFactory
{
    private final FacetsModelFactory facetsModelFactory = new FacetsModelFactory();

    private final ElasticsearchFacetBuilder facetModelEsFacetBuilder = new ElasticsearchFacetBuilder();

    public Set<AbstractFacetBuilder> buildFacetBuilder( ContentIndexQuery query )
    {
        Set<AbstractFacetBuilder> facetBuilders = Sets.newHashSet();

        String xml = query.getFacets();

        if ( Strings.isNullOrEmpty( xml ) )
        {
            return facetBuilders;
        }

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        return facetModelEsFacetBuilder.build( facetsModel );
    }

}
