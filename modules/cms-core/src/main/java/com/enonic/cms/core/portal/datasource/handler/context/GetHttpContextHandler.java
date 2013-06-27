/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.context;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetHttpContextHandler")
public final class GetHttpContextHandler
    extends SimpleDataSourceHandler
{
    public GetHttpContextHandler()
    {
        super( "getHttpContext" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        return new Document( createHttpElement( req.getHttpRequest() ) );
    }

    private Element createHttpElement( final HttpServletRequest request )
    {
        final Element httpEl = new Element( "http" );

        if ( request != null )
        {
            httpEl.setAttribute( "action", request.getMethod() );
            httpEl.addContent( new Element( "user-agent" ).setText( request.getHeader( "user-agent" ) ) );
            httpEl.addContent( new Element( "client-ip" ).setText( request.getRemoteAddr() ) );
            httpEl.addContent( new Element( "referer" ).setText( request.getHeader( "referer" ) ) );

            // accept
            final Element acceptElem = new Element( "accept" );
            httpEl.addContent( acceptElem );

            // language
            final String acceptLanguage = request.getHeader( "accept-language" );
            if ( acceptLanguage != null )
            {
                final String[] languages = StringUtils.split( acceptLanguage, "," );
                for ( String languageStr : languages )
                {
                    if ( languageStr.indexOf( ";" ) > 0 )
                    {
                        final Element langElem = new Element( "language" );
                        langElem.setText( languageStr.substring( 0, languageStr.indexOf( ";" ) ) );
                        langElem.setAttribute( "q", languageStr.substring( languageStr.indexOf( ";" ) + 3 ) );
                        acceptElem.addContent( langElem );
                    }
                    else
                    {
                        acceptElem.addContent( new Element( "language" ).setText( languageStr ) );
                    }
                }
            }
        }
        return httpEl;
    }
}
