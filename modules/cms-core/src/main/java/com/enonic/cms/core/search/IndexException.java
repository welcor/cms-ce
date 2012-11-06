package com.enonic.cms.core.search;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/13/11
 * Time: 3:31 PM
 */
public class IndexException
    extends RuntimeException
{

    public IndexException( final String message )
    {
        super( message );
    }

    public IndexException( final String message, final Exception e )
    {
        super( message, e );
    }
}
