package com.enonic.cms.core.search;

import java.util.Collection;

import com.enonic.cms.core.content.ContentKey;

public interface IndexTransactionService
{

    void startTransaction();

    void commit();

    boolean isActive();

    void registerUpdate( final Collection<ContentKey> contentKeys, final boolean updateMetadataOnly );

    void registerUpdate( ContentKey contentKey, boolean updateMetadataOnly );

    void deleteContent( ContentKey contentKey );

    void clearJournal();
}
