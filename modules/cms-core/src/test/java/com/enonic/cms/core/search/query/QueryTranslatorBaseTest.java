package com.enonic.cms.core.search.query;

import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

import static junit.framework.Assert.assertEquals;


public abstract class QueryTranslatorBaseTest
{
    private QueryTranslator queryTranslator = new QueryTranslator();

    protected final static int QUERY_DEFAULT_SIZE = Integer.MAX_VALUE;


    public ContentIndexQuery createContentQuery( String queryString )
    {
        ContentIndexQuery query = doCreateContentIndexQuery( queryString );

        return query;
    }

    private ContentIndexQuery doCreateContentIndexQuery( String queryString )
    {
        return new ContentIndexQuery( queryString );
    }

    public ContentIndexQuery createContentQuery( int from, int count, String queryString )
    {
        ContentIndexQuery query = doCreateContentIndexQuery( queryString );
        query.setIndex( from );
        query.setCount( count );

        return query;
    }

    public ContentIndexQuery createContentQuery( Set<MenuItemEntity> sectionFilter )
    {
        ContentIndexQuery query = createContentQuery( "" );
        query.setSectionFilter( sectionFilter, ContentIndexQuery.SectionFilterStatus.ANY );

        return query;
    }

    public ContentIndexQuery createContentQuery( String queryString, Set<CategoryKey> categoryFilter )
    {
        ContentIndexQuery query = createContentQuery( queryString );
        query.setCategoryFilter( categoryFilter );

        return query;
    }

    public ContentIndexQuery createContentQuery( String queryString, Set<CategoryKey> categoryFilter,
                                                 Set<ContentTypeKey> contentTypeFilter )
    {
        ContentIndexQuery query = createContentQuery( queryString, categoryFilter );
        query.setContentTypeFilter( contentTypeFilter );

        return query;
    }

    public ContentIndexQuery createContentQuery( Set<CategoryKey> categoryFilter, Set<ContentTypeKey> contentTypeFilter )
    {
        ContentIndexQuery query = createContentQuery( "" );
        query.setCategoryFilter( categoryFilter );
        query.setContentTypeFilter( contentTypeFilter );

        return query;
    }

    public ContentIndexQuery createContentQueryContentFilter( Set<ContentKey> contentFilter )
    {
        ContentIndexQuery query = createContentQuery( "" );
        query.setContentFilter( contentFilter );

        return query;
    }


    public ContentIndexQuery createContentQuery( int from, int count, String queryString, Set<CategoryKey> categoryFilter,
                                                 Set<ContentTypeKey> contentTypeFilter )
    {
        ContentIndexQuery query = createContentQuery( queryString, categoryFilter, contentTypeFilter );
        query.setIndex( from );
        query.setCount( count );

        return query;
    }


    public QueryTranslator getQueryTranslator()
    {
        return queryTranslator;
    }

    public void compareStringsIgnoreFormatting( String expected, String actual )
    {

        String expectedStripped = expected.replaceAll( "\\r\\n|\\r|\\n", " " ).toLowerCase();

        String actualStripped = actual.replaceAll( "\\r\\n|\\r|\\n", " " ).toLowerCase();

        if ( !expectedStripped.equals( actualStripped ) )
        {
            // Just to get the nice "show diff" feature in IDEA
            assertEquals( expected, actual );
        }

    }


    public static String getCurrentTimeZone()
    {
        Calendar now = Calendar.getInstance();

        //get current TimeZone using getTimeZone method of Calendar class
        TimeZone timeZone = now.getTimeZone();

        return timeZone.getDisplayName() + timeZone.getRawOffset();

    }

}
