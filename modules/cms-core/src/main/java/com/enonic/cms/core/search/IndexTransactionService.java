package com.enonic.cms.core.search;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

public interface IndexTransactionService
{

    void startTransaction();

    void commit();

    boolean isActive();

    void updateContent( ContentKey contentKey, boolean skipAttachments );

    void updateContent( ContentEntity contentEntity, boolean skipAttachments );

    void deleteContent( ContentKey contentKey );

    void clearJournal();
}
