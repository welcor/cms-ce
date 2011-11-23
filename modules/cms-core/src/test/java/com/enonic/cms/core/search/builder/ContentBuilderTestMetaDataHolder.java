package com.enonic.cms.core.search.builder;

import com.google.gson.Gson;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 1:06 PM
 */
public class ContentBuilderTestMetaDataHolder
{


    Double key_numeric;

    Double key;

    String orderby_key;

    String orderby_title;

    String title;

    String publishfrom;

    String publishto;

    String timestamp;

    Integer status_numeric;

    Integer status;

    public static ContentBuilderTestMetaDataHolder createMetaDataHolder( String json )
    {
        Gson gson = new Gson();
        return gson.fromJson( json, ContentBuilderTestMetaDataHolder.class );
    }

}
