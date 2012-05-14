package com.enonic.cms.core.search;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.AggregatedQuery;
import com.enonic.cms.core.content.index.AggregatedResult;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.content.index.IndexValueResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasurer;
import com.enonic.cms.core.search.querymeasurer.QueryResultComparer;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/8/11
 * Time: 10:46 AM
 */
public class ContentIndexServiceDispatcher
    implements ContentIndexService
{
    ContentIndexServiceImpl newContentIndexService;

    private IndexQueryMeasurer indexQueryMeasurer;

    private QueryResultComparer resultComparer;

    com.enonic.cms.core.content.index.ContentIndexServiceImpl oldContentIndexService;

    private boolean runNewOnly = false;

    private boolean runOldOnly = false;

    public ContentIndexServiceDispatcher()
    {
        runNewOnly = Boolean.getBoolean( "runNewOnly" );
        runOldOnly = Boolean.getBoolean( "runOldOnly" );
    }

    public int remove( ContentKey contentKey )
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeByCategory( CategoryKey categoryKey )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeByContentType( ContentTypeKey contentTypeKey )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void index( ContentDocument doc, boolean deleteExisting )
    {

        if ( !runOldOnly )
        {
            newContentIndexService.index( doc, false );
        }

        if ( !runNewOnly )
        {
            oldContentIndexService.index( doc, false );
        }

    }

    public void indexBulk( List<ContentDocument> docs )
    {
        newContentIndexService.indexBulk( docs );
    }

    public boolean isIndexed( ContentKey contentKey )
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentResultSet query( ContentIndexQuery query )
    {
        ContentResultSet resultNew = null;
        ContentResultSet resultOld = null;

        if ( !runOldOnly )
        {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start( "new" );
            resultNew = newContentIndexService.query( query );
            stopWatch.stop();

            indexQueryMeasurer.addMeasure( query, stopWatch, "ElasticSearch" );
        }

        if ( !runNewOnly )
        {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start( "old" );
            resultOld = oldContentIndexService.query( query );
            stopWatch.stop();

            indexQueryMeasurer.addMeasure( query, stopWatch, "Hibernate" );
        }

        final boolean runBoth = !runNewOnly && !runOldOnly;

        if ( runBoth )
        {
            resultComparer.compareResults( query, resultNew, resultOld );
        }

        if ( runOldOnly )
        {
            return resultOld;
        }

        return resultNew;
    }

    public IndexValueResultSet query( IndexValueQuery query )
    {
        return this.newContentIndexService.query( query );
    }

    public AggregatedResult query( AggregatedQuery query )
    {
        return this.newContentIndexService.query( query );
    }

    public void optimize()
    {
        newContentIndexService.optimize();
    }

    public void flush()
    {
        newContentIndexService.flush();
    }

    public void setNewContentIndexService( ContentIndexServiceImpl newContentIndexService )
    {
        this.newContentIndexService = newContentIndexService;
    }

    public void setOldContentIndexService( com.enonic.cms.core.content.index.ContentIndexServiceImpl oldContentIndexService )
    {
        this.oldContentIndexService = oldContentIndexService;
    }


    @Autowired
    public void setIndexQueryMeasurer( IndexQueryMeasurer indexQueryMeasurer )
    {
        this.indexQueryMeasurer = indexQueryMeasurer;
    }

    @Autowired
    public void setResultComparer( final QueryResultComparer resultComparer )
    {
        this.resultComparer = resultComparer;
    }

    @Override
    public void initializeMapping()
    {
        newContentIndexService.initializeMapping();
    }

    @Override
    public Collection<ContentIndexEntity> getContentIndexedFields( ContentKey contentKey )
    {
        if ( !runOldOnly )
        {
            return newContentIndexService.getContentIndexedFields( contentKey );
        }
        else
        {
            return oldContentIndexService.getContentIndexedFields( contentKey );
        }
    }

}


