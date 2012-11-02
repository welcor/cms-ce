/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.urlrewrite;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UrlRewriterBean
{
    boolean isEnabled();

    String getStatus();

    boolean doRewriteURL( HttpServletRequest hsRequest, HttpServletResponse hsResponse, FilterChain chain )
        throws java.io.IOException, javax.servlet.ServletException;
}
