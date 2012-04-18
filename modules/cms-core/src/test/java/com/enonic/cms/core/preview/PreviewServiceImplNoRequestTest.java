package com.enonic.cms.core.preview;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PreviewServiceImplNoRequestTest
{
    private PreviewServiceImpl previewService;

    @Before
    public void setUp()
        throws Exception
    {
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
        previewService.setPreviewContext( PreviewContext.NO_PREVIEW );
        // no exceptions must be thrown
    }
}
