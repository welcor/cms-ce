package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunction;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

abstract class AbstractPortalFunction
    extends AbstractXsltFunction
{
    private final static String NAMESPACE_URI = "http://www.enonic.com/cms/portal";

    private final static String OLD_NAMESPACE_URI = "http://www.enonic.com/cms/xslt/portal";

    private PortalFunctionsMediator portalFunctions;

    public AbstractPortalFunction( final String localName )
    {
        super( "", NAMESPACE_URI, localName );
        registerAlias( new StructuredQName( "", OLD_NAMESPACE_URI, localName ) );
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
