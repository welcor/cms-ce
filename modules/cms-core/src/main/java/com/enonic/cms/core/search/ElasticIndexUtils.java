package com.enonic.cms.core.search;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/17/11
 * Time: 8:53 AM
 */
public class ElasticIndexUtils
{


    public static String normalizeName( String name )
    {
        return name.replace( '/', '_' ).toLowerCase();
    }


    public static String toPropertyName( String value )
    {
        return ElasticContentConstants.PROPERTY_FIELD_PREFIX + normalizeName( value );
    }


}
