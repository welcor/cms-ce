package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.Configuration;

import com.enonic.cms.core.xslt.functions.XsltFunctionLibrary;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public final class PortalXsltFunctionLibrary
    extends XsltFunctionLibrary
{
    private final PortalFunctionsMediator mediator;

    public PortalXsltFunctionLibrary( final PortalFunctionsMediator mediator )
    {
        this.mediator = mediator;
        registerAll();
    }

    private void registerAll()
    {
        doRegister( new CreateAttachmentUrlFunction() );
        doRegister( new CreateBinaryUrlFunction() );
        doRegister( new CreateCaptchaFormInputNameFunction() );
        doRegister( new CreateCaptchaImageUrlFunction() );
        doRegister( new CreateContentUrlFunction() );
        doRegister( new CreateImageUrlFunction() );
        doRegister( new CreatePageUrlFunction() );
        doRegister( new CreatePermalinkFunction() );
        doRegister( new CreateResourceUrlFunction() );
        doRegister( new CreateServicesUrlFunction() );
        doRegister( new CreateUrlFunction() );
        doRegister( new CreateWindowPlaceholderFunction() );
        doRegister( new CreateWindowUrlFunction() );
        doRegister( new GetInstanceKeyFunction() );
        doRegister( new GetLocaleFunction() );
        doRegister( new GetPageKeyFunction() );
        doRegister( new GetWindowKeyFunction() );
        doRegister( new ImageExistsFunction() );
        doRegister( new IsCaptchaEnabledFunction() );
        doRegister( new IsWindowEmptyFunction() );
        doRegister( new IsWindowInlineFunction() );
        doRegister( new LocalizeFunction() );
        doRegister( new Md5DigestFunction() );
        doRegister( new ShaDigestFunction() );
    }

    private void doRegister( final AbstractPortalFunction function )
    {
        function.setPortalFunctions( this.mediator );
        add( function );
    }
}
