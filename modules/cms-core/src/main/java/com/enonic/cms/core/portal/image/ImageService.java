/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.image;

import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageResponse;

public interface ImageService
{
    public ImageResponse process( ImageRequest req )
        throws ImageProcessorException;

    public Long getImageTimestamp( ImageRequest req );

    public boolean accessibleInPortal( ImageRequest req );
}
