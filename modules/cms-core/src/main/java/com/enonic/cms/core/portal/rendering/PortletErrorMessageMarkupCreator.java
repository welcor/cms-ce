/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;

import com.enonic.cms.framework.util.HtmlEncoder;

import com.enonic.cms.core.portal.PortletXsltViewTransformationException;

public class PortletErrorMessageMarkupCreator
{
    public String createMarkup( String message, Exception exception )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "<div" );
        str.append( " style=\"" );
        str.append( " border-style: solid;" );
        str.append( " border-width: 2px;" );
        str.append( " border-color: #FF0000;" );
        str.append( " background-color: #FFC0C0;" );
        str.append( " color: black;" );
        str.append( " font-size: 12px;" );
        str.append( " padding: 4px; " );
        str.append( " text-align: left\"" );
        str.append( " title=\"" ).append( getDetails( exception ) ).append( "\">" );
        str.append( HtmlEncoder.encode( message ) );
        str.append( "</div>" );
        return str.toString();
    }

    private String getDetails( final Exception e )
    {
        Throwable error = e;
        final StringWriter writer = new StringWriter();

        if ( error instanceof PortletXsltViewTransformationException )
        {
            error = error.getCause();
        }

        error.printStackTrace( new PrintWriter( writer ) );
        return StringEscapeUtils.escapeHtml( error.getMessage() );
    }
}
