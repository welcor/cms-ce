package com.enonic.cms.web.portal.instanttrace;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.HttpServletUtil;
import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public class InstantTraceResourceHandler
    extends WebHandlerBase
{
    @Autowired
    protected ResourceLoader resourceLoader;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserStoreService userStoreService;

    @Autowired
    protected MemberOfResolver memberOfResolver;

    @Autowired
    private MimeTypeResolver mimeTypeResolver;


    @Override
    protected boolean canHandle( final Path localPath )
    {
        return localPath.containsSubPath( "_itrace", "resources" );
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        handleResource( context.getRequest(), context.getResponse() );
    }

    private void handleResource( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        final String fileName = FilenameUtils.getName( request.getRequestURI() );
        final String mimeType = mimeTypeResolver.getMimeType( fileName );
        final InputStream inputStream = this.resourceLoader.getResource( "WEB-INF/itrace/" + fileName ).getInputStream();
        final ServletOutputStream outputStream = response.getOutputStream();

        HttpServletUtil.copyNoCloseOut( inputStream, outputStream );
        response.setContentType( mimeType );
    }
}
