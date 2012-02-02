package com.enonic.cms.core.search.builder;

import java.util.Collection;
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
public class ContentIndexDataCustomDataBuilder
    extends AbstractIndexDataBuilder
{

    public void build( final XContentBuilder result, final Collection<UserDefinedField> userDefinedFields )
        throws Exception
    {
        Map<String, Set<String>> userDefinedValuesMap = getUserDefinedValuesMap( userDefinedFields );

        for ( String field : userDefinedValuesMap.keySet() )
        {
            final Set<String> values = userDefinedValuesMap.get( field );

            addStringSet( field, values, result, true, true );
        }
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
