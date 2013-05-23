package com.enonic.cms.web.portal.services;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.cms.core.structure.SiteKey;

@Component
public final class SessionServicesProcessor
    extends ServicesProcessorBase
{
    public SessionServicesProcessor()
    {
        super( "session" );
    }

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        VerticalSession vsession = (VerticalSession) session.getAttribute( VerticalSession.VERTICAL_SESSION_OBJECT );
        if ( vsession == null )
        {
            vsession = new VerticalSession();
            session.setAttribute( VerticalSession.VERTICAL_SESSION_OBJECT, vsession );
        }

        if ( "setattributes".equals( operation ) )
        {
            for ( Object key : formItems.keySet() )
            {
                String value = formItems.getString( key );

                if ( !"redirect".equals( key ) && !"_redirect".equals( key ) && !"op".equals( key ) && !"_op".equals( key ) &&
                    !"handler".equals( key ) && !"_handler".equals( key ) )
                {
                    vsession.setAttribute( key.toString(), value );
                }
            }
        }
        else if ( "removeattributes".equals( operation ) )
        {
            Object obj = formItems.get( "name" );
            if ( obj.getClass() == String.class )
            {
                vsession.removeAttribute( (String) obj );
            }
            else
            {
                String[] attributes = (String[]) obj;
                for ( String attribute : attributes )
                {
                    vsession.removeAttribute( attribute );
                }
            }
        }
        else
        {
            super.handlerCustom( request, response, session, formItems, userServices, siteKey, operation );
        }

        redirectToPage( request, response, formItems );
    }
}
