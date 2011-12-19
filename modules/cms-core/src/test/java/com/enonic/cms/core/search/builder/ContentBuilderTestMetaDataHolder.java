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
    Double key;

    String title;

    String publishfrom;

    String publishto;

    String timestamp;

    Integer status;

    String contenttype;

    Integer contenttype_key;

    Double assignee_key;

    String assignee_qualifiedname;

    Double assigner_key;

    String assigner_qualifiedname;

    String assignmentduedate;

    Double modifier_key;

    String modifier_qualifiedname;

    Double owner_key;

    String owner_qualifiedname;

    Integer priority;

    Double category_key;

    String category_name;

    public static ContentBuilderTestMetaDataHolder createMetaDataHolder( String json )
    {
        Gson gson = new Gson();
        return gson.fromJson( json, ContentBuilderTestMetaDataHolder.class );
    }

}
