package com.enonic.cms.core.search.query;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/19/11
 * Time: 10:41 AM
 */
public class QueryTranslatorException extends RuntimeException
{

    public QueryTranslatorException( String s )
    {
        super( s );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public QueryTranslatorException( String s, Throwable throwable )
    {
        super( s, throwable );    //To change body of overridden methods use File | Settings | File Templates.
    }
}
