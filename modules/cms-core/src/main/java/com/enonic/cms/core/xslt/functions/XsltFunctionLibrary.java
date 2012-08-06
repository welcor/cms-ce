package com.enonic.cms.core.xslt.functions;

import java.util.List;

import com.google.common.collect.Lists;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.ExtensionFunctionDefinition;

public abstract class XsltFunctionLibrary
{
    private final List<AbstractXsltFunction> list;

    public XsltFunctionLibrary()
    {
        this.list = Lists.newArrayList();
    }

    protected final void add( final AbstractXsltFunction function )
    {
        this.list.add( function );
    }

    public final void registerAll( final Configuration config )
    {
        for (final AbstractXsltFunction function : this.list) {
            register( config, function );
        }
    }

    private void register( final Configuration config, final AbstractXsltFunction function )
    {
        config.registerExtensionFunction( function );

        for (final ExtensionFunctionDefinition alias : function.getAliases() ) {
            config.registerExtensionFunction( alias );
        }
    }
}
