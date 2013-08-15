package com.enonic.cms.web.status.builders;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.cms.web.status.StatusInfoBuilder;

@Component
public final class OSStatusInfoBuilder
    extends StatusInfoBuilder
{
    public OSStatusInfoBuilder()
    {
        super( "os" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        final OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        json.put( "name", bean.getName() );
        json.put( "version", bean.getVersion() );
        json.put( "arch", bean.getArch() );
        json.put( "cores", bean.getAvailableProcessors() );
        json.put( "load_average", bean.getSystemLoadAverage() );
    }
}
