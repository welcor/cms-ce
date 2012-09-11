package com.enonic.cms.core.search;

import com.enonic.cms.core.content.ContentKey;

class IndexTransactionJournalEntry
{
    public enum JournalOperation
    {
        UPDATE, DELETE
    }

    private final JournalOperation operation;

    private final ContentKey contentKey;

    private boolean skipAttachments = true;

    public IndexTransactionJournalEntry( JournalOperation operation, ContentKey contentKey, boolean skipAttachments )
    {
        this.operation = operation;
        this.contentKey = contentKey;
        this.skipAttachments = skipAttachments;
    }

    public IndexTransactionJournalEntry( JournalOperation operation, ContentKey contentKey )
    {
        this.operation = operation;
        this.contentKey = contentKey;
    }

    public JournalOperation getOperation()
    {
        return operation;
    }

    public boolean isSkipAttachments()
    {
        return skipAttachments;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }
}
