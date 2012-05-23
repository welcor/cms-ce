package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.value.SequenceType;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunction;

abstract class AbstractPortalFunction
    extends AbstractXsltFunction
{
    private PortalFunctionsMediator portalFunctions;

    public AbstractPortalFunction( final String localName )
    {
        super( "portal", "http://www.enonic.com/cms/xslt/portal", localName );
    }

    protected final PortalFunctionsMediator getPortalFunctions()
    {
        return this.portalFunctions;
    }

    public final void setPortalFunctions( final PortalFunctionsMediator portalFunctions )
    {
        this.portalFunctions = portalFunctions;
    }

    protected final void setMaximumNumberOfArguments( final int args )
    {
        final SequenceType[] types = new SequenceType[args];

        for (int i = 0; i < types.length; i++) {
            types[i] = SequenceType.ANY_SEQUENCE;
        }

        setArgumentTypes( types );
    }
}
