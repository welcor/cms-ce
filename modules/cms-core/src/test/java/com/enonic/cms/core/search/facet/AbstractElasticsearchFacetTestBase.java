/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.facet.AbstractFacetBuilder;

public abstract class AbstractElasticsearchFacetTestBase
{
    public String getJson( AbstractFacetBuilder facetBuilder )
        throws Exception
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        facetBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
        builder.endObject();

        return builder.string();
    }

}
