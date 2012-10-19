/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.framework.util.HttpServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;

public class ResourceDataServlet
    extends AbstractAdminwebServlet
{

    public void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {

        String keyStr = request.getParameter( "id" );
        if ( keyStr != null && keyStr.length() > 0 )
        {
            ResourceKey key = ResourceKey.from( keyStr );

            ResourceFile res = resourceService.getResourceFile( key );

            if ( res != null )
            {
                HttpServletUtil.copyNoCloseOut( res.getDataAsInputStream(), response.getOutputStream() );
            }
            else
            {
                String msg = "Resource not found: {0}";
                VerticalAdminLogger.warn(msg, keyStr, null );
                response.sendError( HttpServletResponse.SC_NOT_FOUND, MessageFormat.format( msg, keyStr ) );
            }
        }
        else
        {
            String message = "Resource key not specified.";
            VerticalAdminLogger.warn(message );
            response.sendError( HttpServletResponse.SC_NOT_FOUND, message );
        }
    }
}