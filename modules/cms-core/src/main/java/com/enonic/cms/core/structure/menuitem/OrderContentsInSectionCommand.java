/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.structure.menuitem;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentKey;

public class OrderContentsInSectionCommand
    implements MenuItemServiceCommand
{
    private List<ContentKey> wantedOrder = new ArrayList<ContentKey>();

    private MenuItemKey sectionKey;

    public OrderContentsInSectionCommand()
    {
    }

    public List<ContentKey> getWantedOrder()
    {
        return wantedOrder;
    }

    public void addContent( ContentKey content )
    {
        this.wantedOrder.add( content );
    }

    public void setWantedOrder( List<ContentKey> wantedOrder )
    {
        this.wantedOrder = wantedOrder;
    }

    public MenuItemKey getSectionKey()
    {
        return sectionKey;
    }

    public void setSectionKey( final MenuItemKey sectionKey )
    {
        this.sectionKey = sectionKey;
    }
}
