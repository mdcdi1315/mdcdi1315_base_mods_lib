package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class NeoForgeCommandRegistrar
    implements ICommandRegistrar
{
    private final SingleLinkedList<Action1<CommandDispatcher<CommandSourceStack>>> commands;

    public NeoForgeCommandRegistrar()
    {
        commands = new SingleLinkedList<>();
        NeoForgeUtils.AddListener(NeoForge.EVENT_BUS, RegisterCommandsEvent.class , this::RegisterCommands);
    }

    private void RegisterCommands(RegisterCommandsEvent event)
    {
        var dispatcher = event.getDispatcher();
        var commands_en = commands.GetEnumerator();
        try {
            while (commands_en.MoveNext()) {
                commands_en.getCurrent().action(dispatcher);
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
        commands.Add(command);
    }
}
