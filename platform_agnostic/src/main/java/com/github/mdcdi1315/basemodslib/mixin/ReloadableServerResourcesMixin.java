package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerResourcesReloadedEvent;

import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Inject(method = "loadResources", at = @At("RETURN"))
    private static void loadResources(ResourceManager resourceManager, RegistryAccess.Frozen registryAccess, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> callback_info)
    {
        callback_info.getReturnValue().thenAccept(server -> BaseModsLib.GetEventsManager().FireEvent(new ServerResourcesReloadedEvent(server)));
    }

}
