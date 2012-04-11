package com.enonic.cms.core.structure.menuitem;


import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.structure.SiteEntity;

import static org.junit.Assert.*;

public class MenuItemKeysByPathResolverTest
{

    private SiteEntity site_0;

    @Before
    public void before()
    {
        site_0 = createSite();
    }

    @Test
    public void getPageKeyByPath_given_top_level_menu_item_with_no_children_when_path_is_top_level_menu_item_and_does_not_ends_with_slash_then_top_level_menu_item_is_returned()
    {
        // setup
        MenuItemEntity menuItem_1 = createTopLevelMenuItem( 1, "menuItem-1" );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( menuItem_1 );
        assertEquals( menuItem_1.getKey().toString(), resolver.getPageKeyByPath( "/menuItem-1") );
    }

    @Test
    public void getPageKeyByPath_given_top_level_menu_item_with_no_children_when_path_is_top_level_menu_item_and_ends_with_slash_then_empty_is_returned()
    {
        // setup
        MenuItemEntity menuItem_1 = createTopLevelMenuItem( 1, "menuItem-1" );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( menuItem_1 );
        assertEquals( "", resolver.getPageKeysByPath( "/menuItem-1/" ) );
    }

    @Test
    public void getPageKeyByPath_given_top_level_menu_item_with_two_children_when_path_is_top_level_menu_item_and_ends_with_slash_then_only_children_is_returned()
    {
        // setup
        MenuItemEntity menuItem_1 = createTopLevelMenuItem( 1, "menuItem-1" );
        createMenuItem( 2, "menuItem-1-1", menuItem_1 );
        createMenuItem( 3, "menuItem-1-2", menuItem_1 );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( menuItem_1 );
        assertEquals( "2,3", resolver.getPageKeysByPath( "/menuItem-1/" ) );
    }

    @Test
    public void getPageKeyByPath_given_top_level_menu_item_with_two_children_when_path_is_top_level_menu_item_and_does_not_ends_with_slash_then_only_menu_item_specified_by_path_is_returned()
    {
        // setup
        MenuItemEntity menuItem_1 = createTopLevelMenuItem( 1, "menuItem-1" );
        createMenuItem( 2, "menuItem-1-1", menuItem_1 );
        createMenuItem( 3, "menuItem-1-2", menuItem_1 );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( menuItem_1 );
        assertEquals( "1", resolver.getPageKeyByPath( "/menuItem-1" ) );
    }

    @Test
    public void getPageKeyByPath_grand_children_is_not_returned()
    {
        // setup
        MenuItemEntity menuItem_1 = createTopLevelMenuItem( 1, "menuItem-1" );

        createMenuItem( 2, "menuItem-1-1", menuItem_1 );
        MenuItemEntity menuItem_1_2 = createMenuItem( 3, "menuItem-1-2", menuItem_1 );
        createMenuItem( 4, "menuItem-1-2-1", menuItem_1_2 );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( menuItem_1 );
        assertEquals( "1", resolver.getPageKeyByPath( "/menuItem-1" ) );
    }

    @Test
    public void getPageKeyByPath_given_root_slash_then_returns_top_level_menu_items()
    {
        // setup
        MenuItemEntity news = createTopLevelMenuItem( 1, "news" );
        createTopLevelMenuItem( 2, "health" );
        MenuItemEntity news_world = createMenuItem( 3, "world", news );
        createMenuItem( 4, "politics", news );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( news_world );
        assertEquals( "1,2", resolver.getPageKeysByPath( "/" ) );
    }

    @Test
    public void getPageKeyByPath_given_dot_slash_then_returns_children_of_menu_item()
    {
        // setup
        MenuItemEntity news = createTopLevelMenuItem( 1, "news" );
        MenuItemEntity news_world = createMenuItem( 2, "world", news );
        createMenuItem( 3, "africa", news_world );
        createMenuItem( 4, "europe", news_world );
        createMenuItem( 5, "asia", news_world );
        createMenuItem( 6, "america", news_world );
        createMenuItem( 7, "australia", news_world );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( news_world );
        assertEquals( "3,4,5,6,7", resolver.getPageKeysByPath( "./" ) );
    }

    @Test
    public void getPageKeyByPath_is_case_in_sensitive()
    {
        // setup
        MenuItemEntity news = createTopLevelMenuItem( 1, "news" );
        MenuItemEntity news_world = createMenuItem( 2, "world", news );
        createMenuItem( 3, "Politics", news );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( news_world );
        assertEquals( "3", resolver.getPageKeyByPath( "../politics" ) );
    }

    @Test
    public void getPageKeyByPath_given_dot_slash_child_menu_item_as_path_when_menu_item_is_parent_then_child_is_returned()
    {
        // setup
        MenuItemEntity news = createTopLevelMenuItem( 1, "news" );
        createMenuItem( 2, "world", news );

        // exercise & verify
        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( news );
        assertEquals( "2", resolver.getPageKeyByPath( "./world" ) );
    }

    @Test
    public void testGetPageKeyByPath()
    {

        String keys;
        MenuItemEntity topLevelMenuItem = createTopLevelMenuItem( 1, "Top menu-item" );

        MenuItemEntity menuItem1 = createMenuItem( 2, "child menu-item 1", topLevelMenuItem );
        createMenuItem( 3, "child menu-item 2", topLevelMenuItem );

        MenuItemKeysByPathResolver resolver = new MenuItemKeysByPathResolver( topLevelMenuItem );

        keys = resolver.getPageKeysByPath( "/" );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "/Top menu-item" );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "/Top menu-item/child menu-item 1" );
        assertEquals( "2", keys );

        keys = resolver.getPageKeyByPath( "/Top menu-item/child menu-item 2" );
        assertEquals( "3", keys );

        keys = resolver.getPageKeyByPath( "/Top menu-item/child menu-item 2/nope" );
        assertEquals( "", keys );

        keys = resolver.getPageKeysByPath( "/../" );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "/nope" );
        assertEquals( "", keys );

        keys = resolver.getPageKeysByPath( "/Top menu-item/" );
        assertEquals( "2,3", keys );

        keys = resolver.getPageKeyByPath( "." );
        assertEquals( "1", keys );

        keys = resolver.getPageKeysByPath( "./" );
        assertEquals( "2,3", keys );

        keys = resolver.getPageKeyByPath( "./child menu-item 2" );
        assertEquals( "3", keys );

        keys = resolver.getPageKeyByPath( "./../Top menu-item/child menu-item 2" );
        assertEquals( "3", keys );

        keys = resolver.getPageKeyByPath( "./../../Top menu-item/child menu-item 2" );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( ".." );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "../." );
        assertEquals( "", keys );

        keys = resolver.getPageKeysByPath( "../" );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "../nope" );
        assertEquals( "", keys );

        keys = resolver.getPageKeysByPath( "../../" );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "../.." );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "../../.." );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "nope" );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "./nope" );
        assertEquals( "", keys );

        resolver = new MenuItemKeysByPathResolver( menuItem1 );

        keys = resolver.getPageKeyByPath( "." );
        assertEquals( "2", keys );

        keys = resolver.getPageKeyByPath( "./" );
        assertEquals( "2", keys );

        keys = resolver.getPageKeysByPath( "./" );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( ".." );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "../" );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "./.." );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "./../" );
        assertEquals( "1", keys );

        keys = resolver.getPageKeysByPath( "../" );
        assertEquals( "2,3", keys );

        keys = resolver.getPageKeysByPath( "./../" );
        assertEquals( "2,3", keys );

        keys = resolver.getPageKeysByPath( "./.././././" );
        assertEquals( "2,3", keys );

        keys = resolver.getPageKeysByPath( "./././.././././" );
        assertEquals( "2,3", keys );

        keys = resolver.getPageKeyByPath( "../child menu-item 1" );
        assertEquals( "2", keys );

        keys = resolver.getPageKeyByPath( "../child menu-item 2" );
        assertEquals( "3", keys );

        keys = resolver.getPageKeyByPath( "./../child menu-item 1" );
        assertEquals( "2", keys );

        keys = resolver.getPageKeyByPath( "./../child menu-item 2" );
        assertEquals( "3", keys );

        keys = resolver.getPageKeyByPath( "../../Top menu-item" );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "./../../Top menu-item" );
        assertEquals( "1", keys );

        keys = resolver.getPageKeyByPath( "../.." );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "../../.." );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "nope" );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "./nope" );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "./nope/." );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "./nope/.." );
        assertEquals( "", keys );

        keys = resolver.getPageKeyByPath( "../nope" );
        assertEquals( "", keys );
    }

    private SiteEntity createSite()
    {
        SiteEntity site = new SiteEntity();
        site.setName( "MySite" );
        site.setKey( 0 );
        return site;
    }

    private MenuItemEntity createTopLevelMenuItem( int key, String name )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( new MenuItemKey( key ) );
        menuItem.setName( name );
        menuItem.setParent( null );
        menuItem.setSite( site_0 );
        site_0.addTopMenuItem( menuItem );
        return menuItem;
    }

    private MenuItemEntity createMenuItem( int key, String name, MenuItemEntity parent )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( new MenuItemKey( key ) );
        menuItem.setName( name );
        menuItem.setParent( parent );
        parent.addChild( menuItem );
        menuItem.setSite( site_0 );
        return menuItem;
    }

}
