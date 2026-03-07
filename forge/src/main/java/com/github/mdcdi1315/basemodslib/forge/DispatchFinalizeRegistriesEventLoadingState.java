package com.github.mdcdi1315.basemodslib.forge;

// NOTE: Keep these imports as less as possible.
// This is executed on the FML mod loader class loader, which it means that we do not typically have access to the mod's internals.

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import net.minecraftforge.fml.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.progress.ProgressMeter;

import java.util.Optional;
import java.util.function.*;
import java.util.concurrent.*;

public final class DispatchFinalizeRegistriesEventLoadingState
        implements IModLoadingState
{
    @Override
    public String previous() { return "LOAD_REGISTRIES"; }

    @Override
    public ModLoadingPhase phase() { return ModLoadingPhase.GATHER; }

    @Override
    public ToIntFunction<ModList> size() { return new SizeSupplier(); }

    @Override
    public String name() { return "BML_DISPATCH_REGISTRY_FINALIZED_EVENTS"; }

    @Override
    public Function<ModList, String> message() { return new MessageSupplier(); }

    @Override
    public Optional<Consumer<ModList>> inlineRunnable() { return Optional.empty(); }

    @Override
    public <T extends Event & IModBusEvent> Optional<CompletableFuture<Void>> buildTransition(Executor syncExecutor, Executor parallelExecutor, ProgressMeter progressBar, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask) {
        if (ModLoader.isLoadingStateValid()) {
            // Nice. Dispatch.
            CompletableFuture<Void> cf = preSyncTask.apply(syncExecutor); // We must get the pre-sync task to actually perform transition
            BaseModsLib.LOGGER.info("Dispatching registry finalization events.");
            cf = cf.thenApplyAsync(new UpdateLabel(progressBar), parallelExecutor);
            // Get the events to dispatch. The events are possibly not initialized yet, and we need them to be loaded in the mod class loader, that's why we call them in from the mod loader layer.
            // Calling them from that class will use the class loader of that class for any dependencies, which is what we want to.
            // cf = cf.thenComposeAsync(new GetResult(ForgeModLoaderLayer.RegistryFinalization_GetSynchronizedTasks(cf, progressBar::increment)), syncExecutor);
            // cf = cf.thenComposeAsync(new GetResult(ForgeModLoaderLayer.RegistryFinalization_GetParallelTasks(cf , progressBar::increment)), parallelExecutor);
            cf = cf.thenComposeAsync(new GetResult(ForgeModLoaderLayer.RegistryFinalization_GetTasks(cf , progressBar::increment)), parallelExecutor);
            // We do not need the below in prod code, I just keep it here to verify that the mod loading state actually dispatches.
            // cf = cf.thenAcceptAsync((v) -> { try { Thread.sleep(2000); } catch (InterruptedException ie) {} }, parallelExecutor);
            cf = cf.thenApply(new OnComplete(syncExecutor , postSyncTask));
            cf = cf.thenApplyAsync(Function.identity(), syncExecutor);
            return Optional.of(cf);
        } else {
            return Optional.empty();
        }
    }

    private record GetResult(CompletableFuture<Void> v)
        implements Function<Void , CompletableFuture<Void>>
    {
        @Override
        public CompletableFuture<Void> apply(Void input) { return v; }
    }

    private record UpdateLabel(ProgressMeter pm)
        implements Function<Void, Void>
    {
        @Override
        public Void apply(Void unused) {
            pm.label("Dispatching registry finalized events to BML Mods");
            return null;
        }
    }

    private record OnComplete(Executor pe, Function<Executor, CompletableFuture<Void>> postSyncTask)
        implements Function<Void, Void>
    {
        @Override
        public Void apply(Void unused) {
            postSyncTask.apply(pe); // We must apply the post sync task to hand out to the next mod loading state.
            BaseModsLib.LOGGER.info("Registry finalization events dispatched successfully.");
            return null;
        }
    }

    private record MessageSupplier()
        implements Function<ModList, String>
    {
        @Override
        public String apply(ModList modList) { return "Dispatching registry finalized events to BML Mods"; }
    }

    private record SizeSupplier()
        implements ToIntFunction<ModList>
    {
        @Override
        public int applyAsInt(ModList value) { return ForgeModLoaderLayer.RegistryFinalization_GetEventCount(); }
    }
}