/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;

public class ChangeUserPasswordParams
        extends AbstractParams
        implements Serializable
{
    private static final long serialVersionUID = -1L;

    public String userstore;

    public String username;

    public String password;
}

