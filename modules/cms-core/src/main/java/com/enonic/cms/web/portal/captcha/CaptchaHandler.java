package com.enonic.cms.web.portal.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.captcha.CaptchaRepository;
import com.enonic.cms.web.portal.handler.WebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public final class CaptchaHandler
    extends WebHandlerBase
{
    private CaptchaRepository captchaRepository;

    @Override
    protected boolean canHandle( final String localPath )
    {
        return localPath.endsWith( "/_captcha" );
    }

    @Override
    protected void doHandle( final WebContext context )
        throws Exception
    {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        try
        {
            byte[] captchaChallengeAsJpeg;
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();

            String captchaId = request.getSession().getId();
            BufferedImage challenge = captchaRepository.getImageChallengeForID( captchaId, request.getLocale() );

            ImageIO.write( challenge, "png", imageOutputStream );
            captchaChallengeAsJpeg = imageOutputStream.toByteArray();

            // flush it in the response
            response.setHeader( "Cache-Control", "no-store" );
            response.setHeader( "Pragma", "no-cache" );
            response.setDateHeader( "Expires", 0 );
            response.setContentType( "image/png" );
            ServletOutputStream responseOutputStream = response.getOutputStream();
            responseOutputStream.write( captchaChallengeAsJpeg );
            responseOutputStream.flush();
            responseOutputStream.close();
        }
        catch ( IllegalArgumentException e )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    @Autowired
    public void setCaptchaRepository( CaptchaRepository captchaRepository )
    {
        this.captchaRepository = captchaRepository;
    }
}
