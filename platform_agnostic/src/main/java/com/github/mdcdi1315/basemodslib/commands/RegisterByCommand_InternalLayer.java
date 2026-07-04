package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Action1;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

// Class for creating commands.
// This is used by the default implementation of RegisterByCommand method.
record RegisterByCommand_InternalLayer<T extends AbstractCommand>(Func1<T> factory)
        implements Action1<CommandDispatcher<CommandSourceStack>>
{
    @Override
    public void action(CommandDispatcher<CommandSourceStack> obj) { factory.function().RegisterToDispatcher(obj); }
}