package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandBuildContext;

public final class FabricCommandsRegistrar
    implements ICommandRegistrar
{
    private record CommandRegistrationCallbackImplementation(Action1<CommandDispatcher<CommandSourceStack>> command)
        implements CommandRegistrationCallback
    {
        @Override
        public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext build_context, Commands.CommandSelection command_selection) {
            command.action(dispatcher);
        }
    }

    @Override
    public void Register(Action1<CommandDispatcher<CommandSourceStack>> command)
            throws ArgumentNullException
    {
        CommandRegistrationCallback.EVENT.register(new CommandRegistrationCallbackImplementation(command));
    }
}
