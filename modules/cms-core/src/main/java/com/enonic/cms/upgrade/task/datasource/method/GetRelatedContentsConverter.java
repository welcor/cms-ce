package com.enonic.cms.upgrade.task.datasource.method;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;

final class GetRelatedContentsConverter
    extends DataSourceMethodConverter
{
    public GetRelatedContentsConverter()
    {
        super( "getRelatedContents" );
    }

    @Override
    public Element convert( final String[] params )
    {
        List<Integer> validParametersNumber = Lists.newArrayList( 13, 15, 16 );

        final int parametersLength = params.length;

        if ( !validParametersNumber.contains( parametersLength ) )
        {
            return null;
        }

        if ( parametersLength == 13 )
        {
            return method("getRelatedContent")
                .param( "contentKeys", params[1])
                .param( "relation", params[0])
                .param( "orderBy", params[2])
                .param( "index", params[4])
                .param( "count", params[5])
                .param( "includeData", "true" )
                .param( "childrenLevel", params[7])
                .param( "parentLevel", params[6])
                .param( "requireAll", params[3])
                .build();

        }

        if ( parametersLength == 15 )
        {
            // Translated from !relatedTitlesOnly && !titlesOnly
            boolean includeData = !Boolean.valueOf( params[10] ) || !Boolean.valueOf( params[6] );

            return method("getRelatedContent")
                .param( "contentKeys", params[1])
                .param( "relation", params[0])
                .param( "orderBy", params[2])
                .param( "index", params[4])
                .param( "count", params[5])
                .param( "includeData", Boolean.toString( includeData ) )
                .param( "childrenLevel", params[8])
                .param( "parentLevel", params[7])
                .param( "requireAll", params[3])
                .build();
        }


            // Translated from !relatedTitlesOnly && !titlesOnly
            boolean includeData = !Boolean.valueOf( params[11] ) || !Boolean.valueOf( params[7] );

            return method("getRelatedContent")
                .param( "contentKeys", params[1])
                .param( "relation", params[0])
                .param( "query", params[2])
                .param( "orderBy", params[3])
                .param( "index", params[5])
                .param( "count", params[6])
                .param( "includeData", Boolean.toString( includeData) )
                .param( "childrenLevel", params[9])
                .param( "parentLevel", params[8])
                .param( "requireAll", params[4])
                .build();
    }

}
