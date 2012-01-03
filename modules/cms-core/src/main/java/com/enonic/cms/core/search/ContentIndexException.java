package com.enonic.cms.core.search;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/13/11
 * Time: 3:31 PM
 */
public class ContentIndexException
    extends RuntimeException
{

    public ContentIndexException( String message )
    {
        super( message );
    }

    public ContentIndexException( String message, Exception e )
    {
        super( message, e );
    }
}
