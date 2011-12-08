package com.enonic.cms.core.search.builder;

import com.google.gson.Gson;

public class ContentBuilderTestCustomDataHolder
{
    Double key_numeric;

    Double key;

    String orderby_key;

    String data_person_age;

    public static ContentBuilderTestCustomDataHolder createCustomDataHolder( String json )
    {
        Gson gson = new Gson();
        return gson.fromJson( json, ContentBuilderTestCustomDataHolder.class );
    }
}
