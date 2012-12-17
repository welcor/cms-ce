package com.enonic.cms.core.plugin.manager;

import org.joda.time.DateTime;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.core.plugin.context.PluginContext;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.util.OsgiHelper;

final class PluginHandleImpl
    implements PluginHandle
{
    private final static Logger LOG = LoggerFactory.getLogger( PluginHandleImpl.class );

    private final Bundle bundle;
    private final ExtensionHolder holder;

    public PluginHandleImpl(final Bundle bundle, final ExtensionHolder holder)
    {
        this.bundle = bundle;
        this.holder = holder;
    }

    public long getKey()
    {
        return this.bundle.getBundleId();
    }

    public String getId()
    {
        return this.bundle.getSymbolicName();
    }

    public String getName()
    {
        return OsgiHelper.getBundleName( this.bundle );
    }

    public String getVersion()
    {
        return this.bundle.getVersion().toString();
    }

    public boolean isActive()
    {
        return this.bundle.getState() == Bundle.ACTIVE;
    }

    public DateTime getTimestamp()
    {
        return new DateTime( this.bundle.getLastModified() );
    }

    public PluginContext getContext()
    {
        return OsgiHelper.requireService(this.bundle.getBundleContext(), PluginContext.class);
    }

    public PluginConfig getConfig()
    {
        return getContext().getConfig();
    }

    public void update()
    {
        try {
            this.bundle.update();
        } catch (final Exception e) {
            LOG.warn("Exception when updating plugin [{}]", this.bundle.getSymbolicName(), e);
        }
    }

    public ExtensionSet getExtensions()
    {
        return new ExtensionSetImpl(this.holder.getAllForBundle(this.bundle));
    }
}
