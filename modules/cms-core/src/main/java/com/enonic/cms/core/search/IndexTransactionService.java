package com.enonic.cms.core.search;

import com.enonic.cms.core.content.ContentKey;

public interface IndexTransactionService
{

    void startTransaction();

    void commit();

    boolean isActive();

    void registerUpdate( ContentKey contentKey, boolean skipAttachments );

    void deleteContent( ContentKey contentKey );

    void clearJournal();
}
