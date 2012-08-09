/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Mar 9, 2010
 */
public class StoreNewCategoryCommand
{
    private UserKey creator;

    private ContentTypeKey contentType;

    private CategoryKey parentCategory;

    private List<ContentTypeKey> allowedContentTypes;

    private String name;

    private boolean autoApprove = false;

    private CategoryACL categoryACL = null;

    private String description;

    private LanguageKey language;

    public void setCreator( UserKey creator )
    {
        this.creator = creator;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public void setContentType( ContentTypeKey contentType )
    {
        this.contentType = contentType;
    }

    public ContentTypeKey getContentType()
    {
        return contentType;
    }

    public void setParentCategory( CategoryKey parentCategory )
    {
        this.parentCategory = parentCategory;
    }

    public CategoryKey getParentCategory()
    {
        return parentCategory;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setAutoApprove( boolean autoApprove )
    {
        this.autoApprove = autoApprove;
    }

    public boolean getAutoApprove()
    {
        return autoApprove;
    }

    public void addAccessRights( Iterable<CategoryAccessControl> accessRights )
    {
        for ( CategoryAccessControl ar : accessRights )
        {
            addAccessRight( ar );
        }
    }

    public void addAccessRight( CategoryAccessControl accessRight )
    {
        if ( categoryACL == null )
        {
            categoryACL = new CategoryACL();
        }
        categoryACL.add( accessRight );
    }

    public CategoryACL getCategoryACL()
    {
        return categoryACL;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public List<ContentTypeKey> getAllowedContentTypes()
    {
        return allowedContentTypes;
    }

    public void addAllowedContentType( ContentTypeKey allowedContentType )
    {
        if ( this.allowedContentTypes == null )
        {
            this.allowedContentTypes = new ArrayList<ContentTypeKey>();
        }
        this.allowedContentTypes.add( allowedContentType );
    }

    public LanguageKey getLanguage()
    {
        return language;
    }

    public void setLanguage( LanguageKey language )
    {
        this.language = language;
    }
}
