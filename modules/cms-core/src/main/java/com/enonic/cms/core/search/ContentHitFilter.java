package com.enonic.cms.core.search;


import com.enonic.cms.core.content.ContentKey;

public interface ContentHitFilter
{
    public boolean shouldInclude( ContentKey key );
}
