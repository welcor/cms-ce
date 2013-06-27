/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content.image;

import java.io.Serializable;

public class ImageContentDataInput
    implements Serializable
{
    private static final long serialVersionUID = -3954348979920687656L;

    public ImageNameInput name;

    public ImageDescriptionInput description;

    public ImageKeywordsInput keywords;

    public ImageBinaryInput binary;

}
