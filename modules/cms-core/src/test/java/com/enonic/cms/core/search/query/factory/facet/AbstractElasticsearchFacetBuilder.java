package com.enonic.cms.core.search.query.factory.facet;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.search.facet.AbstractFacetBuilder;

public class AbstractElasticsearchFacetBuilder
{
    public String getJson( AbstractFacetBuilder facetBuilder )
        throws Exception
    {
        final XContentBuilder builder = JsonXContent.contentBuilder();
        builder.startObject();
        facetBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
        builder.endObject();

        return builder.string();
    }

}
