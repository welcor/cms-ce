/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.urlrewrite;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.Status;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;

@Component
public class UrlRewriterBeanImpl
    implements UrlRewriterBean, InitializingBean
{

    private final static Logger LOG = Logger.getLogger( UrlRewriterBean.class.getName() );

    private boolean enabled = false;

    private UrlRewriter urlRewriter;

    private Pattern[] pathPatternsToSkip;

    private Conf conf;

    private boolean logging;

    private String xmlConfigurationFileLocation;

    public void afterPropertiesSet()
        throws Exception
    {
        File configFile = new File( getXmlConfigurationFileLocation() );

        if ( configFile.exists() )
        {
            enabled = true;
            FileInputStream fileStream = new FileInputStream( configFile );
            conf = new Conf( fileStream, getXmlConfigurationFileLocation() );
            urlRewriter = new UrlRewriter( conf );
        }

        ArrayList<String> patternsToSkip = new ArrayList<String>(  );
        patternsToSkip.add( "^(.*)/site/([0-9]+)(.*)/page(.*)" );
        patternsToSkip.add( "^(.*)/site/([0-9]+)(.*)/errorpage(.*)" );
        patternsToSkip.add( "^(.*)/site/([0-9]+)(.*)/binary(.*)" );
        patternsToSkip.add( "^(.*)/site/([0-9]+)(.*)/datasource(.*)" );
        patternsToSkip.add( "^(.*)/site/([0-9]+)(.*)/_default/(.*)" );
        setPathPatternsToSkip( patternsToSkip );
    }

    private void setPathPatternsToSkip( List list )
    {

        pathPatternsToSkip = new Pattern[list.size()];
        for ( int i = 0; i < list.size(); i++ )
        {
            String patternStr = (String) list.get( i );
            pathPatternsToSkip[i] = Pattern.compile( patternStr );
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public String getStatus()
    {
        if ( !enabled )
        {
            return "URL rewriting is disabled";
        }

        Status s = new Status( conf );
        s.displayStatusOffline();
        return s.getBuffer().toString();
    }

    public boolean doRewriteURL( HttpServletRequest hsRequest, HttpServletResponse hsResponse, FilterChain chain )
        throws java.io.IOException, javax.servlet.ServletException
    {
        if ( !enabled )
        {
            return false;
        }

        if ( doSkip( hsRequest ) )
        {
            log( "Skipped: " + hsRequest.getRequestURI() );
            return false;
        }
        try
        {
            RewrittenUrl url = urlRewriter.processRequest( hsRequest, hsResponse );
            if ( url == null )
            {
                log( "Ignored: " + hsRequest.getRequestURI() );
                return false;
            }
            log( "Changed from: " + hsRequest.getRequestURI() + " to: " + url.getTarget() );

            return urlRewriter.processRequest( hsRequest, hsResponse, chain );
        }
        catch ( InvocationTargetException e )
        {
            LOG.log( Level.SEVERE, e.getMessage(), e );
        }
        return false;
    }

    private void log( String msg )
    {
        if ( logging )
        {
            System.out.println( "UrlRewrite: " + msg );
        }
    }

    private boolean doSkip( ServletRequest request )
    {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        String lookIn = httpRequest.getRequestURI();

        for ( Pattern pattern : pathPatternsToSkip )
        {
            Matcher matcher = pattern.matcher( lookIn );
            if ( matcher.find() )
            {
                return true;
            }
        }
        return false;
    }

    @Value( "${cms.urlrewrite.logging}" )
    public void setLogging( boolean logging )
    {
        this.logging = logging;
    }

    public String getXmlConfigurationFileLocation()
    {
        return xmlConfigurationFileLocation;
    }

    @Value("${cms.home}/config/urlrewrite.xml")
    public void setXmlConfigurationFileLocation( String xmlConfigurationFileLocation )
    {
        this.xmlConfigurationFileLocation = xmlConfigurationFileLocation;
    }
}