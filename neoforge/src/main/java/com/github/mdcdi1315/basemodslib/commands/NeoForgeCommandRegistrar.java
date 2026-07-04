package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.core.registries.Registries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeCommandRegistrar
    implements ICommandRegistrar
{
    private DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPE_INFO_REGISTER;
    private SingleLinkedListBasedRegister<Action1<CommandDispatcher<CommandSourceStack>>> commands;

    public NeoForgeCommandRegistrar(String mod_id)
    {
        commands = new SingleLinkedListBasedRegister<>();
        ARG_TYPE_INFO_REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, mod_id);
    }

    private static void RunCommandDispatch(RegisterCommandsEvent rce, Action1<CommandDispatcher<CommandSourceStack>> action)
    {
        try {
            action.action(rce.getDispatcher());
        } catch (Exception e) {
            BaseModsLib.LOGGER.error("COMMAND_REGISTRATION: Cannot register a command dispatch listener!\nRegistration for it will be ignored.", e);
        }
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
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(info, "info");
        ArgumentNullException.ThrowIfNull(argument_type_class, "argument_type_class");

        ARG_TYPE_INFO_REGISTER.register(name, new ArgumentTypeRegistrationFunction<>(argument_type_class, info));
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        ARG_TYPE_INFO_REGISTER.register(bus);
        NeoForgeUtils.AddEnumerableListener(NeoForge.EVENT_BUS, RegisterCommandsEvent.class, commands, NeoForgeCommandRegistrar::RunCommandDispatch);
        commands = null;
        ARG_TYPE_INFO_REGISTER = null;
    }
}
