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
    public IndexQueryException( String s, Throwable throwable )
    {
        super( s, throwable );
    }
}
