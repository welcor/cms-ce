package com.enonic.cms.core.xslt.functions.admin;

import org.springframework.stereotype.Component;

import net.sf.saxon.Configuration;

import com.enonic.cms.core.xslt.functions.XsltFunctionRegistration;

@Component
public final class AdminFunctionRegistration
    implements XsltFunctionRegistration
{
    @Override
    public void register( final Configuration config )
    {
        register( config, new EvaluateFunction() );
        register( config, new NodeSetFunction() );
        register( config, new SerializeFunction() );
        register( config, new UniqueIdFunction() );
        register( config, new UrlEncodeFunction() );
    }

    private void register( final Configuration config, final AbstractAdminFunction function )
    {
        config.registerExtensionFunction( function );
    }
}
