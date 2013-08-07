/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el.accessors;

import com.enonic.cms.core.portal.VerticalSession;

public final class SessionAccessor
    implements Accessor
{
    private final VerticalSession verticalSession;

    public SessionAccessor( final VerticalSession verticalSession )
    {
        this.verticalSession = verticalSession;
    }

    @Override
    public Object getValue( final String name )
    {
        if ( this.verticalSession != null )
        {
            final Object attribute = this.verticalSession.getAttribute( name );

            if ( attribute != null )
            {
                return attribute;
            }
        }

        return null;
    }

}
