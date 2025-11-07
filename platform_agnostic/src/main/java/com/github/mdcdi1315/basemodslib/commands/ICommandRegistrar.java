package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

/**
 * Provides a way to register system chat commands to Minecraft.
 */
public interface ICommandRegistrar
{
    /**
     * Registers a method that will register to the dispatcher your commands, once called.
     * @param command The method to call, that will register the required commands.
     * @throws ArgumentNullException {@code command} is {@code null}.
     */
    void Register(Action1<CommandDispatcher<CommandSourceStack>> command) throws ArgumentNullException;

    // Class for creating commands.
    // This is used by the default implementation of RegisterByCommand method.
    record RegisterByCommand_InternalLayer<T extends AbstractCommand>(Func1<T> factory)
        implements Action1<CommandDispatcher<CommandSourceStack>>
    {
        @Override
        public void action(CommandDispatcher<CommandSourceStack> obj) {
            factory.function().RegisterToDispatcher(obj);
        }
    }

    /**
     * Registers a chat command to Minecraft.
     * @param command_factory The factory function returning instances of command {@link TC}.
     * @param <TC> The type of command to register.
     * @throws ArgumentNullException {@code command_factory} is {@code null}.
     */
    default <TC extends AbstractCommand> void RegisterByCommand(Func1<TC> command_factory)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(command_factory, "command_factory");
        Register(new RegisterByCommand_InternalLayer<>(command_factory));
    }
}
