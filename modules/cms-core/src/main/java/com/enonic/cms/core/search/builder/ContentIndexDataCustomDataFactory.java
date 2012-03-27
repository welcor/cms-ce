package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Maps;

import com.enonic.cms.core.content.index.UserDefinedField;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/1/12
 * Time: 3:18 PM
 */
public class ContentIndexDataCustomDataFactory
    extends AbstractIndexDataFactory
{

    public void build( final XContentBuilder result, final Collection<UserDefinedField> userDefinedFields )
        throws Exception
    {
        Set<String> allUserdataValue = new HashSet<String>();

        Map<String, Set<String>> userDefinedValuesMap = getUserDefinedValuesMap( userDefinedFields );

        for ( String field : userDefinedValuesMap.keySet() )
        {
            final Set<String> values = userDefinedValuesMap.get( field );

            addStringSet( field, values, result, true, true );

            allUserdataValue.addAll( values );
        }

        addAllUserdataField( result, allUserdataValue );
    }

    private void addAllUserdataField( final XContentBuilder result, final Set<String> allUserdataValue )
        throws Exception
    {
        StringBuffer buf = new StringBuffer();

        for ( String value : allUserdataValue )
        {
            buf.append( value + " " );
        }

        addStringSet( ALL_USERDATA_FIELDNAME, allUserdataValue, result, true, false );
    }

    private Map<String, Set<String>> getUserDefinedValuesMap( Collection<UserDefinedField> userDefinedFields )
    {
        Map<String, Set<String>> userDefinedValuesMap = Maps.newHashMap();

        for ( UserDefinedField field : userDefinedFields )
        {
            Set<String> simpleTexts = userDefinedValuesMap.get( field.getName() );

            if ( simpleTexts == null )
            {
                simpleTexts = new TreeSet<String>();
                userDefinedValuesMap.put( field.getName(), simpleTexts );
            }

            simpleTexts.add( field.getValue().toString() );
        }

        return userDefinedValuesMap;
    }


}
