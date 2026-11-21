package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.DotNetLayer.System.ValueTuple3;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.commands.AbstractCommand;
import com.github.mdcdi1315.basemodslib.utils.ChatComponentSupplier;
import com.github.mdcdi1315.basemodslib.mixin.MultiNoiseBiomeSourceMixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;

import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import java.io.*;

public final class GetDimensionBiomeTemperaturesCommand
    extends AbstractCommand
{
    public GetDimensionBiomeTemperaturesCommand() { super("get_dimension_biome_temperatures"); }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder)
    {
        return builder.then(
                Commands.argument("dimension" , DimensionArgument.dimension())
                        .then(
                                Commands.argument("output_file" , StringArgumentType.string())
                                        .executes(GetDimensionBiomeTemperaturesCommand::CommandImpl)
                        )
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static void SaveFileWork(CommandSourceStack stack, ValueTuple3<MultiNoiseBiomeSource, HolderLookup.Provider, File> state)
    {
        var list = ((MultiNoiseBiomeSourceMixin)state.Item1).GetParameters();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (
                FileOutputStream fos = new FileOutputStream(state.Item3);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                JsonWriter writer = new JsonWriter(osw)
        ) {
            try {
                writer.setIndent("\t");
                writer.beginObject();
                writer.name("minecraft_version");
                writer.value(BaseModsLib.GetMinecraftVersion().toString());
                writer.name("climate_parameters");
                DataResult<JsonElement> dr = Climate.ParameterList.codec(Biome.CODEC.fieldOf("biome")).encode(list , RegistryOps.create(JsonOps.INSTANCE, state.Item2) , JsonOps.INSTANCE.empty());
                if (dr.error().isPresent()) {
                    BaseModsLib.LOGGER.error("Cannot encode the biome climate parameter list due to an unexpected error: {}" , dr.error().get().message());
                    stack.sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_dimension_biome_temperatures.list_encoding_failed"));
                    return;
                } else {
                    gson.toJson(dr.result().get() , writer);
                }
            } finally {
                writer.endObject();
            }
        } catch (IOException ioex) {
            BaseModsLib.LOGGER.error("Cannot encode the biome climate parameter list due to an unexpected I/O error." , ioex);
            return;
        }
        stack.sendSuccess(ChatComponentSupplier.FromTranslatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"), true);
    }

    private static int CommandImpl(CommandContext<CommandSourceStack> c)
            throws CommandSyntaxException
    {
        ServerLevel level = DimensionArgument.getDimension(c, "dimension");
        File out_file = new File(StringArgumentType.getString(c , "output_file"));
        if (level.getChunkSource().getGenerator().getBiomeSource() instanceof MultiNoiseBiomeSource mnbs) {
            OffloadTask(c.getSource(), GetDimensionBiomeTemperaturesCommand::SaveFileWork, new ValueTuple3<>(mnbs, level.registryAccess(), out_file));
            return 0;
        } else {
            c.getSource().sendFailure(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_dimension_biome_temperatures.not_multi_noise_source"));
            return -1;
        }
    }
}
