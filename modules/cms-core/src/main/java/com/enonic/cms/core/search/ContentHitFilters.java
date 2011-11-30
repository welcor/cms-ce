package com.enonic.cms.core.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;

public final class ContentHitFilters
{
    public static ContentHitFilter all()
    {
        return new AllFilter();
    }

    public static ContentHitFilter none()
    {
        return new NoneFilter();
    }

    public static ContentHitFilter set( ContentKey... keys )
    {
        return set( Arrays.asList( keys ) );
    }

    public static ContentHitFilter set( Collection<ContentKey> keys )
    {
        return new SetFilter( new HashSet<ContentKey>( keys ) );
    }

    private final static class AllFilter
            implements ContentHitFilter
    {
        public boolean shouldInclude( ContentKey key )
        {
            return true;
        }

        public String toString()
        {
            return "#all";
        }
    }

    private final static class NoneFilter
            implements ContentHitFilter
    {
        public boolean shouldInclude( ContentKey key )
        {
            return false;
        }

        public String toString()
        {
            return "#none";
        }
    }

    private final static class SetFilter
            implements ContentHitFilter
    {
        private final Set<ContentKey> set;

        public SetFilter( Set<ContentKey> set )
        {
            this.set = set;
        }

        public boolean shouldInclude( ContentKey key )
        {
            return this.set.contains( key );
        }

        public String toString()
        {
            return "#set(" + this.set.size() + ")";
        }
    }
}
