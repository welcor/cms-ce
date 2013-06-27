/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

public interface FacetResultSet
{

    public String getName();

    public void setName( String facetName );

    public FacetResultType getFacetResultType();

}
