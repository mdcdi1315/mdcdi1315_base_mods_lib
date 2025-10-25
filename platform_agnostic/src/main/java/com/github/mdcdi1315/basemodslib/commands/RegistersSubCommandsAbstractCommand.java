package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;

import net.minecraft.commands.CommandSourceStack;

public abstract class RegistersSubCommandsAbstractCommand
    extends AbstractCommand
{
    private CommandPermission[] permissions;
    private AbstractCommand[] commands;

    protected RegistersSubCommandsAbstractCommand(String commandname , AbstractCommand... cmds)
            throws ArgumentException {
        super(commandname);
        commands = cmds;
        permissions = null;
    }

    protected RegistersSubCommandsAbstractCommand(String commandname , CommandPermission permission , AbstractCommand... cmds)
            throws ArgumentException
    {
        super(commandname);
        ArgumentNullException.ThrowIfNull(permission , "permission");
        commands = cmds;
        this.permissions = new CommandPermission[] { permission };
    }

    protected RegistersSubCommandsAbstractCommand(String commandname , CommandPermission[] permissions , AbstractCommand... cmds)
            throws ArgumentException {
        super(commandname);
        ArgumentNullException.ThrowIfNull(permissions , "permissions");
        commands = cmds;
        this.permissions = permissions;
    }

    @Override
    protected final LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder) {
        LiteralArgumentBuilder<CommandSourceStack> b = builder;
        if (permissions != null)
        {
            for (var c : permissions) {
                b = b.requires(c);
            }
            permissions = null;
        }
        for (var i : commands)
        {
            b = i.RegisterByBuilder(b); // Recursively apply the commands, if needed
        }
        commands = null; // Aggressively clean the command array to sweep up any unused mem
        return b;
    }
}
