/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.mock;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.enonic.cms.api.plugin.PluginEnvironment;

/**
 * Mock implementation of the abstract PluginEnvironment class.
 */
public class MockPluginEnvironment
    implements PluginEnvironment
{
    /**
     * Shared objects.
     */
    private final Map<String, Serializable> sharedMap = new HashMap<String, Serializable>();

    /**
     * Http servlet request.
     */
    private HttpServletRequest currentRequest;

    /**
     * Return the current request.
     */
    public HttpServletRequest getCurrentRequest()
    {
        return this.currentRequest;
    }

    /**
     * Set the current request.
     */
    public void setCurrentRequest( HttpServletRequest currentRequest )
    {
        this.currentRequest = currentRequest;
    }

    /**
     * Return the current session.
     */
    public HttpSession getCurrentSession()
    {
        return this.currentRequest != null ? this.currentRequest.getSession( true ) : null;
    }

    /**
     * Return shared object by name.
     */
    public Serializable getSharedObject( String name )
    {
        return this.sharedMap.get( name );
    }

    /**
     * Set the shared object.
     */
    public void setSharedObject( String name, Serializable object )
    {
        this.sharedMap.put( name, object );
    }

    /**
     * Return shared object names.
     */
    public Set<String> getSharedObjectNames( String prefix )
    {
        HashSet<String> set = new HashSet<String>();
        for ( String key : this.sharedMap.keySet() )
        {
            if ( ( prefix == null ) || key.startsWith( prefix ) )
            {
                set.add( key );
            }
        }

        return set;
    }
}
