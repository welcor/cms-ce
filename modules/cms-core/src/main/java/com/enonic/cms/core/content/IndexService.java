/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

import com.enonic.cms.core.search.query.ContentDocument;

/**
 *
 */
public interface IndexService
{
    public void reindex( List<ContentKey> contentKeys );

    ContentDocument createContentDocument( ContentEntity content, final boolean updateMetadataOnly );

    public void optimizeIndex();

    public void reinitializeIndex();

    public boolean indexExists();

    public void createIndex();
}
