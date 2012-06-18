package com.enonic.cms.core.search;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 3:58 PM
 */

public interface IndexMappingProvider
{
    public String getMapping( String indexName, String indexType );
}
