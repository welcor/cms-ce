/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.attachment;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.AttachmentRequestResolver;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.store.dao.ContentDao;

final class AttachmentRequestResolverImpl
    extends AttachmentRequestResolver
{
    private final ContentDao contentDao;

    public AttachmentRequestResolverImpl(final ContentDao contentDao)
    {
        this.contentDao = contentDao;
    }

    @Override
    protected BinaryDataKey getBinaryData( ContentEntity content, String label )
    {
        BinaryDataEntity binaryData;
        if ( label == null )
        {
            binaryData = content.getMainVersion().getOneAndOnlyBinaryData();
        }
        else
        {
            binaryData = content.getMainVersion().getBinaryData( label );
        }

        if ( "source".equals( label ) && binaryData == null )
        {
            binaryData = content.getMainVersion().getOneAndOnlyBinaryData();
        }

        if ( binaryData != null )
        {
            return new BinaryDataKey( binaryData.getKey() );
        }
        return null;
    }

    @Override
    protected ContentEntity getContent( ContentKey contentKey )
    {
        return contentDao.findByKey( contentKey );
    }
}
