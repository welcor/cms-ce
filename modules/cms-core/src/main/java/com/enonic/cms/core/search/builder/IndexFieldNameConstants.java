package com.enonic.cms.core.search.builder;

import com.enonic.cms.core.content.index.config.IndexFieldType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 12:07 PM
 */
public class IndexFieldNameConstants
{
    protected static final String CONTENT_KEY_FIELDNAME = "key";

    public static final String INDEX_FIELDNAME_PROPERTY_SEPARATOR = "_";

    public static final String QUERY_LANGUAGE_PROPERTY_SEPARATOR = "/";

    public static final String INDEX_FIELD_TYPE_SEPARATOR = ".";

    public static final String ALL_USERDATA_FIELDNAME = "_all_userdata";

    public static final String ALL_USERDATA_FIELDNAME_DATE = ALL_USERDATA_FIELDNAME + "." + IndexFieldType.DATE.toString();

    public static final String ALL_USERDATA_FIELDNAME_NUMBER = ALL_USERDATA_FIELDNAME + "." + IndexFieldType.NUMBER.toString();

    public static final String PUBLISH_FROM_FIELDNAME = "publishfrom";

    public static final String PUBLISH_TO_FIELDNAME = "publishto";

    public static final String TIMESTAMP_FIELDNAME = "timestamp";

    public static final String STATUS_FIELDNAME = "status";

    public static final String PRIORITY_FIELDNAME = "priority";

    public static final String ASSIGNMENT_DUE_DATE_FIELDNAME = "assignmentduedate";

    public static final String OWNER_FIELDNAME = "owner";

    public static final String MODIFIER_FIELDNAME = "modifier";

    public static final String ASSIGNEE_FIELDNAME = "assignee";

    public static final String ASSIGNER_FIELDNAME = "assigner";

    public static final String TITLE_FIELDNAME = "title";

    public static final String CONTENTLOCATION_APPROVED_FIELDNAME = "contentlocations_approved";

    public static final String CONTENTLOCATION_UNAPPROVED_FIELDNAME = "contentlocations_unapproved";

    public static final String CONTENT_ACCESS_READ_FIELDNAME = "access_read";

    public static final String CONTENT_ACCESS_UPDATE_FIELDNAME = "access_update";

    public static final String CONTENT_ACCESS_DELETE_FIELDNAME = "access_delete";

    public static final String CONTENT_CATEGORY_ACCESS_BROWSE_FIELDNAME = "access_category_browse";

    public static final String CONTENT_CATEGORY_ACCESS_APPROVE_FIELDNAME = "access_category_approve";

    public static final String CONTENT_CATEGORY_ACCESS_ADMINISTRATE_FIELDNAME = "access_category_administrate";

    public static final String USER_KEY_POSTFIX = "_key";

    public static final String USER_NAME_POSTFIX = "_name";

    public static final String USER_QUALIFIED_NAME_POSTFIX = "_qualifiedName";

    public static final String CATEGORY_FIELD_PREFIX = "category";

    public static final String CONTENT_TYPE_PREFIX = "contenttype";

    public static final String NON_ANALYZED_FIELD_POSTFIX = "._tokenized";

    public static final String CONTENTDATA_PREFIX = "data_";

    public static final String CONTENTDATA_PREFIX_ALIAS_FOR_BW_COMPATABILITY = "contentdata_";

    public static final String ATTACHMENT_FIELDNAME = "attachment";

    public static final String ATTACHMENT_ALIAS_FOR_BW_COMPATABILITY = "fulltext";

    public static final String CONTENTKEY_FIELDNAME = "key";

    public static final String CONTENT_CREATED = "created";

    public static final String CONTENT_MODIFIED = "modified";

    public static final String CATEGORY_KEY_FIELDNAME = CATEGORY_FIELD_PREFIX + "key";

    public static final String CATEGORY_NAME_FIELDNAME = CATEGORY_FIELD_PREFIX + "_name";

    public static final String CONTENTTYPE_KEY_FIELDNAME = CONTENT_TYPE_PREFIX + "key";

    public static final String CONTENTTYPE_NAME_FIELDNAME = CONTENT_TYPE_PREFIX;

    public static final String ORDERBY_FIELDNAME_POSTFIX = "orderby";

}
