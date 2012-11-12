package com.enonic.cms.core.content.resultset.facet;

import java.util.Map;

import com.google.common.collect.Maps;

public class TermsFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    private Map<String, Integer> results = Maps.newHashMap();

    public Map<String, Integer> getResults()
    {
        return results;
    }

    public void addResult( String term, Integer count )
    {
        results.put( term, count );
    }

}
