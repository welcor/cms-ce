package com.enonic.cms.core.tools;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.search.SearchResponse;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.core.content.index.ContentIndexService;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 9:23 AM
 */
public class IndexMonitorController
    extends AbstractToolController
{

    private ContentIndexService newContentIndexService;

    private ContentIndexService oldContentIndexService;


    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "newIndexNumberOfContent", getTotalHits() );
        model.put( "Number of nodes", 1 );

        /*
        final ExtensionSet extensions = this.pluginManager.getExtensions();
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "functionLibraryExtensions", toWrappers( extensions.getAllFunctionLibraries() ) );
        model.put( "autoLoginExtensions", toWrappers( extensions.getAllHttpAutoLoginPlugins() ) );
        model.put( "httpInterceptors", toWrappers( extensions.getAllHttpInterceptors() ) );
        model.put( "httpResponseFilters", toWrappers( extensions.getAllHttpResponseFilters() ) );
        model.put( "taskExtensions", toWrappers( extensions.getAllTaskPlugins() ) );
        model.put( "textExtractorExtensions", toWrappers( extensions.getAllTextExtractorPlugins() ) );
        model.put( "pluginHandles", toPluginWrappers( this.pluginManager.getPlugins() ) );
        */

        process( req, res, model, "indexMonitorPage" );

    }

    private long getTotalHits()
    {
        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 0,\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "";

        final SearchResponse response = newContentIndexService.query( termQuery );

        return response.getHits().getTotalHits();
    }


    public void setNewContentIndexService( ContentIndexService newContentIndexService )
    {
        this.newContentIndexService = newContentIndexService;
    }

    public void setOldContentIndexService( ContentIndexService oldContentIndexService )
    {
        this.oldContentIndexService = oldContentIndexService;
    }
}
