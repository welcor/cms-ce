package com.enonic.cms.core.search.builder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 12:07 PM
 */
class IndexFieldNameConstants
{
    public final static char PRE_SEPARATOR = '_';

    public final static char POST_SEPARATOR = '_';

    public static final String NUMERIC_FIELD_POSTFIELD = PRE_SEPARATOR + "numeric";

    public static final String ORDER_FIELD_PREFIX = "orderby" + POST_SEPARATOR;

    public static final String CATEGORY_FIELD_PREFIX = "category";

    public static final String SECTION_FIELD_PREFIX = PRE_SEPARATOR + "contentlocations";

    public static final String CONTENT_TYPE_PREFIX = "contenttype";

    public static final String NON_ANALYZED_FIELD_POSTFIX = "._tokenized";

    protected static final String INDEX_FIELDNAME_PROPERTY_SEPARATOR = "_";

    protected static final String QUERY_LANGUAGE_PROPERTY_SEPARATOR = "/";


}
