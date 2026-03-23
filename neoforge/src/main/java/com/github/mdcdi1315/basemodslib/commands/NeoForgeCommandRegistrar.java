package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class NeoForgeCommandRegistrar
    implements ICommandRegistrar
{
    private final SingleLinkedListBasedRegister<Action1<CommandDispatcher<CommandSourceStack>>> commands;

    public NeoForgeCommandRegistrar()
    {
        commands = new SingleLinkedListBasedRegister<>();
        NeoForgeUtils.AddListener(NeoForge.EVENT_BUS, RegisterCommandsEvent.class , this::RegisterCommands);
    }

    private void RegisterCommands(RegisterCommandsEvent event)
    {
        var dispatcher = event.getDispatcher();
        var commands_en = commands.GetEnumerator();
        try {
            while (commands_en.MoveNext())
            {
                try {
                    commands_en.getCurrent().action(dispatcher);
                } catch (Exception e) {
                    BaseModsLib.LOGGER.error("COMMAND_REGISTRATION: Cannot register a command dispatch listener!\nRegistration for it will be ignored.", e);
                }
            }
        } finally {
            commands_en.Dispose();
        }
    }

    @Override
    public void Register(Action1<CommandDispatcher<CommandSourceStack>> command)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(command, "command");
        commands.Register(command);
    }
}
