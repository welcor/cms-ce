/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import static org.junit.Assert.assertArrayEquals;

public class CategoryEntityTest
    extends TestCase
{

    public void testGetAllChildrenWithNullChildren()
    {

        CategoryEntity root = createCategory( 0, "root", null );

        List<CategoryEntity> actualChildren = root.getDescendants();
        assertNotNull( actualChildren );
        assertEquals( 0, actualChildren.size() );
    }

    public void testAllChildrenWithOneChild()
    {

        CategoryEntity root = createCategory( 0, "root", null );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root );

        Set<CategoryEntity> expectedChildren = new HashSet<CategoryEntity>();
        expectedChildren.add( cat_1 );

        List<CategoryEntity> actualChildren = root.getDescendants();
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new CategoryEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new CategoryEntity[actualChildren.size()] ) );
    }

    public void testGetAllChildren()
    {

        CategoryEntity root = createCategory( 0, "root", null );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root );
        CategoryEntity cat_1_1 = createCategory( 2, "0.1.1", cat_1 );
        CategoryEntity cat_1_2 = createCategory( 3, "0.1.2", cat_1 );
        CategoryEntity cat_1_2_1 = createCategory( 4, "0.1.2.1", cat_1_2 );
        CategoryEntity cat_2 = createCategory( 5, "0.2", root );
        CategoryEntity cat_2_1 = createCategory( 6, "0.2", cat_2 );

        List<CategoryEntity> expectedChildren = new ArrayList<CategoryEntity>();
        expectedChildren.add( cat_1 );
        expectedChildren.add( cat_1_1 );
        expectedChildren.add( cat_1_2 );
        expectedChildren.add( cat_1_2_1 );
        expectedChildren.add( cat_2 );
        expectedChildren.add( cat_2_1 );

        List<CategoryEntity> actualChildren = root.getDescendants();
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new CategoryEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new CategoryEntity[actualChildren.size()] ) );
    }

    public void testGetAllChildren2()
    {

        CategoryEntity root = createCategory( 0, "root", null );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root );
        CategoryEntity cat_1_1 = createCategory( 2, "0.1.1", cat_1 );
        CategoryEntity cat_1_2 = createCategory( 3, "0.1.2", cat_1 );
        CategoryEntity cat_1_2_1 = createCategory( 4, "0.1.2.1", cat_1_2 );
        CategoryEntity cat_2 = createCategory( 5, "0.2", root );
        CategoryEntity cat_2_1 = createCategory( 6, "0.2", cat_2 );

        List<CategoryEntity> expectedChildren = new ArrayList<CategoryEntity>();
        expectedChildren.add( cat_1_1 );
        expectedChildren.add( cat_1_2 );
        expectedChildren.add( cat_1_2_1 );

        List<CategoryEntity> actualChildren = cat_1.getDescendants();
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new CategoryEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new CategoryEntity[actualChildren.size()] ) );
    }

    public void testSetUnitOnDescendants()
    {
        UnitKey unitKey1 = new UnitKey( 1 );
        UnitKey unitKey2 = new UnitKey( 2 );

        UnitEntity unit1 = new UnitEntity();
        unit1.setKey( unitKey1 );

        CategoryEntity root = createCategory( 0, "root", null, unit1 );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root, unit1 );
        CategoryEntity cat_1_1 = createCategory( 2, "0.1.1", cat_1, unit1 );
        CategoryEntity cat_1_2 = createCategory( 3, "0.1.2", cat_1_1, unit1 );
        CategoryEntity cat_1_2_1 = createCategory( 4, "0.1.2.1", cat_1_2, unit1 );

        assertEquals( unitKey1, root.getUnit().getKey() );
        assertEquals( unitKey1, cat_1_2_1.getUnit().getKey() );
        assertEquals( unitKey1, cat_1.getUnit().getKey() );

        UnitEntity unit2 = new UnitEntity();
        unit2.setKey( unitKey2 );

        cat_1_1.setUnitOnDescendants( unit2 );

        assertEquals( unitKey1, root.getUnit().getKey() );
        assertEquals( unitKey1, cat_1.getUnit().getKey() );

        assertEquals( unitKey2, cat_1_1.getUnit().getKey() );
        assertEquals( unitKey2, cat_1_2.getUnit().getKey() );
        assertEquals( unitKey2, cat_1_2_1.getUnit().getKey() );
    }

    public void testIsSubCategory()
    {
        CategoryEntity root = createCategory( 0, "root", null );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root );
        CategoryEntity cat_1_1 = createCategory( 2, "0.1.1", cat_1 );
        CategoryEntity cat_1_2 = createCategory( 3, "0.1.2", cat_1 );
        CategoryEntity cat_1_2_1 = createCategory( 4, "0.1.2.1", cat_1_2 );
        CategoryEntity cat_2 = createCategory( 5, "0.2", root );
        CategoryEntity cat_2_1 = createCategory( 6, "0.2", cat_2 );

        assertFalse( root.isSubCategoryOf( cat_1 ) );

        assertTrue( cat_1.isSubCategoryOf( root ) );
        assertTrue( cat_1_2_1.isSubCategoryOf( cat_1 ) );
        assertFalse( cat_1_2_1.isSubCategoryOf( cat_1_1 ) );
        assertTrue( cat_2_1.isSubCategoryOf( root ) );

        assertTrue( cat_2_1.isSubCategoryOf( root ) );
        assertFalse( cat_2_1.isSubCategoryOf( cat_1 ) );
    }

    private CategoryEntity createCategory( int key, String name, CategoryEntity parent )
    {
        return createCategory( key, name, parent, null );
    }

    private CategoryEntity createCategory( int key, String name, CategoryEntity parent, UnitEntity unit )
    {
        CategoryEntity cat = new CategoryEntity();
        cat.setKey( new CategoryKey( key ) );
        cat.setName( name );
        cat.setUnit( unit );
        if ( parent != null )
        {
            cat.setParent( parent );
        }
        return cat;
    }
}
