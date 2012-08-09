package com.enonic.cms.store.dao;


import java.util.HashSet;
import java.util.Set;

public class ContentEagerFetches
{
    private final Set<Table> values = new HashSet<Table>();

    public ContentEagerFetches add( final Table value )
    {
        this.values.add( value );
        return this;
    }

    public boolean hasTable( final Table value )
    {
        return this.values.contains( value );
    }

    public enum Table
    {
        ACCESS,
        MAIN_VERSION,
        SECTION_CONTENT,
        DIRECT_MENUITEM_PLACEMENT,
        CONTENT_HOME
    }

    public final static ContentEagerFetches PRESET_FOR_PORTAL =
        new ContentEagerFetches().add( Table.MAIN_VERSION ).add( Table.SECTION_CONTENT ).add( Table.SECTION_CONTENT ).add(
            Table.DIRECT_MENUITEM_PLACEMENT ).add( Table.CONTENT_HOME );

    public final static ContentEagerFetches PRESET_FOR_APPLYING_CONTENT_ACCESS = new ContentEagerFetches().add( Table.ACCESS );
}
