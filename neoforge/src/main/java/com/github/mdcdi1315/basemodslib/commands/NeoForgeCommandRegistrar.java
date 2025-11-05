package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class NeoForgeCommandRegistrar
    implements ICommandRegistrar
{
    private final List<Action1<CommandDispatcher<CommandSourceStack>>> commands;

    public NeoForgeCommandRegistrar()
    {
        commands = new List<>(4);
        NeoForge.EVENT_BUS.addListener(this::RegisterCommands);
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
