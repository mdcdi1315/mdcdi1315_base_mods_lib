package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.world.NBTUtils;
import com.github.mdcdi1315.basemodslib.commands.AbstractCommand;
import com.github.mdcdi1315.basemodslib.utils.ChatComponentSupplier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.data.structures.StructureUpdater;

import java.io.File;

public final class UpdateStructureTemplateCommand
    extends AbstractCommand
{
    public UpdateStructureTemplateCommand() {
        super("update_structure_template");
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder)
    {
        return builder.then(
                Commands.argument("source_file", StringArgumentType.string())
                        .then(
                                Commands.argument("dest_file", StringArgumentType.string())
                                        .executes(UpdateStructureTemplateCommand::CommandImplementation)
                        )
        );
    }

    private static int CommandImplementation(CommandContext<CommandSourceStack> c)
            throws CommandSyntaxException
    {
        File source = new File(StringArgumentType.getString(c, "source_file"));
        if (!source.exists()) {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_template.source_not_exist" , source.toString()));
            return -1;
        } else {
            File destination = new File(StringArgumentType.getString(c, "dest_file"));
            if (destination.exists()) {
                c.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_template.warn_dest_file_exists"));
            }
            CompoundTag ctg;
            try {
                ctg = NBTUtils.LoadNBTFile(source);
                NBTUtils.SaveNBTFileAsGZip(destination , StructureUpdater.update(source.toString() , ctg));
            } catch (Exception e) {
                c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_template.update_failure"));
                BaseModsLib.LOGGER.error("Cannot update the specified structure file due to an exception." , e);
                return -2;
            }
            c.getSource().sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"), false);
            return 0;
        }
    }
}
