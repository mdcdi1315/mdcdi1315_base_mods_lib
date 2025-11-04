package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

/**
 * Provides the base class for initializing and using a Minecraft command. <br />
 * Non-abstract subclasses of this do represent actual Minecraft commands.
 */
public abstract class AbstractCommand
{
    private final String name;

    /**
     * Constructs a new command instance. <br />
     * It's name will be the one passed to the {@code commandname} parameter.
     * @param commandname The name of the newly created command.
     * @throws ArgumentException {@code commandname} was null or empty.
     */
    protected AbstractCommand(String commandname)
        throws ArgumentException
    {
        ArgumentException.ThrowIfNullOrEmpty(commandname , "commandname");
        name = commandname;
    }

    /**
     * Internal implementation detail. Do not use.
     * @param dispatcher The command dispatcher to use.
     */
    public void RegisterToDispatcher(@DisallowNull CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(CommandImplementation(Commands.literal(name)));
    }

    /**
     * Internal implementation detail. Do not use.
     * @param builder The command builder to use.
     */
    public LiteralArgumentBuilder<CommandSourceStack> RegisterByBuilder(@DisallowNull LiteralArgumentBuilder<CommandSourceStack> builder)
    {
        return builder.then(CommandImplementation(Commands.literal(name)));
    }

    /**
     * Provides the command's implementation.
     * @param builder The command implementation builder to use.
     * @return The transformed builder, passing through all the commands
     */
    @NotNull
    protected abstract LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(@DisallowNull LiteralArgumentBuilder<CommandSourceStack> builder);

    /**
     * Gets the name of this command.
     * @return The command's name.
     */
    @NotNull
    public String getName() {
        return name;
    }
}
