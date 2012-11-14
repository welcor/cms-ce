package com.enonic.cms.core.search.query;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/6/11
 * Time: 11:51 AM
 */
public class IndexQueryException
    extends RuntimeException
{

    public IndexQueryException( final String s )
    {
        super( s );
    }

    public IndexQueryException( final String s, final Throwable throwable )
    {
        super( s, throwable );
    }
}
