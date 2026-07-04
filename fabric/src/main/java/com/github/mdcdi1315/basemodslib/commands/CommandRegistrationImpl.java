package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandBuildContext;

record CommandRegistrationImpl(SingleLinkedListBasedRegister<Action1<CommandDispatcher<CommandSourceStack>>> commands)
        implements CommandRegistrationCallback
{
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment)
    {
        try (IEnumerator<Action1<CommandDispatcher<CommandSourceStack>>> cmd_enumerator = commands.GetEnumerator())
        {
            while (cmd_enumerator.MoveNext())
            {
                try {
                    cmd_enumerator.getCurrent().action(dispatcher);
                } catch (Exception ex) {
                    BaseModsLib.LOGGER.warn("[FabricCommandRegistrationImpl] Failed to execute a command action.", ex);
                }
            }
        }
    }
}
