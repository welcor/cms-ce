/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.adminweb;


import java.util.LinkedHashMap;
import java.util.Map;

import com.enonic.cms.api.plugin.userstore.UserStoreConfigField;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.api.plugin.userstore.UserStoreConfig;
import com.enonic.cms.api.plugin.userstore.UserFieldType;

class UserFormEditableFieldsResolver
{
    private UserStoreEntity userStore;

    private UserStoreConfig userStoreConfig;

    private FormAction formAction;

    private boolean canCreateUserConnectorPolicy = false;

    private boolean canUpdateUserConnectorPolicy = false;

    UserFormEditableFieldsResolver( UserStoreEntity userStore, FormAction formAction, boolean canCreateUserConnectorPolicy,
                                    boolean canUpdateUserConnectorPolicy )
    {
        this.userStore = userStore;
        this.userStoreConfig = userStore.getConfig();
        this.formAction = formAction;
        this.canCreateUserConnectorPolicy = canCreateUserConnectorPolicy;
        this.canUpdateUserConnectorPolicy = canUpdateUserConnectorPolicy;
    }

    void resolveAndApply( final Map xslParams )
    {
        //noinspection unchecked
        xslParams.putAll( resolveEditnessForFields() );
    }

    Map resolveEditnessForFields()
    {
        Map<String, Boolean> map = new LinkedHashMap<String, Boolean>();
        map.put( "is-email-editable", isEmailEditable() );
        map.put( "is-firstname-editable", isFirstNameEditable() );
        map.put( "is-lastname-editable", isLastNameEditable() );
        map.put( "is-middlename-editable", isMiddleNameEditable() );
        map.put( "is-nick-editable", isNickNameEditable() );
        map.put( "is-birthday-editable", isBirthdayEditable() );
        map.put( "is-country-editable", isCountryEditable() );
        map.put( "is-description-editable", isDescriptionEditable() );
        map.put( "is-initials-editable", isInitialsEditable() );
        map.put( "is-globalposition-editable", isGlobalPositionEditable() );
        map.put( "is-htmlemail-editable", isHtmlEmailEditable() );
        map.put( "is-locale-editable", isLocaleEditable() );
        map.put( "is-personalid-editable", isPersonalIdEditable() );
        map.put( "is-memberid-editable", isMemberIdEditable() );
        map.put( "is-organization-editable", isOrganizationEditable() );
        map.put( "is-phone-editable", isPhoneEditable() );
        map.put( "is-fax-editable", isFaxEditable() );
        map.put( "is-mobile-editable", isMobileEditable() );
        map.put( "is-prefix-editable", isPrefixEditable() );
        map.put( "is-suffix-editable", isSuffixEditable() );
        map.put( "is-title-editable", isTitleEditable() );
        map.put( "is-timezone-editable", isTimeZoneEditable() );
        map.put( "is-homepage-editable", isHomePageEditable() );
        map.put( "is-address-editable", isAddressEditable() );
        map.put( "is-photo-editable", isPhotoEditable() );
        map.put( "is-gender-editable", isGenderEditable() );
        return map;
    }

    boolean isEmailEditable()
    {
        if ( userStore.isLocal() )
        {
            return true;
        }
        else if ( formAction == FormAction.CREATE )
        {
            return canCreateUserConnectorPolicy;
        }
        else if ( formAction == FormAction.UPDATE )
        {
            return canUpdateUserConnectorPolicy;
        }

        return false;
    }

    boolean isGenderEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.GENDER ) );
    }

    boolean isPhotoEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.PHOTO ) );
    }

    boolean isAddressEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.ADDRESS ) );
    }

    boolean isHomePageEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.HOME_PAGE ) );
    }

    boolean isTimeZoneEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.TIME_ZONE ) );
    }

    boolean isTitleEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.TITLE ) );
    }

    boolean isSuffixEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.SUFFIX ) );
    }

    boolean isPrefixEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.PREFIX ) );
    }

    boolean isMobileEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.MOBILE ) );
    }

    boolean isFaxEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.FAX ) );
    }

    boolean isPhoneEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.PHONE ) );
    }

    boolean isOrganizationEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.ORGANIZATION ) );
    }

    boolean isMemberIdEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.MEMBER_ID ) );
    }

    boolean isPersonalIdEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.PERSONAL_ID ) );
    }

    boolean isLocaleEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.LOCALE ) );
    }

    boolean isFirstNameEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.FIRST_NAME ) );
    }

    boolean isLastNameEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.LAST_NAME ) );
    }

    boolean isNickNameEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.NICK_NAME ) );
    }

    boolean isMiddleNameEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.MIDDLE_NAME ) );
    }

    boolean isHtmlEmailEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.HTML_EMAIL ) );
    }

    boolean isGlobalPositionEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.GLOBAL_POSITION ) );
    }

    boolean isInitialsEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.INITIALS ) );
    }

    boolean isDescriptionEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.DESCRIPTION ) );
    }

    boolean isCountryEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.COUNTRY ) );
    }

    boolean isBirthdayEditable()
    {
        return isEditable( userStoreConfig.getUserFieldConfig( UserFieldType.BIRTHDAY ) );
    }

    private boolean isEditable( UserStoreConfigField config )
    {
        if ( config == null )
        {
            return false;
        }
        else if ( config.isReadOnly() )
        {
            return false;
        }
        else if ( !config.isRemote() )
        {
            return true;
        }
        else if ( userStore.isLocal() )
        {
            return true;
        }
        else if ( formAction == FormAction.CREATE )
        {
            return canCreateUserConnectorPolicy;
        }
        else if ( formAction == FormAction.UPDATE )
        {
            return canUpdateUserConnectorPolicy;
        }

        return false;
    }

    enum FormAction
    {
        CREATE, UPDATE
    }
}
