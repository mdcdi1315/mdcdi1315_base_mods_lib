package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.DotNetLayer.System.ValueTuple2;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.world.NBTUtils;
import com.github.mdcdi1315.basemodslib.commands.AbstractCommand;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
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

    private static void StructureUpdateOffloadedTask(CommandSourceStack stack, ValueTuple2<File, File> state)
    {
        CompoundTag ctg;

        try {
            ctg = NBTUtils.LoadNBTFile(state.Item1);
            NBTUtils.SaveNBTFileAsGZip(state.Item2, StructureUpdater.update(state.Item1.toString() , ctg));
        } catch (Exception e) {
            stack.sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_template.update_failure"));
            BaseModsLib.LOGGER.error("Cannot update the specified structure file due to an exception.", e);
            return;
        }

        SendCommandSuccessMessage(stack);
    }

    private static int CommandImplementation(CommandContext<CommandSourceStack> c)
            throws CommandSyntaxException
    {
        File source = new File(StringArgumentType.getString(c, "source_file"));
        if (source.exists()) {
            File destination = new File(StringArgumentType.getString(c, "dest_file"));
            if (destination.exists()) {
                c.getSource().sendSystemMessage(
                        Component
                                .translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_template.warn_dest_file_exists")
                                .withStyle(ChatFormatting.YELLOW)
                );
            }
            OffloadTask(c.getSource(), UpdateStructureTemplateCommand::StructureUpdateOffloadedTask, new ValueTuple2<>(source, destination));
            return 0;
        } else {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_template.source_not_exist" , source.toString()));
            return -1;
        }
    }
}
