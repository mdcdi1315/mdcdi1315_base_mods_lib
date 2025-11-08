package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

/**
 * Provides a way for registering sub-commands through an {@link AbstractCommand} class implementation.
 */
public abstract class RegistersSubCommandsAbstractCommand
    extends AbstractCommand
{
    private CommandPermission[] permissions;
    private AbstractCommand[] commands;

    /**
     * Creates a new instance of the {@link RegistersSubCommandsAbstractCommand} class by specifying the name of the command to register, as well as the other sub-commands to register as well.
     * @param command_name The name of this command.
     * @param commands The sub-commands to register.
     * @throws ArgumentException {@code command_name} is {@code null} or the empty string ("").
     * @throws ArgumentNullException {@code commands} is {@code null}.
     */
    protected RegistersSubCommandsAbstractCommand(String command_name , AbstractCommand... commands)
            throws ArgumentException
    {
        super(command_name);
        ArgumentNullException.ThrowIfNull(commands, "commands");
        permissions = null;
        this.commands = commands;
    }

    /**
     * Creates a new instance of the {@link RegistersSubCommandsAbstractCommand} class by specifying the name of the command to register, as well as the other sub-commands to register as well. <br />
     * This constructor does also provide the permission required to use this command and all the sub-commands.
     * @param command_name The name of this command.
     * @param permission The {@link CommandPermission} required, so that this command and all the sub-commands can be used.
     * @param commands The sub-commands to register.
     * @throws ArgumentException {@code command_name} is {@code null} or the empty string ("").
     * @throws ArgumentNullException {@code commands} and/or {@code permission} are {@code null}.
     */
    protected RegistersSubCommandsAbstractCommand(String command_name , CommandPermission permission , AbstractCommand... commands)
            throws ArgumentException
    {
        super(command_name);
        ArgumentNullException.ThrowIfNull(commands, "commands");
        ArgumentNullException.ThrowIfNull(permission , "permission");
        this.commands = commands;
        this.permissions = new CommandPermission[] { permission };
    }

    /**
     * Creates a new instance of the {@link RegistersSubCommandsAbstractCommand} class by specifying the name of the command to register, as well as the other sub-commands to register as well. <br />
     * This constructor does also provide the permission(s) required to use this command and all the sub-commands.
     * @param command_name The name of this command.
     * @param permissions The {@link CommandPermission}s required, so that this command and all the sub-commands can be used.
     * @param commands The sub-commands to register.
     * @throws ArgumentException {@code command_name} is {@code null} or the empty string ("").
     * @throws ArgumentNullException {@code commands} and/or {@code permission} are {@code null}.
     */
    protected RegistersSubCommandsAbstractCommand(String command_name , CommandPermission[] permissions , AbstractCommand... commands)
            throws ArgumentException
    {
        super(command_name);
        ArgumentNullException.ThrowIfNull(commands, "commands");
        ArgumentNullException.ThrowIfNull(permissions , "permissions");
        this.commands = commands;
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
