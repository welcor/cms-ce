/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.enonic.cms.core.content.category.CategoryACL;
import com.enonic.cms.core.content.category.CategoryAccessControl;
import com.enonic.cms.core.security.group.GroupKey;

public class ContentACL
    implements Iterable<ContentAccessControl>
{
    private Map<GroupKey, ContentAccessControl> mapByGroupKey = new LinkedHashMap<GroupKey, ContentAccessControl>();

    public void add( ContentAccessControl control )
    {
        mapByGroupKey.put( control.getGroup(), control );
    }

    public boolean containsKey( GroupKey groupKey )
    {
        return mapByGroupKey.containsKey( groupKey );
    }

    @Override
    public Iterator<ContentAccessControl> iterator()
    {
        return mapByGroupKey.values().iterator();
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for ( ContentAccessControl cac : mapByGroupKey.values() )
        {
            s.append( cac ).append( ", " );
        }
        return s.toString();
    }

    public static ContentACL create( final CategoryACL categoryACL )
    {
        final ContentACL contentACL = new ContentACL();
        for ( CategoryAccessControl car : categoryACL )
        {
            contentACL.add( ContentAccessControl.create( car ) );
        }
        return contentACL;
    }
}
