package com.enonic.cms.core.portal.instanttrace;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.enonic.cms.core.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.core.portal.livetrace.DatasourceMethodArgument;
import com.enonic.cms.core.portal.livetrace.InstructionPostProcessingTrace;
import com.enonic.cms.core.portal.livetrace.PageRenderingTrace;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.core.portal.livetrace.User;
import com.enonic.cms.core.portal.livetrace.ViewTransformationTrace;
import com.enonic.cms.core.portal.livetrace.WindowRenderingTrace;
import com.enonic.cms.core.security.user.QualifiedUsername;

public class JsonSerializer
{
    private PortalRequestTrace portalRequestTrace;

    private PageRenderingTrace pageTrace;

    private long timeZero = 0;

    public JsonSerializer( final PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
        this.pageTrace = portalRequestTrace.getPageRenderingTrace();
        this.timeZero = this.pageTrace.getDuration().getStartTime().getMillis();
    }

    public String serialize()
    {

        JsonObject wrapper = new JsonObject();

        JsonObject xtrace = new JsonObject();

        appendVersion( xtrace );

        appendRequest( xtrace );

        wrapper.add( "xtrace", xtrace );

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        return gson.toJson( wrapper );
    }

    private void appendRequest( JsonObject xtrace )
    {
        xtrace.add( "request", createRequest() );
    }

    private void appendVersion( JsonObject xtrace )
    {
        xtrace.addProperty( "version", "1.0" );
    }

    private JsonObject createRequest()
    {
        JsonObject requestObject = new JsonObject();

        requestObject.addProperty( "id", String.valueOf( portalRequestTrace.getCompletedNumber() ) );
        requestObject.addProperty( "number", portalRequestTrace.getRequestNumber() );
        requestObject.addProperty( "requester", portalRequestTrace.getRequester().toString() );
        requestObject.addProperty( "url", portalRequestTrace.getUrl() );

        appendPage( requestObject );

        return requestObject;
    }

    private void appendPage( JsonObject requestObject )
    {
        requestObject.add( "page", createPage() );
    }

    private JsonObject createPage()
    {
        JsonObject pageObject = new JsonObject();

        pageObject.addProperty( "name", portalRequestTrace.getUrl() );
        pageObject.addProperty( "cache_hit", pageTrace.getCacheUsage().isUsedCachedResult() );
        pageObject.addProperty( "requester", resolveQualifiedUsernameAsString( toQualifiedUsername( pageTrace.getRenderer() ) ) );

        JsonObject duration = new JsonObject();

        duration.addProperty( "start_time_ms", 0 );
        duration.addProperty( "stop_time_ms", pageTrace.getDuration().getStopTime().getMillis() - timeZero );
        duration.addProperty( "total_time_ms", pageTrace.getDuration().getAsMilliseconds() );
        pageObject.add( "duration", duration );

        appendPageDatasources( pageObject );

        if ( !pageTrace.getCacheUsage().isUsedCachedResult() )
        {
            JsonObject viewObject = new JsonObject();

            if ( pageTrace.getViewTransformationTrace() != null )
            {
                viewObject.addProperty( "resource", pageTrace.getViewTransformationTrace().getView() );
            }

            appendXsltTransformingObjectForPageView( viewObject );
            pageObject.add( "view", viewObject );
        }

        appendWindows( pageObject );

        pageObject.add( "instruction_post_processing",
                        createInstructionPostProcessingObject( pageTrace.getInstructionPostProcessingTrace() ) );

        return pageObject;
    }

    private QualifiedUsername toQualifiedUsername( User user )
    {
        if ( user.getUserStoreName() == null )
        {
            return new QualifiedUsername( user.getUserName() );
        }
        else
        {
            return new QualifiedUsername( user.getUserStoreName(), user.getUserName() );
        }
    }

    private void appendPageDatasources( JsonObject page )
    {
        if ( !pageTrace.getCacheUsage().isUsedCachedResult() )
        {
            page.add( "datasources", createDatasources( pageTrace.getDatasourceExecutionTraces().getList() ) );
        }
    }

    private void appendWindows( JsonObject page )
    {
        page.add( "windows", createWindows( pageTrace.getWindowRenderingTraces().getList() ) );
    }

    private JsonArray createWindows( List<WindowRenderingTrace> windows )
    {
        JsonArray windowsArray = new JsonArray();
        for ( WindowRenderingTrace window : windows )
        {
            createWindow( windowsArray, window );
        }
        return windowsArray;
    }

    private void createWindow( JsonArray windowsArray, WindowRenderingTrace windowTrace )
    {
        JsonObject windowObject = new JsonObject();

        windowObject.addProperty( "name", windowTrace.getPortletName() );
        windowObject.addProperty( "cache_hit", windowTrace.getCacheUsage().isUsedCachedResult() );
        windowObject.addProperty( "requester", resolveQualifiedUsernameAsString( toQualifiedUsername( windowTrace.getRenderer() ) ) );

        JsonObject duration = new JsonObject();

        duration.addProperty( "start_time_ms", windowTrace.getDuration().getStartTime().getMillis() - timeZero );
        duration.addProperty( "stop_time_ms", windowTrace.getDuration().getStopTime().getMillis() - timeZero );
        duration.addProperty( "total_time_ms", windowTrace.getDuration().getAsMilliseconds() );
        windowObject.add( "duration", duration );

        appendWindowDatasources( windowObject, windowTrace );

        if ( !windowTrace.getCacheUsage().isUsedCachedResult() )
        {
            JsonObject viewObject = new JsonObject();
            if ( windowTrace.getViewTransformationTrace() != null )
            {
                viewObject.addProperty( "resource", windowTrace.getViewTransformationTrace().getView() );
            }

            appendXsltTransformingObjectForWindowView( viewObject, windowTrace );
            windowObject.add( "view", viewObject );
        }

        windowObject.add( "instruction_post_processing",
                          createInstructionPostProcessingObject( windowTrace.getInstructionPostProcessingTrace() ) );

        windowsArray.add( windowObject );
    }

    private void appendXsltTransformingObjectForPageView( JsonObject viewObject )
    {
        if ( !pageTrace.getCacheUsage().isUsedCachedResult() )
        {
            ViewTransformationTrace viewTransformationTrace = pageTrace.getViewTransformationTrace();
            long startTime = viewTransformationTrace.getDuration().getStartTime().getMillis() - timeZero;
            long endTime = viewTransformationTrace.getDuration().getStopTime().getMillis() - timeZero;

            JsonObject xsltTransformingObject = new JsonObject();

            JsonObject duration = new JsonObject();

            duration.addProperty( "start_time_ms", startTime );
            duration.addProperty( "stop_time_ms", endTime );
            duration.addProperty( "total_time_ms", endTime - startTime );
            xsltTransformingObject.add( "duration", duration );

            viewObject.add( "xslt_transforming", xsltTransformingObject );
        }
    }

    private void appendXsltTransformingObjectForWindowView( JsonObject viewObject, WindowRenderingTrace windowTrace )
    {
        if ( !windowTrace.getCacheUsage().isUsedCachedResult() )
        {
            final ViewTransformationTrace viewTransformationTrace = windowTrace.getViewTransformationTrace();
            final long startTime = viewTransformationTrace.getDuration().getStartTime().getMillis() - timeZero;
            final long endTime = viewTransformationTrace.getDuration().getStopTime().getMillis() - timeZero;

            JsonObject xsltTransformingObject = new JsonObject();

            JsonObject duration = new JsonObject();

            duration.addProperty( "start_time_ms", startTime );
            duration.addProperty( "stop_time_ms", endTime );
            duration.addProperty( "total_time_ms", endTime - startTime );
            xsltTransformingObject.add( "duration", duration );

            viewObject.add( "xslt_transforming", xsltTransformingObject );
        }
    }

    private JsonObject createInstructionPostProcessingObject( InstructionPostProcessingTrace trace )
    {
        JsonObject object = new JsonObject();

        JsonObject duration = new JsonObject();
        duration.addProperty( "total_time_ms", trace.getDuration().getAsMilliseconds() );
        duration.addProperty( "start_time_ms", trace.getStartTime().getMillis() - timeZero );
        duration.addProperty( "stop_time_ms", trace.getStartTime().getMillis() + trace.getDuration().getAsMilliseconds() - timeZero );

        object.add( "duration", duration );

        return object;
    }

    private void appendWindowDatasources( JsonObject windowObject, WindowRenderingTrace window )
    {
        if ( !window.getCacheUsage().isUsedCachedResult() )
        {
            windowObject.add( "datasources", createDatasources( window.getDatasourceExecutionTraces().getList() ) );
        }
    }

    private JsonArray createDatasources( List<DatasourceExecutionTrace> datasources )
    {
        JsonArray datasourceArray = new JsonArray();

        for ( DatasourceExecutionTrace datasource : datasources )
        {
            createDatasource( datasourceArray, datasource );
        }

        return datasourceArray;
    }

    private void createDatasource( JsonArray datasourceArray, DatasourceExecutionTrace datasource )
    {
        JsonObject datasourceObject = new JsonObject();
        datasourceObject.addProperty( "name", datasource.getMethodName() );

        JsonObject duration = new JsonObject();
        duration.addProperty( "start_time_ms", datasource.getDuration().getStartTime().getMillis() - timeZero );
        duration.addProperty( "stop_time_ms", datasource.getDuration().getStopTime().getMillis() - timeZero );
        duration.addProperty( "total_time_ms", datasource.getDuration().getAsMilliseconds() );
        datasourceObject.add( "duration", duration );

        JsonObject parameters = new JsonObject();
        for ( DatasourceMethodArgument parameter : datasource.getDatasourceMethodArguments() )
        {
            parameters.addProperty( parameter.getName(), parameter.getValue() );
        }

        datasourceObject.add( "parameters", parameters );

        datasourceArray.add( datasourceObject );
    }

    private String resolveQualifiedUsernameAsString( QualifiedUsername qualifiedUsername )
    {
        String userStoreName = "";
        if ( qualifiedUsername.getUserStoreName() != null )
        {
            userStoreName = qualifiedUsername.getUserStoreName() + "\\";
        }

        return userStoreName + qualifiedUsername.getUsername();
    }
}
