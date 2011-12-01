package com.enonic.cms.core.search;

import com.enonic.cms.core.content.index.AggregatedQuery;
import com.enonic.cms.core.content.index.AggregatedResult;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.content.index.IndexValueResultSet;
import com.enonic.cms.core.content.query.ContentByCategoryQuery;
import com.enonic.cms.core.content.query.ContentByContentQuery;
import com.enonic.cms.core.content.query.ContentByQueryQuery;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.content.query.OpenContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/30/11
 * Time: 1:19 PM
 */
public interface ContentQueryService
{

    /**
     * Query the content.
     */
    public ContentResultSet query( ContentIndexQuery query );

    /**
     * Query the index values.
     */
    public IndexValueResultSet query( IndexValueQuery query );

    /**
     * Query the index values.
     */
    public AggregatedResult query( AggregatedQuery query );
}

