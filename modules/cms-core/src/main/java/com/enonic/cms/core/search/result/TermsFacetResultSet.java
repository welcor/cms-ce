package com.enonic.cms.core.search.result;

import java.util.Map;

import com.google.common.collect.Maps;

public class TermsFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    FacetResultType facetResultType = FacetResultType.TERMS;

    private Map<String, Integer> results = Maps.newLinkedHashMap();

    public Map<String, Integer> getResults()
    {
        return results;
    }

    public void addResult( String term, Integer count )
    {
        results.put( term, count );
    }

    @Override
    public FacetResultType getFacetResultType()
    {
        return facetResultType;
    }
}
