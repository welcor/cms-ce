/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.structure.menuitem.section.SectionContentTypeFilterKey;

public class SectionContentTypeFilterKeyUserType
    extends AbstractIntegerBasedUserType<SectionContentTypeFilterKey>
{
    public SectionContentTypeFilterKeyUserType()
    {
        super( SectionContentTypeFilterKey.class );
    }

    public SectionContentTypeFilterKey get( int value )
    {
        return new SectionContentTypeFilterKey( value );
    }

    public Integer getIntegerValue( SectionContentTypeFilterKey value )
    {
        return value.toInt();
    }

    public boolean isMutable()
    {
        return false;
    }
}
