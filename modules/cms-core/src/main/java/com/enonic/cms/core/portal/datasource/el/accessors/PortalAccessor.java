/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el.accessors;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.portal.datasource.el.ExpressionContext;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemType;

public final class PortalAccessor
{
    public final ExpressionContext expressionContext;

    public PortalAccessor( final ExpressionContext expressionContext )
    {
        this.expressionContext = expressionContext;
    }

    public boolean getIsWindowInline()
    {
        if ( expressionContext.isPortletWindowRenderedInline() != null )
        {
            return expressionContext.isPortletWindowRenderedInline();
        }

        return false;
    }

    public String getInstanceKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null )
        {
            return expressionContext.getPortalInstanceKey().toString();
        }

        return null;
    }

    public String getLocale()
    {
        if ( expressionContext.getLocale() != null )
        {
            return expressionContext.getLocale().toString();
        }

        return null;
    }


    public String getWindowKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null && expressionContext.getPortalInstanceKey().isWindow() )
        {
            return expressionContext.getPortalInstanceKey().getWindowKey().asString();
        }

        return null;
    }

    public String getPageKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null )
        {
            final MenuItemKey menuItemKey = expressionContext.getPortalInstanceKey().getMenuItemKey();

            if ( menuItemKey != null )
            {
                return menuItemKey.toString();
            }
        }

        return null;
    }

    public String getSiteKey()
    {
        return expressionContext.getSite().getKey().toString();
    }

    public String getContentKey()
    {
        if ( expressionContext.getContentFromRequest() != null )
        {
            return expressionContext.getContentFromRequest().getKey().toString();
        }

        final MenuItemEntity menuItem = expressionContext.getMenuItem();

        if ( menuItem == null || menuItem.getType() != MenuItemType.CONTENT )
        {
            return null;
        }

        final ContentEntity content = menuItem.getContent();

        if ( content == null )
        {
            return null;
        }

        return content.getKey().toString();
    }

}
