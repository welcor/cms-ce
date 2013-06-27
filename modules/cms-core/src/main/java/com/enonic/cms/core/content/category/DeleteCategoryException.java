/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


public class DeleteCategoryException
    extends RuntimeException
{
    public DeleteCategoryException( RuntimeException cause )
    {
        super( "Failed to delete category: " + cause.getMessage(), cause );
    }
}
