package com.enonic.cms.api.plugin.ext.auth;

/**
 * Authentication token that includes userStore, user and password.
 */
public interface AuthenticationToken
{
    /**
     * Returns the user store the user lives in.
     *
     * @return user store name.
     */
    public String getUserStore();

    /**
     * Returns the user name of the user to authenticate.
     *
     * @return user name.
     */
    public String getUserName();

    /**
     * Returns the password for user.
     *
     * @return user password.
     */
    public String getPassword();
}
