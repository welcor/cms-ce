/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

/**
 * This interface defines the content entity fetcher.
 */
public interface ContentEntityFetcher
{
    ContentMap fetch( List<ContentKey> keys );
}
