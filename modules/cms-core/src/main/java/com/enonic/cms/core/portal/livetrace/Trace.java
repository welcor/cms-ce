/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

public interface Trace
{
    Duration getDuration();

    void setContainer( Traces container );
}
