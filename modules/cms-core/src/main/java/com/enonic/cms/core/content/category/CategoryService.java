/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;


/**
 * Mar 9, 2010
 */
public interface CategoryService
{
    public CategoryKey storeNewCategory( StoreNewCategoryCommand command );

    public void updateCategory( UpdateCategoryCommand command );

    public void synchronizeCategoryACL( SynchronizeCategoryACLCommand command );

    public void synchronizeCategoryACLInNewTX( SynchronizeCategoryACLCommand command );

    void syncronizeContentACL( SynchronizeContentACLCommand command );

    void syncronizeContentACLInNewTx( SynchronizeContentACLCommand command );

    void modifyCategoryACL( ModifyCategoryACLCommand command );

    void modifyCategoryACLInNewTX( ModifyCategoryACLCommand command );

    void modifyContentACL( ModifyContentACLCommand command );

    void modifyContentACLInNewTX( ModifyContentACLCommand command );

    void deleteCategory( DeleteCategoryCommand command );

    void moveCategory( MoveCategoryCommand command );
}
