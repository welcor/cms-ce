/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.query;

import com.enonic.cms.core.content.ContentKey;

/**
 * This interface defines the index value result.
 */
public interface IndexValueResult
{
    /**
     * Return the value.
     */
    public String getValue();

    /**
     * Return the content key.
     */
    public ContentKey getContentKey();
}
