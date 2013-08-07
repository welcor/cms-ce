/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.api.plugin.ext.userstore.UserField;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserFields;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;

public final class UserFieldTransformer
{
    private final AddressTransformer addressTransformer = new AddressTransformer();

    private final UserFieldHelper helper = new UserFieldHelper();

    private boolean transformNullValuesToBlanksForConfiguredFields = false;

    private boolean transformNullHtmlEmailValueToFalseIfConfigured = false;

    private UserStoreConfig userStoreConfig = null;

    public UserFieldTransformer transformNullValuesToBlanksForConfiguredFields( UserStoreConfig userStoreConfig )
    {
        transformNullValuesToBlanksForConfiguredFields = true;
        this.userStoreConfig = userStoreConfig;
        return this;
    }

    public UserFieldTransformer transformNullHtmlEmailValueToFalseIfConfigured( UserStoreConfig userStoreConfig )
    {
        transformNullHtmlEmailValueToFalseIfConfigured = true;
        this.userStoreConfig = userStoreConfig;
        return this;
    }

    public UserFields toUserFields( ExtendedMap formValues )
    {
        Map<String, String> map = toStringStringMap( formValues );
        UserFields fields = fromStoreableMap( map );

        FileItem item = formValues.getFileItem( UserFieldType.PHOTO.getName(), null );
        if ( item != null )
        {
            updatePhoto( fields, UserPhotoHelper.convertPhoto( item.get() ) );
        }

        return fields;
    }

    public UserFields fromStoreableMap( Map<String, String> map )
    {
        UserFields fields = new UserFields( true );
        for ( UserFieldType type : UserFieldType.values() )
        {
            updateUserField( fields, type, map );
        }

        fields.addAll( this.addressTransformer.fromStoreableMap( map ).getAll() );
        return fields;
    }

    public void updatePhoto( UserFields fields, byte[] value )
    {
        if ( value != null )
        {
            fields.add( new UserField( UserFieldType.PHOTO, value ) );
        }
    }

    public Map<String, String> toStoreableMap( UserFields fields )
    {
        HashMap<String, String> result = new HashMap<String, String>();
        for ( UserField field : fields )
        {
            if ( !field.isAddress() && !field.isPhoto() )
            {
                addSimpleField( result, field );
            }
        }

        result.putAll( this.addressTransformer.toStoreableMap( fields ) );
        return result;
    }

    private Map<String, String> toStringStringMap( ExtendedMap formValues )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        for ( Object key : formValues.keySet() )
        {
            String name = key.toString().replace( "_", "-" );
            Object value = formValues.get( key );

            if ( value instanceof String )
            {
                map.put( name, (String) value );
            }
        }

        return map;
    }

    private void updateUserField( UserFields fields, UserFieldType type, Map<String, String> map )
    {
        if ( type == UserFieldType.ADDRESS )
        {
            return;
        }

        if ( type != UserFieldType.PHOTO )
        {
            updateSimpleField( fields, type, map );
        }
    }

    private void updateSimpleField( UserFields fields, UserFieldType type, Map<String, String> map )
    {
        String value = map.get( type.getName() );

        if ( value == null && type == UserFieldType.HTML_EMAIL && transformNullHtmlEmailValueToFalseIfConfigured &&
            fieldIsConfigured( type ) )
        {
            value = "false";
        }
        else if ( value == null && transformNullValuesToBlanksForConfiguredFields && fieldIsConfigured( type ) )
        {
            value = "";
        }

        if ( value != null )
        {
            Object typedValue = this.helper.fromString( type, value );
            fields.add( new UserField( type, typedValue ) );
        }
    }

    private boolean fieldIsConfigured( UserFieldType type )
    {
        return userStoreConfig.getUserFieldConfig( type ) != null;
    }

    private void addSimpleField( Map<String, String> result, UserField field )
    {
        UserFieldType type = field.getType();
        String strValue = this.helper.toString( field );

        if ( !field.getType().isStringBased() )
        {
            addNullable( result, type.getName(), strValue );
        }
        else
        {
            addIfNotNull( result, type.getName(), strValue );
        }
    }

    private void addIfNotNull( Map<String, String> result, String name, String value )
    {
        if ( value != null )
        {
            result.put( name, value );
        }
    }

    private void addNullable( Map<String, String> result, String name, String value )
    {
        result.put( name, value );
    }
}
