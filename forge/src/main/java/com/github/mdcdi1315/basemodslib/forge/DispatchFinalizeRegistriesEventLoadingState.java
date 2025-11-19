package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;
import com.github.mdcdi1315.basemodslib.registries.ForgeRegistryWrappedInRegistry;

import net.minecraftforge.fml.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.loading.progress.ProgressMeter;

import java.util.Optional;
import java.util.function.*;
import java.util.concurrent.*;

public final class DispatchFinalizeRegistriesEventLoadingState
        implements IModLoadingState
{
    @Override
    public String name() { return "BML_DISPATCH_REGISTRY_FINALIZED_EVENTS"; }

    @Override
    public String previous() { return "LOAD_REGISTRIES"; }

    @Override
    public ModLoadingPhase phase() { return ModLoadingPhase.GATHER; }

    @Override
    public Function<ModList, String> message() {
        return (ml) -> "Dispatching registry finalized events to BML Mods";
    }

    @Override
    public ToIntFunction<ModList> size() { return (ml) -> 6; } // 6 stages in total

    @Override
    public Optional<Consumer<ModList>> inlineRunnable() {
        return Optional.empty();
    }

    @Override
    public <T extends Event & IModBusEvent> Optional<CompletableFuture<Void>> buildTransition(Executor syncExecutor, Executor parallelExecutor, ProgressMeter progressBar, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask) {
        if (ModLoader.isLoadingStateValid()) {
            // Nice. Dispatch.
            EventManager manager = BaseModsLib.GetEventsManager();

            CompletableFuture<Void> cf = preSyncTask.apply(syncExecutor); // We must get the pre-sync task to actually perform transition
            BaseModsLib.LOGGER.info("Dispatching registry finalization events.");
            cf = cf.thenApplyAsync(new UpdateLabel(progressBar), parallelExecutor);
            cf = cf.thenComposeAsync(new GetResult(GetSyncTasks(cf, progressBar, manager)), syncExecutor);
            cf = cf.thenComposeAsync(new GetResult(GetParallelDispatchableTasks(cf, progressBar, manager)), parallelExecutor);
            // We do not need the below in prod code, I just keep it here to verify that the mod loading state actually dispatches.
            // cf = cf.thenAcceptAsync((v) -> { try { Thread.sleep(2000); } catch (InterruptedException ie) {} }, parallelExecutor);
            cf = cf.thenApply(new OnComplete(syncExecutor , postSyncTask));
            cf = cf.thenApplyAsync(Function.identity(), syncExecutor);
            return Optional.of(cf);
        } else {
            return Optional.empty();
        }
    }

    private static CompletableFuture<Void> GetParallelDispatchableTasks(CompletableFuture<Void> root, ProgressMeter progressBar, EventManager manager) {
        CompletableFuture<Void> cf_actual_2 = root;
        // The below can be dispatched at the same time
        cf_actual_2 = cf_actual_2.thenApplyAsync(new ManagerEventFireDirect(manager, progressBar, (mgr) -> mgr.FireEvent(new EntityTypeRegistryFinalizedEvent(new ForgeRegistryWrappedInRegistry<>(ForgeRegistries.ENTITY_TYPES)))));
        cf_actual_2 = cf_actual_2.thenApplyAsync(new ManagerEventFireDirect(manager, progressBar, (mgr) -> mgr.FireEvent(new MenuTypeRegistryFinalizedEvent(new ForgeRegistryWrappedInRegistry<>(ForgeRegistries.MENU_TYPES)))));
        return cf_actual_2;
    }

    private static CompletableFuture<Void> GetSyncTasks(CompletableFuture<Void> root, ProgressMeter progressBar, EventManager manager) {
        CompletableFuture<Void> cf_actual_1 = root;
        // The below tasks must be executed one after the other
        cf_actual_1 = cf_actual_1.thenAcceptAsync(new ManagerEventFire(manager, progressBar, (mgr) -> mgr.FireEvent(new BlockRegistryFinalizedEvent(new ForgeRegistryWrappedInRegistry<>(ForgeRegistries.BLOCKS)))));
        cf_actual_1 = cf_actual_1.thenAcceptAsync(new ManagerEventFire(manager, progressBar, (mgr) -> mgr.FireEvent(new BlockEntityTypeRegistryFinalizedEvent(new ForgeRegistryWrappedInRegistry<>(ForgeRegistries.BLOCK_ENTITY_TYPES)))));
        cf_actual_1 = cf_actual_1.thenAcceptAsync(new ManagerEventFire(manager, progressBar, (mgr) -> mgr.FireEvent(new ItemRegistryFinalizedEvent(new ForgeRegistryWrappedInRegistry<>(ForgeRegistries.ITEMS)))));
        cf_actual_1 = cf_actual_1.thenAcceptAsync(new ManagerEventFire(manager, progressBar, (mgr) -> mgr.FireEvent(new FluidRegistryFinalizedEvent(new ForgeRegistryWrappedInRegistry<>(ForgeRegistries.FLUIDS)))));
        return cf_actual_1;
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

    // This waits for the current method to complete, thus you chain tasks to be executed one after the other.
    private record ManagerEventFire(EventManager manager, ProgressMeter pm, Consumer<EventManager> event_to_fire)
            implements Consumer<Void>
    {
        @Override
        public void accept(Void unused) {
            event_to_fire.accept(manager);
            pm.increment();
        }
    }

    // This applies the event on the fly, thus the CF will immediately continue with processing the next application.
    private record ManagerEventFireDirect(EventManager manager, ProgressMeter pm , Consumer<EventManager> event_to_fire)
        implements Function<Void , Void>
    {
        @Override
        public Void apply(Void unused) {
            event_to_fire.accept(manager);
            pm.increment();
            return unused;
        }
    }

}