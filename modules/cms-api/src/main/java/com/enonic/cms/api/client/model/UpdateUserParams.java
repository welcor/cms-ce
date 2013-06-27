/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;

import com.enonic.cms.api.client.model.user.UserInfo;

public class UpdateUserParams
        extends AbstractParams
        implements Serializable
{
    private static final long serialVersionUID = -1L;

    public String userstore;

    public String username;

    public String displayName;

    public String email;

    public UserInfo userInfo = new UserInfo();

    /**
     * There are two possible settings for the updateStrategy: <code>UPDATE</code> and <code>MODIFY</code>.
     * <code>MODIFY</code> may be used, when only one or a few fields should to be changed.  The provided values
     * will be changed, and all others will be left unchanged.
     * With <code>UPDATE</code>, every field in the new content must have a value, and will be set to whatever
     * value is provided.  This is the only way to remove the data for a field that has had a value that should be
     * changed to a blank value or no value.  If <code>UPDATE</code> is used, a field will no value will not be
     * changed.
     *
     * These strategies apply only to the data set in the <code>contentData</code> field.
     * <code>publishFrom</code>, <code>publishTo</code>, <code>createNewVersion</code> and <code>setAsCurrentVersion</code>
     * are metadata that are not affected by this update strategy.
     */
    public UpdateStrategy updateStrategy = UpdateStrategy.UPDATE;

    public enum UpdateStrategy
    {
        UPDATE,
        MODIFY
    }
}

