package com.enonic.cms.core.content.image;

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 9/20/11
 * Time: 2:18 PM
 */
public class ImageUtilTest
{
    private BufferedImage originalImage;

    private BufferedImage extremeImage;

    @Before
    public void setUp()
    {
        try
        {
            originalImage = ImageIO.read( loadImage( "Arn.jpg" ) );
            extremeImage = ImageIO.read( loadImage( "transparentLine.png" ) );
        }
        catch ( IOException e )
        {
            fail( "Image not found" );
        }
    }

    @Test
    public void testScaleImage()
    {
        BufferedImage scaledImage = ImageUtil.scaleImage( originalImage, 200, 200, ContentImageUtil.getBufferedImageType( "jpg" ) );

        Assert.assertEquals( 200, scaledImage.getHeight() );
        Assert.assertEquals( 200, scaledImage.getWidth() );
    }

    @Test
    public void testScaleImageIllegalValues()
    {
        BufferedImage scaledImage = ImageUtil.scaleImage( originalImage, -200, -200, ContentImageUtil.getBufferedImageType( "jpg" ) );

        Assert.assertEquals( 1, scaledImage.getHeight() );
        Assert.assertEquals( 1, scaledImage.getWidth() );
    }

    @Test
    public void testScaleImageExtremeValues()
    {
        BufferedImage scaledImage = ImageUtil.scaleImage( extremeImage, 400, 0, ContentImageUtil.getBufferedImageType( "png" ) );

        Assert.assertEquals( 1, scaledImage.getHeight() );
        Assert.assertEquals( 400, scaledImage.getWidth() );
    }

    @Test
    public void testTransparencyIsKeptInScaling()
    {
        BufferedImage scaledImage = ImageUtil.scaleImage( extremeImage, 400, 3, ContentImageUtil.getBufferedImageType( "png" ) );

        double[] alphaPixel = {100.0};
        scaledImage.getAlphaRaster().getPixel( 200, 1, alphaPixel );
        Assert.assertEquals( 0.0, alphaPixel[0], 0.0 );
        alphaPixel[0] = 100.0;
        scaledImage.getAlphaRaster().getPixel( 0, 0, alphaPixel );
        Assert.assertEquals( 0.0, alphaPixel[0], 0.0 );
        alphaPixel[0] = 100.0;
        scaledImage.getAlphaRaster().getPixel( 399, 2, alphaPixel );
        Assert.assertEquals( 0.0, alphaPixel[0], 0.0 );
    }


    private InputStream loadImage( String fileName )
        throws IOException
    {
        ClassPathResource resource = new ClassPathResource( ImageUtilTest.class.getName().replace( ".", "/" ) + "-" + fileName );
        return resource.getInputStream();
    }
}
