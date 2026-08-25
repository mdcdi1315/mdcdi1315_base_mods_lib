package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.DotNetLayer.System.AppContext;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.RuntimeFeature;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.VersionInfo;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.commands.AbstractCommand;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import java.util.Optional;

public final class BMLInfoCommand
    extends AbstractCommand
{
    public BMLInfoCommand() { super("info"); }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(LiteralArgumentBuilder<CommandSourceStack> builder) { return builder.executes(BMLInfoCommand::Implementation); }

    @SuppressWarnings("OptionalIsPresent")
    private static int Implementation(CommandContext<CommandSourceStack> context)
    {
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.header"));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.bml_version", VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_VERSION)));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.bml_build_time", VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_BUILD_TIME)));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.mc_version", BaseModsLib.GetMinecraftVersion().toString()));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.mod_loader", BaseModsLib.GetModLoaderBranding(), BaseModsLib.GetModLoaderVersion().toString()));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.loaded_instances", BaseModsLib.GetModInstanceCollection().getCount()));
        context.getSource().sendSystemMessage(Component.translatable(BaseModsLib.IsDevelopmentEnvironment() ? "mdcdi1315_base_mods_lib.devcmds.get_bml_info.is_dev_env_yes" : "mdcdi1315_base_mods_lib.devcmds.get_bml_info.is_dev_env_no"));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.dotnetlayerinfo.banner"));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.dotnetlayerinfo.targetframeworkname", AppContext.GetTargetFrameworkName()));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.dotnetlayerinfo.app_context_directory", AppContext.GetBaseDirectory()));
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.dotnetlayerinfo.runtimefeatures.banner"));
        for (var c : new String[] {
                RuntimeFeature.PortablePdb,
                RuntimeFeature.DefaultImplementationsOfInterfaces,
                RuntimeFeature.UnmanagedSignatureCallingConvention,
                RuntimeFeature.CovariantReturnsOfClasses,
                RuntimeFeature.ByRefFields,
                RuntimeFeature.ByRefLikeGenerics,
                RuntimeFeature.VirtualStaticsInInterfaces,
                RuntimeFeature.NumericIntPtr
        })
        {
            context.getSource().sendSystemMessage(
                    Component.translatable(
                            "mdcdi1315_base_mods_lib.devcmds.get_bml_info.dotnetlayerinfo.runtimefeatures.feature",
                            c,
                            RuntimeFeature.IsSupported(c) ?
                                    Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.dotnetlayerinfo.runtimefeatures.feature.supported") :
                                    Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.dotnetlayerinfo.runtimefeatures.feature.unsupported")
                    )
            );
        }
        Runtime.Version v = Runtime.version();
        if (BaseModsLib.GetEnvironment() == ModdingEnvironment.CLIENT)
        {
            Optional<String> os;
            context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.java_runtime_header"));
            context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.java_runtime_version", v.feature(), v.interim(), v.update(), v.patch()));
            os = v.pre();
            if (os.isPresent()) {
                context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.java_runtime_prerelease_info", os.get()));
            }
            os = v.optional();
            if (os.isPresent()) {
                context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.devcmds.get_bml_info.java_runtime_additional_info", os.get()));
            }
        }
        context.getSource().sendSystemMessage(Component.translatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully"));
        return 0;
    }
}
