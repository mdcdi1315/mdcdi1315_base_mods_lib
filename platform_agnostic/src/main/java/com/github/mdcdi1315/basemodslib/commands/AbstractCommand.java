package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public abstract class AbstractCommand
{
    private final String name;

    protected AbstractCommand(String commandname)
        throws ArgumentException
    {
        ArgumentException.ThrowIfNullOrEmpty(commandname , "commandname");
        name = commandname;
    }

    public void RegisterToDispatcher(@DisallowNull CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(CommandImplementation(Commands.literal(name)));
    }

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
