package com.enonic.cms.core.structure.menuitem;

import org.junit.Test;

import static org.junit.Assert.*;


public class MenuItemServiceImpl2Test
{

    @Test
    public void given_removeCommand_but_not_setHomeCommand_then_same()
    {
        MenuItemServiceCommand[] commands =
            new MenuItemServiceCommand[]{new AddContentToSectionCommand(), new RemoveContentsFromSectionCommand()};

        // exercise
        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertSame( commands, sorted );
    }

    @Test
    public void given_setHomeCommand_but_no_removeCommand_then_same()
    {
        MenuItemServiceCommand[] commands = new MenuItemServiceCommand[]{new AddContentToSectionCommand(), new SetContentHomeCommand()};

        // exercise
        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertSame( commands, sorted );
    }

    @Test
    public void given_neither_setHomeCommand_and_removeCommand_then_same()
    {
        MenuItemServiceCommand[] commands =
            new MenuItemServiceCommand[]{new AddContentToSectionCommand(), new AddContentToSectionCommand()};

        // exercise
        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertSame( commands, sorted );
    }

    @Test
    public void given_empty_then_same()
    {
        MenuItemServiceCommand[] commands = new MenuItemServiceCommand[]{};

        // exercise
        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertSame( commands, sorted );
    }

    @Test
    public void given_setHome_before_remove_then_remove_comes_first()
    {
        MenuItemServiceCommand[] commands =
            new MenuItemServiceCommand[]{new SetContentHomeCommand(), new RemoveContentsFromSectionCommand()};

        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertEquals( 2, sorted.length );
        assertTrue( sorted[0].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[1].getClass().equals( SetContentHomeCommand.class ) );
    }

    @Test
    public void given_removeCommand_before_setHomeCommand_then_same_order()
    {
        MenuItemServiceCommand[] commands =
            new MenuItemServiceCommand[]{new RemoveContentsFromSectionCommand(), new SetContentHomeCommand()};

        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertEquals( 2, sorted.length );
        assertTrue( sorted[0].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[1].getClass().equals( SetContentHomeCommand.class ) );
    }

    @Test
    public void x1()
    {
        MenuItemServiceCommand[] commands = new MenuItemServiceCommand[]{new SetContentHomeCommand(), new AddContentToSectionCommand(),
            new RemoveContentsFromSectionCommand()};

        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertEquals( 3, sorted.length );
        assertTrue( sorted[0].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[1].getClass().equals( SetContentHomeCommand.class ) );
        assertTrue( sorted[2].getClass().equals( AddContentToSectionCommand.class ) );
    }

    @Test
    public void x2()
    {
        MenuItemServiceCommand[] commands =
            new MenuItemServiceCommand[]{new SetContentHomeCommand(), new RemoveContentsFromSectionCommand(),
                new AddContentToSectionCommand()};

        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertEquals( 3, sorted.length );
        assertTrue( sorted[0].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[1].getClass().equals( SetContentHomeCommand.class ) );
        assertTrue( sorted[2].getClass().equals( AddContentToSectionCommand.class ) );
    }

    @Test
    public void x3()
    {
        MenuItemServiceCommand[] commands = new MenuItemServiceCommand[]{new AddContentToSectionCommand(), new SetContentHomeCommand(),
            new RemoveContentsFromSectionCommand()};

        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertEquals( 3, sorted.length );
        assertTrue( sorted[0].getClass().equals( AddContentToSectionCommand.class ) );
        assertTrue( sorted[1].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[2].getClass().equals( SetContentHomeCommand.class ) );
    }

    @Test
    public void x4()
    {
        MenuItemServiceCommand[] commands = new MenuItemServiceCommand[]{new AddContentToSectionCommand(), new SetContentHomeCommand(),
            new ApproveContentInSectionCommand(), new RemoveContentsFromSectionCommand(), new OrderContentsInSectionCommand()};

        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertEquals( 5, sorted.length );
        assertTrue( sorted[0].getClass().equals( AddContentToSectionCommand.class ) );
        assertTrue( sorted[1].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[2].getClass().equals( SetContentHomeCommand.class ) );
        assertTrue( sorted[3].getClass().equals( ApproveContentInSectionCommand.class ) );
        assertTrue( sorted[4].getClass().equals( OrderContentsInSectionCommand.class ) );
    }

    @Test
    public void x5()
    {
        MenuItemServiceCommand[] commands = new MenuItemServiceCommand[]{new AddContentToSectionCommand(), new SetContentHomeCommand(),
            new ApproveContentInSectionCommand(), new RemoveContentsFromSectionCommand(), new SetContentHomeCommand(),
            new OrderContentsInSectionCommand(), new RemoveContentsFromSectionCommand()};

        MenuItemServiceCommand[] sorted = new MenuItemServiceImpl().moveRemoveCommandsBeforeAnySetHomeCommands( commands );

        assertEquals( 7, sorted.length );
        assertTrue( sorted[0].getClass().equals( AddContentToSectionCommand.class ) );
        assertTrue( sorted[1].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[2].getClass().equals( RemoveContentsFromSectionCommand.class ) );
        assertTrue( sorted[3].getClass().equals( SetContentHomeCommand.class ) );
        assertTrue( sorted[4].getClass().equals( ApproveContentInSectionCommand.class ) );
        assertTrue( sorted[5].getClass().equals( SetContentHomeCommand.class ) );
        assertTrue( sorted[6].getClass().equals( OrderContentsInSectionCommand.class ) );
    }

}
