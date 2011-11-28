package com.enonic.cms.core.search;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/24/11
 * Time: 12:51 PM
 */
final class IndexAnalyzerSettingsBuilder
{

    public static String buildAnalyserSettings()
        throws Exception
    {
        /* return jsonBuilder()
             .startObject()
                 .startObject( "analysis" )
                     .startObject( "analyzer" )
                         .startObject( "keywordlowercase" )
                             .field( "type", "custom" )
                             .field( "tokenizer", "keyword" )
                             .field( "filter", new String[]{"lowercase"} )
                         .endObject()
                     .endObject()
                 .endObject()
             .endObject()
             .string();
        */

        return "";
    }


}
