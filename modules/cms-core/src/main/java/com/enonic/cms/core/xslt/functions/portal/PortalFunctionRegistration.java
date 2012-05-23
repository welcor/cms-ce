package com.enonic.cms.core.xslt.functions.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.saxon.Configuration;

import com.enonic.cms.core.xslt.functions.XsltFunctionRegistration;

@Component
public final class PortalFunctionRegistration
    implements XsltFunctionRegistration
{
    private PortalFunctionsMediator mediator;

    @Autowired
    public void setMediator( final PortalFunctionsMediator mediator )
    {
        this.mediator = mediator;
    }

    @Override
    public void register( final Configuration config )
    {
        register( config, new CreateAttachmentUrlFunction() );
        register( config, new CreateBinaryUrlFunction() );
        register( config, new CreateCaptchaFormInputNameFunction() );
        register( config, new CreateCaptchaImageUrlFunction() );
        register( config, new CreateContentUrlFunction() );
        register( config, new CreateImageUrlFunction() );
        register( config, new CreatePageUrlFunction() );
        register( config, new CreatePermalinkFunction() );
        register( config, new CreateResourceUrlFunction() );
        register( config, new CreateServicesUrlFunction() );
        register( config, new CreateUrlFunction() );
        register( config, new CreateWindowPlaceholderFunction() );
        register( config, new CreateWindowUrlFunction() );
        register( config, new GetInstanceKeyFunction() );
        register( config, new GetLocaleFunction() );
        register( config, new GetPageKeyFunction() );
        register( config, new GetWindowKeyFunction() );
        register( config, new ImageExistsFunction() );
        register( config, new IsCaptchaEnabledFunction() );
        register( config, new IsWindowEmptyFunction() );
        register( config, new IsWindowInlineFunction() );
        register( config, new LocalizeFunction() );
        register( config, new Md5DigestFunction() );
        register( config, new ShaDigestFunction() );
    }

    private void register( final Configuration config, final AbstractPortalFunction function )
    {
        function.setPortalFunctions( this.mediator );
        config.registerExtensionFunction( function );
    }
}
