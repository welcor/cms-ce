/*
 * Copyright 2000-2013 Enonic AS
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
public class UpdateCategoryCommand
{
    private UserKey updater;

    private ContentTypeKey contentType;

    private CategoryKey category;

    private List<ContentTypeKey> allowedContentTypes;

    private String name;

    private boolean autoApprove = false;

    private String description;

    private LanguageKey language;

    public void setUpdater( UserKey updater )
    {
        this.updater = updater;
    }

    public UserKey getUpdater()
    {
        return updater;
    }

    public void setContentType( ContentTypeKey contentType )
    {
        this.contentType = contentType;
    }

    public ContentTypeKey getContentType()
    {
        return contentType;
    }

    public void setCategory( CategoryKey category )
    {
        this.category = category;
    }

    public CategoryKey getCategory()
    {
        return category;
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
