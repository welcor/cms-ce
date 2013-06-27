/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.store.dao;

import com.enonic.cms.core.Specification;

public class SingleResultExpectedException
    extends RuntimeException
{
    public SingleResultExpectedException( Specification specification )
    {
        super( "Expected single result using specification: \n" + specification.toString() );
    }
}
