/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import java.util.Set;

import com.google.common.collect.Sets;

public class RangeFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{

    Set<RangeFacetResultEntry> resultEntries = Sets.newLinkedHashSet();

    @Override
    public FacetResultType getFacetResultType()
    {
        return FacetResultType.RANGE;
    }


    protected void addResult( RangeFacetResultEntry rangeFacetResultEntry )
    {
        resultEntries.add( rangeFacetResultEntry );
    }

    public Set<RangeFacetResultEntry> getResultEntries()
    {
        return resultEntries;
    }
}
