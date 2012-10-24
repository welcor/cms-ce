package com.enonic.cms.core.portal.datasource2.upgrade;

import java.util.List;

import org.jdom.Element;

final class FallbackMethodConverter
    extends MethodConverter
{
    @Override
    public boolean canHandle( final String methodName )
    {
        return true;
    }

    @Override
    public void doConvert( final Element result, final String methodName, final List<String> values )
    {
        setName( result, methodName );
    }
}
