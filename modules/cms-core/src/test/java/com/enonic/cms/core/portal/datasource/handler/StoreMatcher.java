/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public abstract class StoreMatcher<T> extends BaseMatcher<T>
{
    protected abstract void store(T value);

    @SuppressWarnings({ "unchecked" })
    public boolean matches(Object value) {
        store((T)value);
        return true;
    }

    public void describeTo(Description description) {
        description.appendText("stores value");
    }
}
