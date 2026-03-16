package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.commands.AbstractCommand;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

public final class BMLInfoCommand
    extends AbstractCommand
{
    public BMLInfoCommand() { super("info"); }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder) { return builder.executes(BMLInfoCommand::Implementation); }

    private static int Implementation(CommandContext<CommandSourceStack> context)
    {
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.header"));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.mc_version", BaseModsLib.GetMinecraftVersion().toString()));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.mod_loader", BaseModsLib.GetModLoaderBranding(), BaseModsLib.GetModLoaderVersion().toString()));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.loaded_instances", BaseModsLib.GetModInstancesCount()));
        context.getSource().sendSystemMessage(Component.translatable(BaseModsLib.IsDevelopmentEnvironment() ? "mdcdi1315_base_mods_lib.devcmds.get_bml_info.is_dev_env_yes" : "mdcdi1315_base_mods_lib.devcmds.get_bml_info.is_dev_env_no"));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"));
        return 0;
    }
}
