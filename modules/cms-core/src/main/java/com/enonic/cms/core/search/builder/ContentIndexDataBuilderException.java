package com.enonic.cms.core.search.builder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/6/11
 * Time: 10:45 AM
 */
public class ContentIndexDataBuilderException
    extends RuntimeException
{

    public ContentIndexDataBuilderException( String s, Throwable throwable )
    {
        super( s, throwable );
    }
}