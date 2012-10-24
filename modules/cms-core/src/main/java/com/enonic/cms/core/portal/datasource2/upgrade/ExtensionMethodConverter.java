package com.enonic.cms.core.portal.datasource2.upgrade;

import java.util.List;

import org.jdom.Element;

final class ExtensionMethodConverter
    extends MethodConverter
{
    @Override
    public boolean canHandle( final String methodName )
    {
        return methodName.contains( "." );
    }

    @Override
    public void doConvert( final Element result, final String methodName, final List<String> values )
    {
        setName( result, "invokeExtension" );
        addParameter( result, "name", methodName );

        for ( int i = 0; i < values.size(); i++ )
        {
            addParameter( result, "param" + ( i + 1 ), values.get( i ) );
        }
    }
}
