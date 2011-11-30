package com.enonic.cms.core.search.query;

import java.util.Set;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.search.ContentSearchQuery;

public abstract class QueryTranslatorBaseTest
{

    private QueryTranslator queryTranslator = new QueryTranslator();

    public ContentSearchQuery createContentQuery()
    {
        ContentSearchQuery query = new ContentSearchQuery();

        return query;
    }

    public ContentSearchQuery createContentQuery( String queryString )
    {
        ContentSearchQuery query = new ContentSearchQuery();
        query.setQuery( queryString );

        return query;
    }

    public ContentSearchQuery createContentQuery( int from, int count, String queryString )
    {
        ContentSearchQuery query = new ContentSearchQuery();
        query.setQuery( queryString );
        query.setFrom( from );
        query.setCount( count );

        return query;
    }

    public ContentSearchQuery createContentQuery( String queryString, Set<CategoryKey> categoryFilter )
    {
        ContentSearchQuery query = createContentQuery( queryString );
        query.setCategoryFilter( categoryFilter );

        return query;
    }

    public ContentSearchQuery createContentQuery( String queryString, Set<CategoryKey> categoryFilter,
                                                  Set<ContentTypeKey> contentTypeFilter )
    {
        ContentSearchQuery query = createContentQuery( queryString, categoryFilter );
        query.setContentTypeFilter( contentTypeFilter );

        return query;
    }

    public ContentSearchQuery createContentQuery( Set<CategoryKey> categoryFilter,
                                                  Set<ContentTypeKey> contentTypeFilter )
    {
        ContentSearchQuery query = createContentQuery();
        query.setCategoryFilter( categoryFilter );
        query.setContentTypeFilter( contentTypeFilter );

        return query;
    }

    public ContentSearchQuery createContentQuery( int from, int count, String queryString,
                                                  Set<CategoryKey> categoryFilter,
                                                  Set<ContentTypeKey> contentTypeFilter )
    {
        ContentSearchQuery query = createContentQuery( queryString, categoryFilter, contentTypeFilter );
        query.setFrom( from );
        query.setCount( count );

        return query;
    }


    public QueryTranslator getQueryTranslator()
    {
        return queryTranslator;
    }
}
