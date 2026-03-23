package com.github.mdcdi1315.basemodslib.registries;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

public record FabricBridgedIdentifiableReloadListener(ResourceLocation location, PreparableReloadListener wrapped)
    implements IdentifiableResourceReloadListener
{
    @Override
    public ResourceLocation getFabricId() { return location; }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {
        return wrapped.reload(barrier, manager, backgroundExecutor, gameExecutor);
    }

    @Override
    public String getName() { return location.toString(); }
}
