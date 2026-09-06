package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.commands.AbstractCommand;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

public final class DisplayLoadedBMLModsCommand
    extends AbstractCommand
{
    public DisplayLoadedBMLModsCommand() { super("loaded_bml_mods"); }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder) { return builder.executes(DisplayLoadedBMLModsCommand::DisplayImpl); }

    private static int DisplayImpl(CommandContext<CommandSourceStack> context)
    {
        var mod_instances = BaseModsLib.GetModInstanceCollection();

        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_loaded_mods.mods_banner", mod_instances.getCount()));

        try (var e = mod_instances.GetEnumerator())
        {
            IServerModInstance smd;
            while (e.MoveNext()) {
                smd = e.getCurrent();
                context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_loaded_mods.mod_info_msg", smd.GetModId(), smd.hashCode()));
            }
        }

        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"));
        return 0;
    }
}
