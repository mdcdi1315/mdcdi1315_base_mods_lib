package com.github.mdcdi1315.basemodslib.registries;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

public record FabricBridgedIdentifiableReloadListener(ResourceLocation location, PreparableReloadListener wrapped)
    implements IdentifiableResourceReloadListener
{
    @Override
    public ResourceLocation getFabricId() { return location; }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return wrapped.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }

    @Override
    public String getName() { return location.toString(); }
}
