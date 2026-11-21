package com.github.mdcdi1315.basemodslib.commands.libcmd;

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

    private static final class UpdateStructureTaskState
    {
        private boolean completed, failed;
        public final File source_file, dest_file;

        public UpdateStructureTaskState(File source_file, File dest_file)
        {
            this.failed = false;
            this.completed = false;
            this.dest_file = dest_file;
            this.source_file = source_file;
        }

        public void SetFailed() { failed = true; }

        public void SetCompleted() { completed = true; }

        public boolean IsFailed() { return failed; }

        public boolean IsComplete() { return completed; }

        public boolean HasExecuted() { return completed | failed; }
    }

    private record UpdateStructuresReport(File destination, UpdateStructureTaskState[] tasks) { }

    private static boolean NBTFileFilter(File dir, String name) { return name.endsWith(".nbt"); }

    private static void UpdateOffloadedTask(CommandSourceStack stack, UpdateStructureTaskState v)
    {
        CompoundTag ctg;
        try {
            stack.sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.updating_file_task", v.source_file.getName()));
            ctg = NBTUtils.LoadNBTFile(v.source_file);
            NBTUtils.SaveNBTFileAsGZip(v.dest_file, StructureUpdater.update(v.source_file.toString(), ctg));
            v.SetCompleted();
        } catch (Exception e) {
            v.SetFailed();
            BaseModsLib.LOGGER.error("Cannot update the structure file {} due to an exception:\n{}", v.source_file.getName() , e);
        }
    }

    private static void AwaitStructuresReport(CommandSourceStack stack, UpdateStructuresReport r)
    {
        int failed = 0;
        int succeeded = 0;
        UpdateStructureTaskState state;
        for (int I = 0; I < r.tasks.length; I++)
        {
            state = r.tasks[I];
            while (!state.HasExecuted())
            {
                try { Thread.sleep(200); } catch (InterruptedException _) { }
            }
            if (state.IsFailed()) { failed++; }
            if (state.IsComplete()) { succeeded++; }
        }
        if (failed == r.tasks.length) {
            stack.sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.all_files_failed").withStyle(ChatFormatting.RED));
            return;
        } else if (failed > 0) {
            stack.sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.not_all_files_updated", failed , r.tasks.length).withStyle(ChatFormatting.YELLOW));
        }
        stack.sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.update_structure_templates.completed_inform_about_files" , succeeded, r.destination.toString()).withStyle(ChatFormatting.GREEN));
        SendCommandSuccessMessage(stack);
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
        UpdateStructureTaskState[] tasks = new UpdateStructureTaskState[files.length];
        for (int I = 0; I < tasks.length; I++)
        {
            OffloadTask(
                    c.getSource(),
                    UpdateStructureTemplatesCommand::UpdateOffloadedTask,
                    tasks[I] = new UpdateStructureTaskState(files[I], new File(destination, files[I].getName()))
            );
        }
        OffloadTask(c.getSource(), UpdateStructureTemplatesCommand::AwaitStructuresReport, new UpdateStructuresReport(destination, tasks));
        return 0;
    }
}
