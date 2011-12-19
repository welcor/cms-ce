package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.util.StopWatch;

import com.google.common.collect.Sets;

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
import com.enonic.cms.core.search.measure.IndexMeasureLogEntry;
import com.enonic.cms.core.search.measure.IndexTimeMeasurer;

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

    com.enonic.cms.core.content.index.ContentIndexServiceImpl oldContentIndexService;

    private boolean runNewOnly = false;

    private boolean runOldOnly = false;

    private List<ExecuteDiffEntry> executeDiffList = new ArrayList<ExecuteDiffEntry>();

    private IndexTimeMeasurer queryMeasurer = new IndexTimeMeasurer();

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
        newContentIndexService.index( doc, false );
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

        StopWatch stopWatch = new StopWatch();

        ContentResultSet resultNew = null;
        ContentResultSet resultOld = null;
        long newServiceTime = 0;
        long oldServiceTime = 0;

        if ( !runOldOnly )
        {
            stopWatch.start( "new" );
            resultNew = newContentIndexService.query( query );
            stopWatch.stop();
            newServiceTime = stopWatch.getLastTaskTimeMillis();
        }

        if ( !runNewOnly )
        {
            stopWatch.start( "old" );
            resultOld = oldContentIndexService.query( query );
            stopWatch.stop();
            oldServiceTime = stopWatch.getLastTaskTimeMillis();
        }

        if ( !runNewOnly && !runOldOnly )
        {

            addTimeMeasurement( query, newServiceTime, oldServiceTime );
            //compareResults(query, resultNew, resultOld);
        }

        return resultNew;
    }

    private void compareResults( ContentIndexQuery query, ContentResultSet resultNew, ContentResultSet resultOld )
    {

        final HashSet<ContentKey> newResultContentKeys = Sets.newHashSet( resultNew.getKeys() );
        final HashSet<ContentKey> oldResultContentKeys = Sets.newHashSet( resultOld.getKeys() );
        Sets.SetView<ContentKey> diff = Sets.symmetricDifference( newResultContentKeys, oldResultContentKeys );

        if ( diff.isEmpty() )
        {
            return;
        }

        System.out.println( "Different results, hits new : " + newResultContentKeys.size() + ", hits old: " + oldResultContentKeys.size() );
        executeDiffList.add( new ExecuteDiffEntry( query.toString(), newResultContentKeys, oldResultContentKeys ) );
    }


    private void addTimeMeasurement( ContentIndexQuery query, long newServiceTime, long oldServiceTime )
    {
        queryMeasurer.add( new IndexMeasureLogEntry( newServiceTime, oldServiceTime, query ) );
    }

    public IndexValueResultSet query( IndexValueQuery query )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public AggregatedResult query( AggregatedQuery query )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void createIndex()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setNewContentIndexService( ContentIndexServiceImpl newContentIndexService )
    {
        this.newContentIndexService = newContentIndexService;
    }

    public void setOldContentIndexService( com.enonic.cms.core.content.index.ContentIndexServiceImpl oldContentIndexService )
    {
        this.oldContentIndexService = oldContentIndexService;
    }


    private class ExecuteDiffEntry
    {

        private String query;

        private HashSet<ContentKey> newResultContentKeys;

        private HashSet<ContentKey> oldResultContentKeys;

        private ExecuteDiffEntry( String query, HashSet<ContentKey> newResultContentKeys, HashSet<ContentKey> oldResultContentKeys )
        {
            this.query = query;
            this.newResultContentKeys = newResultContentKeys;
            this.oldResultContentKeys = oldResultContentKeys;
        }

        @Override
        public String toString()
        {

            StringBuffer buf = new StringBuffer();

            buf.append( "Query: " + query + "\n\r" );

            Sets.SetView<ContentKey> onlyInNew = Sets.difference( newResultContentKeys, oldResultContentKeys );
            Sets.SetView<ContentKey> onlyInOld = Sets.difference( oldResultContentKeys, newResultContentKeys );

            if ( !onlyInNew.isEmpty() )
            {

                buf.append( "Only in new: " + "\n\r" );

                for ( ContentKey key : onlyInNew )
                {
                    buf.append( key.toString() + "\n\r" );
                }
            }

            if ( !onlyInOld.isEmpty() )
            {

                buf.append( "Only in old: " + "\n\r" );

                for ( ContentKey key : onlyInNew )
                {
                    buf.append( key.toString() + "\n\r" );
                }
            }

            return buf.toString();
        }
    }

    public List<ExecuteDiffEntry> getExecuteDiffList()
    {
        return executeDiffList;
    }

    public void optimize()
    {
        newContentIndexService.optimize();
    }
}


