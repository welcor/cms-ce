package com.enonic.cms.core.portal.datasource;

import java.util.Map;

import com.enonic.cms.core.portal.datasource.expressionfunctions.StaticExpressionFunctions;

/**
 * root object for Spring EL.
 *
 * SpEL does not allow to add random properties, they must exist in root object
 *
 * all functions in StaticExpressionFunctions will be available by default in contexts
 *
 */
public class ExpressionRootObject
    extends StaticExpressionFunctions
{
    private Map<String, String> param;
    private Map<String, String> session;
    private Map<String, String> cookie;
    private Map<String, String> user;
    private Map<String, Object> portal;

    public ExpressionRootObject()
    {
    }

    public Map<String, String> getParam()
    {
        return param;
    }

    public void setParam( final Map<String, String> param )
    {
        this.param = param;
    }

    public Map<String, String> getSession()
    {
        return session;
    }

    public void setSession( final Map<String, String> session )
    {
        this.session = session;
    }

    public Map<String, String> getCookie()
    {
        return cookie;
    }

    public void setCookie( final Map<String, String> cookie )
    {
        this.cookie = cookie;
    }

    public Map<String, String> getUser()
    {
        return user;
    }

    public void setUser( final Map<String, String> user )
    {
        this.user = user;
    }

    public Map<String, Object> getPortal()
    {
        return portal;
    }

    public void setPortal( final Map<String, Object> portal )
    {
        this.portal = portal;
    }
}
