package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.ForgeUtils;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.core.registries.Registries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.DeferredRegister;

public final class ForgeCommandRegistrar
    implements ICommandRegistrar
{
    private DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPE_INFO_REGISTER;
    private SingleLinkedListBasedRegister<Action1<CommandDispatcher<CommandSourceStack>>> commands;

    public ForgeCommandRegistrar(String mod_id)
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
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void RegisterArgumentTypeInfo(@ConstantExpected String name, Class<A> argument_type_class, ArgumentTypeInfo<A, T> info)
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
        ForgeUtils.AddEnumerableListener(MinecraftForge.EVENT_BUS, RegisterCommandsEvent.class, commands, ForgeCommandRegistrar::RunCommandDispatch);
        commands = null;
        ARG_TYPE_INFO_REGISTER = null;
    }
}
