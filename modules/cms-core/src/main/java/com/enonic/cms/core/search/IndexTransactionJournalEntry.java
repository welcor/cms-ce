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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final IndexTransactionJournalEntry that = (IndexTransactionJournalEntry) o;

        if ( skipAttachments != that.skipAttachments )
        {
            return false;
        }
        if ( contentKey != null ? !contentKey.equals( that.contentKey ) : that.contentKey != null )
        {
            return false;
        }
        if ( operation != that.operation )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = operation != null ? operation.hashCode() : 0;
        result = 31 * result + ( contentKey != null ? contentKey.hashCode() : 0 );
        result = 31 * result + ( skipAttachments ? 1 : 0 );
        return result;
    }
}
