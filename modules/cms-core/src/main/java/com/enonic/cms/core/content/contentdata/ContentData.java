/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;


public interface ContentData
{
    String getTitle();

    void replaceBinaryKeyPlaceholders( List<BinaryDataKey> binaryDatas );

    Set<ContentKey> resolveRelatedContentKeys();

    boolean hasRelatedChild( ContentKey contentKey );

    boolean markReferencesToContentAsDeleted( ContentKey key );

    void turnBinaryKeysIntoPlaceHolders( Map<BinaryDataKey, Integer> indexByBinaryDataKey );
}
