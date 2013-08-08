/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el;

import com.enonic.cms.core.portal.datasource.el.accessors.Accessor;
import com.enonic.cms.core.portal.datasource.el.accessors.PortalAccessor;
import com.enonic.cms.core.portal.datasource.el.accessors.UserAccessor;

/**
 * root object for Spring EL.
 * <p/>
 * SpEL does not allow to add random properties, they must exist in root object
 * <p/>
 * all functions in StaticExpressionFunctions will be available by default in contexts
 */
final class ExpressionRootObject
    extends StaticExpressionFunctions
{
    private Accessor param;

    private Accessor params;

    private Accessor session;

    private Accessor cookie;

    private Accessor properties;

    private UserAccessor user;

    private PortalAccessor portal;

    public ExpressionRootObject()
    {
    }

    public Accessor getParam()
    {
        return param;
    }

    public void setParam( final Accessor param )
    {
        this.param = param;
    }

    public Accessor getParams()
    {
        return params;
    }

    public void setParams( final Accessor params )
    {
        this.params = params;
    }

    public Accessor getSession()
    {
        return session;
    }

    public void setSession( final Accessor session )
    {
        this.session = session;
    }

    public Accessor getCookie()
    {
        return cookie;
    }

    public void setCookie( final Accessor cookie )
    {
        this.cookie = cookie;
    }

    public UserAccessor getUser()
    {
        return user;
    }

    public void setUser( final UserAccessor user )
    {
        this.user = user;
    }

    public PortalAccessor getPortal()
    {
        return portal;
    }

    public void setPortal( final PortalAccessor portal )
    {
        this.portal = portal;
    }

    public Accessor getProperties()
    {
        return properties;
    }

    public void setProperties( final Accessor properties )
    {
        this.properties = properties;
    }
}
