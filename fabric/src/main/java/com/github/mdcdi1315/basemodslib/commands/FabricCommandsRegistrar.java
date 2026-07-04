package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

public final class FabricCommandsRegistrar
    implements ICommandRegistrar
{
    private String mod_id;
    private SingleLinkedListBasedRegister<Action1<CommandDispatcher<CommandSourceStack>>> commands;

    public FabricCommandsRegistrar(String mod_id)
    {
        this.mod_id = mod_id;
        commands = new SingleLinkedListBasedRegister<>();
    }

    @Override
    public void Register(Action1<CommandDispatcher<CommandSourceStack>> command)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(command, "command");
        commands.Register(command);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void RegisterArgumentTypeInfo(String name, Class<A> argument_type_class, ArgumentTypeInfo<A, T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ArgumentNullException.ThrowIfNull(argument_type_class, "argument_type_class");

        ArgumentTypeRegistry.registerArgumentType(
                RegistryUtils.ConstructResourceLocation(mod_id, name),
                argument_type_class,
                info
        );
    }

    public void RegistrationFinalized()
    {
        mod_id = null;
        if (commands.HasItems())
        {
            CommandRegistrationCallback.EVENT.register(
                    new CommandRegistrationImpl(commands)
            );
        }
        commands = null;
    }
}
