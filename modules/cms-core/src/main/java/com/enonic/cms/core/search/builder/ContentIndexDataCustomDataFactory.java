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
        final Set<String> allUserdataFieldValues = new HashSet<String>();

        final List<String> handledFieldNames = Lists.newArrayList();

        for ( final UserDefinedField userDefinedField : userDefinedFields )
        {
            final String fieldName = userDefinedField.getName();

            final boolean alreadyHandledThisField = handledFieldNames.contains( fieldName );

            if ( alreadyHandledThisField )
            {
                continue;
            }

            final Set<String> allValuesForField = getAllValuesForFieldName( fieldName, userDefinedFields );

            contentIndexData.addContentIndexDataElement( fieldName, allValuesForField );

            allUserdataFieldValues.addAll( allValuesForField );

            handledFieldNames.add( fieldName );
        }

        addAllUserdataField( contentIndexData, allUserdataFieldValues );
    }

    private Set<String> getAllValuesForFieldName( String fieldName, final Collection<UserDefinedField> userDefinedFields )
    {
        final Set<String> values = Sets.newTreeSet();

        for ( final UserDefinedField userDefinedField : userDefinedFields )
        {
            if ( fieldName.equals( userDefinedField.getName() ) )
            {
                values.add( userDefinedField.getValue().getText() );
            }
        }

        return values;
    }

    private void addAllUserdataField( final ContentIndexData contentIndexData, final Set<String> allUserdataValue )
    {
        contentIndexData.addContentIndexDataElement( ALL_USERDATA_FIELDNAME, allUserdataValue, false );
    }


}
