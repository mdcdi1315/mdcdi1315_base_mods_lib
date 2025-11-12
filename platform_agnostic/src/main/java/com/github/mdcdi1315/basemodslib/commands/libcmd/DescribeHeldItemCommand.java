package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.world.NBTUtils;
import com.github.mdcdi1315.basemodslib.commands.AbstractCommand;
import com.github.mdcdi1315.basemodslib.utils.ChatComponentSupplier;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;

import java.io.*;

public final class DescribeHeldItemCommand
    extends AbstractCommand
{
    public DescribeHeldItemCommand() { super("describe_held_item"); }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder)
    {
        return builder.then(
                Commands.argument("file_format", StringArgumentType.string())
                        .then(
                                Commands.argument("file_path" , StringArgumentType.string())
                                        .executes(DescribeHeldItemCommand::DescribeItemGeneric)
                        )
        ).executes(DescribeHeldItemCommand::DescribeItemPrintJsonInChat);
    }

    private static String EncodeToJsonString(JsonElement element)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setLenient(true);
        jsonWriter.setIndent("    ");
        new GsonBuilder().setPrettyPrinting().create().toJson(element , jsonWriter);
        return stringWriter.toString();
    }

    private static DescribeHeldItemFileFormat GetFormatArgOrFail(CommandContext<?> cxt, String arg_name)
            throws CommandSyntaxException
    {
        String parsed = StringArgumentType.getString(cxt , arg_name);
        DescribeHeldItemFileFormat fft;
        try {
            fft = DescribeHeldItemFileFormat.valueOf(parsed);
        } catch (IllegalArgumentException iae) {
            throw new SimpleCommandExceptionType(Component.translatable("mdcdi1315_base_mods_lib.devcmds.describe_held_item_file_format_arg.invalid_value" , parsed)).create();
        }
        return fft;
    }

    private static int DescribeItemGeneric(CommandContext<CommandSourceStack> c)
            throws CommandSyntaxException
    {
        ServerPlayer player = c.getSource().getPlayer();
        if (player == null) {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.cmds.requires_player_context"));
            return -1;
        }
        DescribeHeldItemFileFormat fft = GetFormatArgOrFail(c , "file_format");
        File fp = new File(StringArgumentType.getString(c , "file_path"));
        int result = SaveToFile(fft , player , fp);
        if (result == -2) {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.describe_held_item.cannot_encode_stack"));
            return -2;
        } else if (result == 0) {
            c.getSource().sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"), false);
            return 0;
        } else if (result == 1) {
            c.getSource().sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.devcmds.describe_held_item.item_stack_empty"), false);
            return 0;
        } else {
            return -1;
        }
    }

    private static int SaveToFile(DescribeHeldItemFileFormat file_format, ServerPlayer player, File out_file)
    {
        ItemStack is = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (is.isEmpty()) { return 1; }
        return switch (file_format) {
            case JSON -> SaveAsJson(is , out_file);
            default -> SaveAsNbt(is , out_file , file_format == DescribeHeldItemFileFormat.NBT_COMPRESSED);
        };
    }

    private static int SaveAsNbt(ItemStack is , File out_file, boolean compressed)
    {
        DataResult<Tag> element = ItemStack.CODEC.encode(is , NbtOps.INSTANCE , NbtOps.INSTANCE.empty());
        if (element.error().isPresent()) {
            BaseModsLib.LOGGER.error("Cannot encode the specified item stack.\nError details: {}" , element.error().get().message());
            return -2;
        }
        try {
            if (compressed) {
                NBTUtils.SaveNBTFileAsGZip(out_file , (CompoundTag) element.result().get());
            } else {
                NBTUtils.SaveNBTFile(out_file ,  (CompoundTag) element.result().get());
            }
            return 0;
        } catch (IOException ioex) {
            BaseModsLib.LOGGER.error("Cannot encode the specified item stack" , ioex);
            return -2;
        } catch (ClassCastException cce) {
            BaseModsLib.LOGGER.error("Item stack was not of type CompoundTag!!!!");
            return -2;
        }
    }

    private static int SaveAsJson(ItemStack is , File out_file)
    {
        DataResult<JsonElement> element = ItemStack.CODEC.encode(is , JsonOps.INSTANCE , JsonOps.INSTANCE.empty());
        if (element.error().isPresent()) {
            BaseModsLib.LOGGER.error("Cannot encode the specified item stack.\nError details: {}" , element.error().get().message());
            return -2;
        }
        try (
                FileOutputStream fos = new FileOutputStream(out_file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                JsonWriter writer = new JsonWriter(osw)
        ) {
            writer.setIndent("\t");
            writer.setSerializeNulls(false);
            new GsonBuilder().setPrettyPrinting().setLenient().create().toJson(element.result().get() , writer);
        } catch (IOException ioex) {
            BaseModsLib.LOGGER.error("Cannot encode the specified item stack" , ioex);
            return -2;
        }
        return 0;
    }

    private static int DescribeItemPrintJsonInChat(CommandContext<CommandSourceStack> c)
            throws CommandSyntaxException
    {
        ServerPlayer player = c.getSource().getPlayer();
        if (player == null) {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.cmds.requires_player_context"));
            return -1;
        } else {
            ItemStack is = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (is.isEmpty()) {
                c.getSource().sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.devcmds.describe_held_item.item_stack_empty"), false);
                return 0;
            }
            DataResult<JsonElement> element = ItemStack.CODEC.encode(is , JsonOps.INSTANCE , JsonOps.INSTANCE.empty());
            if (element.error().isPresent()) {
                c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.describe_held_item.cannot_encode_stack"));
                BaseModsLib.LOGGER.error("Cannot encode the specified item stack.\nError details: {}" , element.error().get().message());
                return -2;
            }
            try {
                c.getSource().sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.devcmds.describe_held_item.encoded_stack_message_json", EncodeToJsonString(element.result().get())), false);
            } catch (Exception e) {
                c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.describe_held_item.cannot_encode_stack"));
                BaseModsLib.LOGGER.error("Cannot encode the specified item stack.", e);
                return -2;
            }
            c.getSource().sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"), false);
            return 0;
        }
    }
}
