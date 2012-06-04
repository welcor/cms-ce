package com.enonic.cms.core.preview;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

public class PreviewServiceImplWithRequestTest
{
    private final PreviewContext SOME_PREVIEW = new PreviewContext( new MenuItemPreviewContext( null ) );

    private PreviewServiceImpl previewService;

    private MockHttpServletRequest request;


    @Before
    public void setUp()
        throws Exception
    {
        request = new MockHttpServletRequest();
        ServletRequestAccessor.setRequest( request );

        previewService = new PreviewServiceImpl();
    }

    @Test
    public void testIsInPreview()
        throws Exception
    {
        final boolean inPreview = previewService.isInPreview();
        Assert.assertFalse( inPreview );
    }

    @Test
    public void testGetPreviewContext()
        throws Exception
    {
        final PreviewContext previewContext = previewService.getPreviewContext();
        Assert.assertEquals( PreviewContext.NO_PREVIEW, previewContext );
    }

    @Test
    public void testSetPreviewContext()
        throws Exception
    {
        previewService.setPreviewContext( SOME_PREVIEW );
        // no exceptions must be thrown
    }

    @Test
    public void testIsInPreviewWithPreviewAndNoSession()
        throws Exception
    {
        previewService.setPreviewContext( SOME_PREVIEW );

        final boolean inPreview = previewService.isInPreview();
        Assert.assertFalse( inPreview );
    }

    @Test
    public void testIsInPreviewWithPreviewAndWithSession()
        throws Exception
    {
        request.getSession( true );
        previewService.setPreviewContext( SOME_PREVIEW );

        final boolean inPreview = previewService.isInPreview();
        Assert.assertFalse( inPreview );
    }

    @Test
    public void testIsInPreviewWithPreviewAndWithSessionAndPreviewEnabled()
        throws Exception
    {
        request.getSession( true );
        request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

        previewService.setPreviewContext( SOME_PREVIEW );

        final boolean inPreview = previewService.isInPreview();
        Assert.assertTrue( inPreview );
    }

    @Test
    public void testGetPreviewContextNoSession()
        throws Exception
    {
        //request.getSession( false );
        request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

        previewService.setPreviewContext( SOME_PREVIEW );

        final PreviewContext previewContext = previewService.getPreviewContext();
        Assert.assertEquals( null, previewContext );  // no session !
    }

    @Test
    public void testGetPreviewContextWithSession()
        throws Exception
    {
        request.getSession( true );
        request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

        previewService.setPreviewContext( SOME_PREVIEW );

        final PreviewContext previewContext = previewService.getPreviewContext();
        Assert.assertEquals( SOME_PREVIEW, previewContext );
    }

    @Test
    public void testGetPreviewContextWithSessionAndNoPreview()
        throws Exception
    {
        request.getSession( true );

        final PreviewContext previewContext = previewService.getPreviewContext();
        Assert.assertEquals( PreviewContext.NO_PREVIEW, previewContext );
    }

}
