package com.enonic.cms.core.content.category;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.enonic.cms.core.security.group.GroupKey;

public class CategoryACL
    implements Iterable<CategoryAccessControl>
{
    private Map<GroupKey, CategoryAccessControl> mapByGroupKey = new LinkedHashMap<GroupKey, CategoryAccessControl>();

    public void add( CategoryAccessControl control )
    {
        mapByGroupKey.put( control.getGroupKey(), control );
    }

    public CategoryAccessControl get( GroupKey groupKey )
    {
        return mapByGroupKey.get( groupKey );
    }

    public boolean hasAccessForGroup( GroupKey group )
    {
        return mapByGroupKey.containsKey( group );
    }

    @Override
    public Iterator<CategoryAccessControl> iterator()
    {
        return mapByGroupKey.values().iterator();
    }
}
