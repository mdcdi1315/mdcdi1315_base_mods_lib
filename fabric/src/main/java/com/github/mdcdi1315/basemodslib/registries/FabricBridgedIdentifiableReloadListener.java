package com.github.mdcdi1315.basemodslib.registries;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("NullableProblems")
public record FabricBridgedIdentifiableReloadListener(ResourceLocation location, PreparableReloadListener wrapped)
    implements IdentifiableResourceReloadListener
{
    @Override
    public String getName() { return location.toString(); }

    @Override
    public ResourceLocation getFabricId() { return location; }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pb, ResourceManager rm, ProfilerFiller pr, ProfilerFiller rp, Executor be, Executor ge) { return wrapped.reload(pb, rm, pr, rp, be, ge); }
}
