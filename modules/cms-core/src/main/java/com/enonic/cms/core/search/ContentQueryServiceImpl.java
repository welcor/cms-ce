package com.enonic.cms.core.search;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.index.AggregatedQuery;
import com.enonic.cms.core.content.index.AggregatedResult;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.content.index.IndexValueResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.index.ContentIndexService;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/30/11
 * Time: 1:19 PM
 */
public class ContentQueryServiceImpl
    implements ContentQueryService
{

    @Autowired
    ContentIndexService contentIndexService;

    public ContentResultSet query( ContentIndexQuery query )
    {

        try
        {
            return contentIndexService.query( query );
        }
        catch ( Exception e )
        {

            throw new RuntimeException( "Failurino!", e );
        }

    }

    public IndexValueResultSet query( IndexValueQuery query )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public AggregatedResult query( AggregatedQuery query )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
