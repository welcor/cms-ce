package com.enonic.cms.core.portal.livetrace;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.livetrace.systeminfo.SystemInfo;

@Component
public class LivePortalTraceJsonGenerator
{
    private ObjectMapper jacksonObjectMapper;

    public LivePortalTraceJsonGenerator()
    {
        jacksonObjectMapper = new ObjectMapper().configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false );
    }

    public String generate( final PortalRequestTrace trace )
    {
        try
        {
            return jacksonObjectMapper.writeValueAsString( trace );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to transform objects to JSON: " + e.getMessage(), e );
        }
    }

    public String generate( final List<PortalRequestTraceRow> rows )
    {
        try
        {
            return jacksonObjectMapper.writeValueAsString( rows );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to transform objects to JSON: " + e.getMessage(), e );
        }
    }

    public String generate( final SystemInfo systemInfoObject )
    {
        try
        {
            return jacksonObjectMapper.writeValueAsString( systemInfoObject );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to transform objects to JSON: " + e.getMessage(), e );
        }
    }
}
