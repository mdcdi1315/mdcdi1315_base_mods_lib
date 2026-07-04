package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.Contract;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

/**
 * Provides a way to register system chat commands to Minecraft.
 */
@Contract
public interface ICommandRegistrar
{
    /**
     * Registers a method that will register to the dispatcher your commands, once called.
     * @param command The method to call, that will register the required commands.
     * @throws ArgumentNullException {@code command} is {@code null}.
     */
    void Register(Action1<CommandDispatcher<CommandSourceStack>> command) throws ArgumentNullException;

    /**
     * Registers a new argument type to Minecraft.
     * @param info The {@link ArgumentTypeInfo} instance to register.
     * @param name The name of the newly registered argument type information.
     * @param argument_type_class The class of the argument type that resolves the argument type.
     * @param <T> The Brigadier argument type to register.
     * @param <A> The template used to unpack the argument type.
     * @throws ArgumentNullException {@code info} and/or {@code name} are {@code null}.
     * @since 1.0.35
     */
    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void RegisterArgumentTypeInfo(@ConstantExpected String name, Class<A> argument_type_class, ArgumentTypeInfo<A, T> info) throws ArgumentNullException;

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
