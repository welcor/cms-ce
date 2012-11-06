/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.webdav;

import java.io.File;

import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.core.resource.access.ResourceAccessResolver;
import com.enonic.cms.core.security.SecurityService;

public interface DavConfiguration
{
    public File getResourceRoot();

    public SecurityService getSecurityService();

    public ResourceAccessResolver getResourceAccessResolver();

    public MimeTypeResolver getMimeTypeResolver();
}
