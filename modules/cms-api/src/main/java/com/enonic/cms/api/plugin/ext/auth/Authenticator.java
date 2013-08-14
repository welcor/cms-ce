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
     * Tries to authenticate the user. Return SUCCESS if authentication was successful. Then the authentication
     * chain will stop and return control to the user. If FAILURE was returned, the chain stops and an exception
     * is returned to the user. If CONTINUE was returned, it will continue to the next authenticator in chain and
     * eventually go to the user stores authentication method.
     *
     * @param token authentication token.
     * @return result state.
     */
    public abstract AuthenticationResult authenticate( AuthenticationToken token );
}
