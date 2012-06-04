package com.enonic.cms.web.portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestDispatcher
{
    public void handle( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException;
}
