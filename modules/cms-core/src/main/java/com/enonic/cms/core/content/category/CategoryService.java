/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;


public interface CategoryService
{
    public CategoryKey storeNewCategory( StoreNewCategoryCommand command );

    public void updateCategory( UpdateCategoryCommand command );

    public void synchronizeCategoryACL_withoutRequiresNewPropagation_for_test_only( SynchronizeCategoryACLCommand command );

    public void synchronizeCategoryACL( SynchronizeCategoryACLCommand command );

    void synchronizeContentACL_withoutRequiresNewPropagation_for_test_only( SynchronizeContentACLCommand command );

    void synchronizeContent( SynchronizeContentACLCommand command );

    void modifyCategoryACL_withoutRequiresNewPropagation_for_test_only( ModifyCategoryACLCommand command );

    void modifyCategoryACL( ModifyCategoryACLCommand command );

    void modifyContentACL_withoutRequiresNewPropagation_for_test_only( ModifyContentACLCommand command );

    void modifyContentACL( ModifyContentACLCommand command );

    void deleteCategory( DeleteCategoryCommand command );

    void moveCategory( MoveCategoryCommand command );
}
