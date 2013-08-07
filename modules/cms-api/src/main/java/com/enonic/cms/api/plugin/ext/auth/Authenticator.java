package com.enonic.cms.api.plugin.ext.auth;

import com.enonic.cms.api.plugin.ext.ExtensionBase;

public abstract class Authenticator
    extends ExtensionBase
    implements Comparable<Authenticator>
{
    private int priority = 0;

    /**
     * Priority of this authentication extension. Used if multiple interceptors
     * are registered.
     *
     * @return the priority.
     */
    public final int getPriority()
    {
        return this.priority;
    }

    /**
     * Priority of this authentication extension. Used if multiple interceptors
     * are registered. This method sets the priority.
     *
     * @param priority priority of extension.
     */
    public final void setPriority( int priority )
    {
        this.priority = priority;
    }

    public final int compareTo( final Authenticator other )
    {
        return this.priority - other.priority;
    }

    /**
     * Tries to authenticate the user. Returns true if authenticated, and false if not.
     * If it returns false, then the system will ask the next authenticator in chain. If no such interceptor
     * exists, then it will go trough to the user store (local or remote). If it returns true, then
     * it will not go trough to the next interceptor in chain.
     *
     * @param token authentication token.
     * @return true if successful, false otherwise.
     */
    public abstract boolean authenticate( AuthenticationToken token );
}
