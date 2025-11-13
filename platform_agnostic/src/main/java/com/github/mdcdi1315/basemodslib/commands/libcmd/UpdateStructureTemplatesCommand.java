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

public final class UpdateStructureTemplatesCommand
    extends AbstractCommand
{
    public UpdateStructureTemplatesCommand() {
        super("update_structure_templates");
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder)
    {
        return builder.then(
                Commands.argument("source_dir", StringArgumentType.string())
                        .then(
                                Commands.argument("dest_dir", StringArgumentType.string())
                                        .executes(UpdateStructureTemplatesCommand::CommandImplementation)
                        )
        );
    }

    private static boolean NBTFileFilter(File dir, String name) {
        return name.endsWith(".nbt");
    }

    private static int CommandImplementation(CommandContext<CommandSourceStack> c)
            throws CommandSyntaxException
    {
        File source = new File(StringArgumentType.getString(c, "source_dir"));
        if (!source.isDirectory()) {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.source_not_exist_or_dir" , source.toString()));
            return -1;
        }
        File destination = new File(StringArgumentType.getString(c, "dest_dir"));
        if (!destination.isDirectory()) {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.dest_not_exist_or_dir", destination.toString()));
            return -1;
        }
        File[] files = source.listFiles(UpdateStructureTemplatesCommand::NBTFileFilter);
        if (files == null) {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.i_o_error_gathering_files"));
            return -2;
        }
        int failed = 0;
        CompoundTag ctg;
        for (File f : files)
        {
            // For each file, read it as NBT structure template and then save it as the updated variant of it.
            try {
                ctg = NBTUtils.LoadNBTFile(f);
                NBTUtils.SaveNBTFileAsGZip(new File(destination , f.getName()) , StructureUpdater.update(f.toString() , ctg));
            } catch (Exception e) {
                failed++;
                BaseModsLib.LOGGER.error("Cannot update the structure file {} due to an exception:\n{}", f.getName() , e);
            }
        }
        if (failed == files.length) {
            c.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.all_files_failed"));
            return -3;
        } else if (failed > 0) {
            c.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.not_all_files_updated", failed , files.length));
        }
        c.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.completed_inform_about_files" , files.length - failed, destination.toString()));
        c.getSource().sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"), true);
        return 0;
    }
}
