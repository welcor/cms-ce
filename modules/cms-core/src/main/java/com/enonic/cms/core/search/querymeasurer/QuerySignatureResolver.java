package com.enonic.cms.core.search.querymeasurer;

import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/20/12
 * Time: 9:11 AM
 */
public class QuerySignatureResolver
{
    private final static String LS = System.getProperty( "line.separator" );

    public static IndexQuerySignature createQuerySignature( ContentIndexQuery query )
    {
        StringBuffer s = new StringBuffer();

        appendIfNotNull( s, "query : ", query.getQuery() );
        appendIfNotNull( s, "index : ", query.getIndex() );
        appendIfNotNull( s, "count : ", query.getCount() );
        appendIfNotNull( s, "categoryAccessTypeFilter : ", query.getCategoryAccessTypeFilter() );
        appendIfNotNull( s, "contentStatusFilter : ", query.getContentStatusFilter() );
        appendIfNotNull( s, "contentOnlineAtFilter : ", query.getContentOnlineAtFilter() != null ? "yes" : "no" );
        appendIfNotNull( s, "contentFilter : ", query.getContentFilter() );
        appendIfNotNull( s, "sectionFilter : ", query.getSectionFilter()  );
        appendIfNotNull( s, "categoryFilter : ", query.getCategoryFilter() );
        appendIfNotNull( s, "contentTypeFilter : ", query.getContentTypeFilter() );
        appendIfNotNull( s, "securityFilter : ", query.getSecurityFilter() );

        return new IndexQuerySignature( s.toString() );
    }


    private static void appendIfNotNull( StringBuffer s, String fieldName, Object paramValue )
    {
        if ( paramValue == null )
        {
            return;
        }

        s.append( fieldName + paramValue + LS );

    }

}
