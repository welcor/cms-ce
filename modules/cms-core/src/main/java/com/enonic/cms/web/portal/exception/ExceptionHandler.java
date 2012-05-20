package com.enonic.cms.web.portal.exception;

import java.io.IOException;

import javax.servlet.ServletException;

import com.enonic.cms.web.portal.PortalWebContext;

public interface ExceptionHandler
{
    public void handle( PortalWebContext context, Exception error )
        throws ServletException, IOException;
}
