package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    extends IndexFieldNameConstants
{

    public void create( final ContentIndexData contentIndexData, final Collection<UserDefinedField> userDefinedFields )
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

            contentIndexData.addContentData( fieldName, values );

            allUserdataValue.addAll( values );

            handledFieldNames.add( fieldName );
        }

        addAllUserdataField( contentIndexData, allUserdataValue );
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

    //TODO: Check this
    private void addAllUserdataField( final ContentIndexData contentIndexData, final Set<String> allUserdataValue )
    {
        contentIndexData.addContentData( ALL_USERDATA_FIELDNAME, allUserdataValue );
    }


}
