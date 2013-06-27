/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource;

import org.jdom.Element;
import org.jdom.filter.Filter;

final class ElementNameFilter
    implements Filter
{
    private final String name;

    public ElementNameFilter( final String name )
    {
        this.name = name;
    }

    @Override
    public boolean matches( final Object o )
    {
        return ( o instanceof Element ) && matches( (Element) o );
    }

    private boolean matches( final Element o )
    {
        return o.getName().equalsIgnoreCase( this.name );
    }
}
