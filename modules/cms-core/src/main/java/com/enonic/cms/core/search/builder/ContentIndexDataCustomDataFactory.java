package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

        List<String> handledFieldNames = Lists.newArrayList();

        for ( UserDefinedField userDefinedField : userDefinedFields )
        {
            final String fieldName = userDefinedField.getName();

            if ( handledFieldNames.contains( fieldName ) )
            {
                continue;
            }

            Set<String> values = getAllValuesForFieldName( fieldName, userDefinedFields );

            addStringSet( IndexFieldNameResolver.normalizeFieldName( fieldName ), values, result );

            switch ( userDefinedField.getIndexFieldType() )
            {
                case STRING:
                {
                    // Always added anyway
                    break;
                }
                case NUMBER:
                {
                    translateAndAddNumericSet( IndexFieldNameResolver.getNumericsFieldName( fieldName ), values, result );
                    break;
                }
                case DATE:
                {
                    translateAndAddDateSet( IndexFieldNameResolver.getDateFieldName( fieldName ), values, result );
                    break;
                }
            }

            allUserdataValue.addAll( values );

            handledFieldNames.add( fieldName );
        }

        addAllUserdataField( result, allUserdataValue );
    }

    private Set<String> getAllValuesForFieldName( String fieldName, final Collection<UserDefinedField> userDefinedFields )
    {
        Set<String> values = Sets.newTreeSet();

        for ( UserDefinedField userDefinedField : userDefinedFields )
        {
            if ( fieldName.equals( userDefinedField.getName() ) )
            {
                values.add( userDefinedField.getValue().getText() );
            }
        }

        return values;
    }


    private void addAllUserdataField( final XContentBuilder result, final Set<String> allUserdataValue )
        throws Exception
    {
        StringBuffer buf = new StringBuffer();

        for ( String value : allUserdataValue )
        {
            buf.append( value + " " );
        }

        addStringSet( ALL_USERDATA_FIELDNAME, allUserdataValue, result );
    }


}
