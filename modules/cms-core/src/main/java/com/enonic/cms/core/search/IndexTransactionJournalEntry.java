package com.enonic.cms.core.search;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.builder.ContentIndexData;

class IndexTransactionJournalEntry
{

    public enum JournalOperation
    {
        UPDATE, DELETE
    }


    private final JournalOperation operation;

    private final ContentIndexData contentIndexData;

    private final ContentKey contentKey;


    public IndexTransactionJournalEntry( JournalOperation operation, ContentIndexData contentIndexData )
    {
        this.operation = operation;
        this.contentIndexData = contentIndexData;
        this.contentKey = contentIndexData.getKey();
    }

    public IndexTransactionJournalEntry( JournalOperation operation, ContentKey contentKey )
    {
        this.operation = operation;
        this.contentIndexData = null;
        this.contentKey = contentKey;
    }


    public JournalOperation getOperation()
    {
        return operation;
    }

    public ContentIndexData getContentIndexData()
    {
        return contentIndexData;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }
}
