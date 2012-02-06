package com.enonic.cms.itest.search;

import java.util.List;

import org.elasticsearch.action.search.SearchResponse;

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

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/31/12
 * Time: 7:55 AM
 */
public class ContentIndexServiceTestWrapper
    implements ContentIndexService
{
    private ContentIndexService contentIndexService;

    public int remove( ContentKey contentKey )
    {
        return contentIndexService.remove( contentKey );
    }

    public void removeByCategory( CategoryKey categoryKey )
    {
        contentIndexService.removeByCategory( categoryKey );
    }

    public void removeByContentType( ContentTypeKey contentTypeKey )
    {
        contentIndexService.removeByContentType( contentTypeKey );
    }

    public void index( ContentDocument doc, boolean deleteExisting )
    {
        contentIndexService.index( doc, deleteExisting );
        contentIndexService.flush();
    }

    public void indexBulk( List<ContentDocument> docs )
    {
        contentIndexService.indexBulk( docs );
        contentIndexService.flush();
    }

    public boolean isIndexed( ContentKey contentKey )
    {
        return contentIndexService.isIndexed( contentKey );
    }

    public ContentResultSet query( ContentIndexQuery query )
    {
        return contentIndexService.query( query );
    }

    public IndexValueResultSet query( IndexValueQuery query )
    {
        return contentIndexService.query( query );
    }

    public AggregatedResult query( AggregatedQuery query )
    {
        return contentIndexService.query( query );
    }

    public void createIndex()
    {
        contentIndexService.createIndex();
    }

    public void updateIndexSettings()
    {
        contentIndexService.updateIndexSettings();
    }

    public void optimize()
    {
        contentIndexService.optimize();
    }

    public void deleteIndex()
    {
        contentIndexService.deleteIndex();
    }

    public void flush()
    {
        contentIndexService.flush();
    }

    public SearchResponse query( String query )
    {
        return contentIndexService.query( query );
    }

    public void initalizeIndex( boolean forceDelete )
    {
        contentIndexService.initalizeIndex( forceDelete );
    }

    public void setContentIndexService( ContentIndexService contentIndexService )
    {
        this.contentIndexService = contentIndexService;
    }
}

