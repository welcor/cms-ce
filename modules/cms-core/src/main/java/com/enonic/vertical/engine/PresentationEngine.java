/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.enonic.vertical.engine.handlers.CategoryHandler;
import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.ContentHandler;
import com.enonic.vertical.engine.handlers.ContentObjectHandler;
import com.enonic.vertical.engine.handlers.GroupHandler;
import com.enonic.vertical.engine.handlers.LanguageHandler;
import com.enonic.vertical.engine.handlers.LogHandler;
import com.enonic.vertical.engine.handlers.MenuHandler;
import com.enonic.vertical.engine.handlers.PageHandler;
import com.enonic.vertical.engine.handlers.PageTemplateHandler;
import com.enonic.vertical.engine.handlers.SectionHandler;
import com.enonic.vertical.engine.handlers.SecurityHandler;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.user.User;

@Component("presentationEngine")
public class PresentationEngine
    extends BaseEngine
    implements InitializingBean
{
    private CategoryHandler categoryHandler;

    private CommonHandler commonHandler;

    private ContentHandler contentHandler;

    private ContentObjectHandler contentObjectHandler;

    private GroupHandler groupHandler;

    private LanguageHandler languageHandler;

    private LogHandler logHandler;

    private MenuHandler menuHandler;

    private PageHandler pageHandler;

    private PageTemplateHandler pageTemplateHandler;

    private SectionHandler sectionHandler;

    private SecurityHandler securityHandler;

    private UserHandler userHandler;

    public void afterPropertiesSet()
        throws Exception
    {
        init();
    }

    private void init()
    {
        // event listeners
        menuHandler.addListener( logHandler );
    }

    public CategoryHandler getCategoryHandler()
    {
        return categoryHandler;
    }

    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public ContentObjectHandler getContentObjectHandler()
    {
        return contentObjectHandler;
    }

    public GroupHandler getGroupHandler()
    {
        return groupHandler;
    }

    public LanguageHandler getLanguageHandler()
    {
        return languageHandler;
    }

    public LogHandler getLogHandler()
    {
        return logHandler;
    }

    public MenuHandler getMenuHandler()
    {
        return menuHandler;
    }

    public PageHandler getPageHandler()
    {
        return pageHandler;
    }

    public PageTemplateHandler getPageTemplateHandler()
    {
        return pageTemplateHandler;
    }

    public SectionHandler getSectionHandler()
    {
        return sectionHandler;
    }

    public SecurityHandler getSecurityHandler()
    {
        return securityHandler;
    }

    public UserHandler getUserHandler()
    {
        return userHandler;
    }

    public Document getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory )
    {
        return categoryHandler.getSuperCategoryNames( CategoryKey.parse( categoryKey ), withContentCount, includeCategory );
    }

    public Document getCategories( User user, int key, int levels, boolean topLevel, boolean details, boolean catCount,
                                   boolean contentCount )
    {
        return getCategoryHandler().getCategories( checkUser( user ), CategoryKey.parse( key ), levels, topLevel, details, catCount,
                                                   contentCount );
    }

    /**
     * Checks if user is set and returns the user if not null, otherwise returns the anonymous user.
     *
     * @param user The user to check.
     * @return If the user passed in was <code>null</code>, the anonymous user, otherwise, the same user as was passed in.
     */
    private User checkUser( User user )
    {
        if ( user == null )
        {
            return userHandler.getAnonymousUser();
        }
        else
        {
            return user;
        }
    }

    @Autowired
    public void setCategoryHandler( CategoryHandler categoryHandler )
    {
        this.categoryHandler = categoryHandler;
    }

    @Autowired
    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    @Autowired
    public void setContentHandler( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    @Autowired
    public void setContentObjectHandler( ContentObjectHandler contentObjectHandler )
    {
        this.contentObjectHandler = contentObjectHandler;
    }

    @Autowired
    public void setGroupHandler( GroupHandler groupHandler )
    {
        this.groupHandler = groupHandler;
    }

    @Autowired
    public void setLanguageHandler( LanguageHandler languageHandler )
    {
        this.languageHandler = languageHandler;
    }

    @Autowired
    public void setLogHandler( LogHandler logHandler )
    {
        this.logHandler = logHandler;
    }

    @Autowired
    public void setMenuHandler( MenuHandler menuHandler )
    {
        this.menuHandler = menuHandler;
    }

    @Autowired
    public void setPageHandler( PageHandler pageHandler )
    {
        this.pageHandler = pageHandler;
    }

    @Autowired
    public void setPageTemplateHandler( PageTemplateHandler pageTemplateHandler )
    {
        this.pageTemplateHandler = pageTemplateHandler;
    }

    @Autowired
    public void setSectionHandler( SectionHandler sectionHandler )
    {
        this.sectionHandler = sectionHandler;
    }

    @Autowired
    public void setSecurityHandler( SecurityHandler securityHandler )
    {
        this.securityHandler = securityHandler;
    }

    @Autowired
    public void setUserHandler( UserHandler userHandler )
    {
        this.userHandler = userHandler;
    }
}
