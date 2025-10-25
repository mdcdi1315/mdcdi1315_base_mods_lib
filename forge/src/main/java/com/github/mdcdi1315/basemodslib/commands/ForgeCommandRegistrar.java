package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class ForgeCommandRegistrar
    implements ICommandRegistrar
{
    private final List<Action1<CommandDispatcher<CommandSourceStack>>> commands;

    public ForgeCommandRegistrar() {
        commands = new List<>(4);
        MinecraftForge.EVENT_BUS.addListener(this::RunCommandsRegistration);
    }

    private void RunCommandsRegistration(RegisterCommandsEvent rce)
    {
        CommandDispatcher<CommandSourceStack> dispatch = rce.getDispatcher();
        IEnumerator<Action1<CommandDispatcher<CommandSourceStack>>> en = commands.GetEnumerator();
        try {
            while (en.MoveNext())
            {
                try {
                    en.getCurrent().action(dispatch);
                } catch (Exception e) {
                    BaseModsLib.LOGGER.error("COMMAND_REGISTRATION: Cannot register a command dispatch listener!\nRegistration for it will be ignored.", e);
                }
            }
        } finally {
            en.Dispose();
        }
    }

    @Override
    public void Register(Action1<CommandDispatcher<CommandSourceStack>> command)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(command);
        commands.Add(command);
    }
}
