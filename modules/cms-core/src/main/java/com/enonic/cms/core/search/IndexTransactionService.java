package com.enonic.cms.core.search;

import java.util.Collection;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;

public interface IndexTransactionService
{

    void startTransaction();

    void commit();

    boolean isActive();

    void registerUpdate( final Collection<ContentKey> contentKeys, final boolean skipAttachments );

    void registerUpdate( ContentKey contentKey, boolean skipAttachments );

    void deleteContent( ContentKey contentKey );

    void clearJournal();
}
