/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import java.util.LinkedHashMap;
import java.util.Map;

public class RelatedContentFetchTrace
    extends BaseTrace
    implements Trace
{
    private Map<Integer, Fetch> childrenFetches = new LinkedHashMap<Integer, Fetch>();

    private Map<Integer, Fetch> parentFetches = new LinkedHashMap<Integer, Fetch>();

    private Integer maxChildrenLevel;

    private Integer maxParentLevel;

    public void setMaxParentLevel( final Integer maxParentLevel )
    {
        this.maxParentLevel = maxParentLevel;
    }

    public void setMaxChildrenLevel( final Integer maxChildrenLevel )
    {
        this.maxChildrenLevel = maxChildrenLevel;
    }

    public void setParentFetch( final int level, final int count )
    {
        parentFetches.put( level, new Fetch( count ) );
    }

    public void setChildrenFetch( final int level, final int count )
    {
        childrenFetches.put( level, new Fetch( count ) );
    }

    public Integer getMaxChildrenLevel()
    {
        return maxChildrenLevel;
    }

    public Integer getMaxParentLevel()
    {
        return maxParentLevel;
    }

    public String getChildrenFetches()
    {
        StringBuilder s = new StringBuilder();
        int count = 0;
        for ( Fetch fetch : childrenFetches.values() )
        {
            count++;
            s.append( fetch.getCount() );
            if ( count < childrenFetches.size() )
            {
                s.append( " -> " );
            }
        }
        return s.toString();
    }

    public String getParentFetches()
    {
        StringBuilder s = new StringBuilder();
        int count = 0;
        for ( Fetch fetch : parentFetches.values() )
        {
            count++;
            s.append( fetch.getCount() );
            if ( count < parentFetches.size() )
            {
                s.append( " -> " );
            }
        }
        return s.toString();
    }

    public class Fetch
    {
        private int count;

        public Fetch( final int count )
        {
            this.count = count;
        }

        public int getCount()
        {
            return count;
        }
    }

}
