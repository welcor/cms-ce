package com.enonic.cms.core.search.facet;

public class FacetQueryException extends RuntimeException
{

    public FacetQueryException( final String s )
    {
        super( s );
    }

    public FacetQueryException( final String s, final Throwable throwable )
    {
        super( s, throwable );
    }
}
