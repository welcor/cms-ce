/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.Collection;

import com.enonic.cms.core.content.ContentKey;

public interface RelationsDataEntry
{
    // creates a shallow copy of internal RelatedContent collection
    Collection<ContentKey> getRelatedContentKeys();

    // marks content from internal RelatedContent collection
    boolean markReferencesToContentAsDeleted( ContentKey contentKey );
}