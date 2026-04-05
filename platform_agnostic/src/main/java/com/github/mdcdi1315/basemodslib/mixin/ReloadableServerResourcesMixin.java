package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerResourcesReloadedEvent;

import net.minecraft.commands.Commands;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

@Mixin(value = ReloadableServerResources.class, priority = 100000)
public class ReloadableServerResourcesMixin
{
    @Inject(method = "loadResources", at = @At("RETURN"))
    private static void loadResources(
            ResourceManager resourceManager,
            LayeredRegistryAccess<RegistryLayer> registries,
            FeatureFlagSet enabledFeatures,
            Commands.CommandSelection commandSelection,
            int functionCompilationLevel,
            Executor backgroundExecutor,
            Executor gameExecutor,
            CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> callback_info
    ) {
        callback_info
                .getReturnValue()
                .handleAsync(ReloadableServerResourcesMixin::MDCDI1315$BML$OnResourcesReloadedEvent);
    }

    @Unique
    private static ReloadableServerResources MDCDI1315$BML$OnResourcesReloadedEvent(ReloadableServerResources rsr, Throwable th)
    {
        if (th == null) {
            // If the throwable is null, all the constructed reload methods have been successfully completed.
            BaseModsLib.GetEventsManager().FireEvent(new ServerResourcesReloadedEvent(rsr));
        }
        // Return the object back.
        return rsr;
    }
}
