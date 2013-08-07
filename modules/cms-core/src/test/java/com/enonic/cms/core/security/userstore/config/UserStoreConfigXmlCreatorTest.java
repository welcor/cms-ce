/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.config;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfigField;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;

import static org.junit.Assert.*;

public class UserStoreConfigXmlCreatorTest
{
    @Test
    public void testCreateDocumentAddressField()
    {
        Collection<UserStoreConfigField> userFieldConfigs = new TreeSet<UserStoreConfigField>();

        UserStoreConfigField fieldConfig = new UserStoreConfigField( UserFieldType.ADDRESS );
        fieldConfig.setReadOnly( true );
        userFieldConfigs.add( fieldConfig );

        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.setUserFieldConfigs( userFieldConfigs );

        Document checked = UserStoreConfigXmlCreator.createDocument( userStoreConfig );
        Element rootElement = checked.getRootElement();
        Element userFields = JDOMUtil.getElement( rootElement, "user-fields" );

        List<Element> children = userFields.getChildren();
        for ( Element child : children )
        {
            assertEquals( "true", child.getAttributeValue( "readonly" ) );
            assertEquals( "false", child.getAttributeValue( "required" ) );
            assertEquals( "false", child.getAttributeValue( "remote" ) );
            assertEquals( "true", child.getAttributeValue( "iso" ) );
        }
    }

    @Test
    public void testCreateDocumentFirstnameField()
    {
        Collection<UserStoreConfigField> userFieldConfigs = new TreeSet<UserStoreConfigField>();

        UserStoreConfigField fieldConfig = new UserStoreConfigField( UserFieldType.FIRST_NAME );
        fieldConfig.setRequired( true );
        userFieldConfigs.add( fieldConfig );

        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.setUserFieldConfigs( userFieldConfigs );

        Document checked = UserStoreConfigXmlCreator.createDocument( userStoreConfig );
        Element rootElement = checked.getRootElement();
        Element userFields = JDOMUtil.getElement( rootElement, "user-fields" );

        List<Element> children = userFields.getChildren();
        for ( Element child : children )
        {
            assertEquals( "false", child.getAttributeValue( "readonly" ) );
            assertEquals( "true", child.getAttributeValue( "required" ) );
            assertEquals( "false", child.getAttributeValue( "remote" ) );
        }
    }
}
