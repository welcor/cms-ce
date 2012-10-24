/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.el;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.preference.PreferenceService;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.MenuItemDao;


@Component
public class ExpressionFunctionsFactory
{
    private static ExpressionFunctionsFactory instance;

    private PreferenceService preferenceService;

    private TimeService timeService;

    private MenuItemDao menuItemDao;

    private ThreadLocal<ExpressionContext> context = new ThreadLocal<ExpressionContext>();

    public static ExpressionFunctionsFactory get()
    {
        return instance;
    }

    public ExpressionFunctionsFactory()
    {
        instance = this;
    }

    public void setContext( ExpressionContext value )
    {
        context.set( value );
    }

    public ExpressionContext getContext()
    {
        return context.get();
    }

    public void removeContext()
    {
        context.remove();
    }

    public ExpressionFunctions createExpressionFunctions()
    {
        ExpressionFunctions expressionFunctions = new ExpressionFunctions();
        expressionFunctions.setPreferenceService( preferenceService );
        expressionFunctions.setContext( getContext() );
        expressionFunctions.setTimeService( timeService );
        expressionFunctions.setMenuItemDao( menuItemDao );
        return expressionFunctions;
    }

    @Autowired
    public void setPreferenceService( PreferenceService preferenceService )
    {
        this.preferenceService = preferenceService;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }


    @Autowired
    public void setMenuItemDao( MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }
}
