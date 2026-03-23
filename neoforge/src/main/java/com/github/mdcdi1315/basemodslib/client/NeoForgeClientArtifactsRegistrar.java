package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.gui.screens.inventory.MenuAccess;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class NeoForgeClientArtifactsRegistrar
    implements IBlockEntityRendererRegistrar,
        IColorHandlersRegistrar,
        IEntityRendererRegistrar,
        IModelDefinitionRegistrar,
        IParticleProviderRegistrar,
        IMenuScreensRegistrar
{
    private SingleLinkedListBasedRegister<MenuScreenRegInfo<? , ?>> menu_screens_info;
    private SingleLinkedListBasedRegister<ModelDefinitionRegistrationInfo> model_infos;
    private SingleLinkedListBasedRegister<ItemColorHandlerRegistrationInfo> item_colors;
    private SingleLinkedListBasedRegister<BlockColorHandlerRegistrationInfo> block_colors;
    private SingleLinkedListBasedRegister<EntityRendererRegistrationInfo<? extends Entity>> entities;
    private SingleLinkedListBasedRegister<BlockEntityRendererRegistrationInfo<? extends BlockEntity>> block_entities;
    private SingleLinkedListBasedRegister<SimpleParticleProviderRegistrationInfo<? extends ParticleOptions>> particles_simple;
    private SingleLinkedListBasedRegister<AdvancedParticleProviderRegistrationInfo<? extends ParticleOptions>> particles_advanced;

    public NeoForgeClientArtifactsRegistrar()
    {
        entities = new SingleLinkedListBasedRegister<>();
        model_infos = new SingleLinkedListBasedRegister<>();
        item_colors = new SingleLinkedListBasedRegister<>();
        block_colors = new SingleLinkedListBasedRegister<>();
        block_entities = new SingleLinkedListBasedRegister<>();
        particles_simple = new SingleLinkedListBasedRegister<>();
        menu_screens_info = new SingleLinkedListBasedRegister<>();
        particles_advanced = new SingleLinkedListBasedRegister<>();
    }

    @Override
    public <T extends BlockEntity> void Register(BlockEntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_entities.Register(info);
    }

    @Override
    public void Register(ItemColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        item_colors.Register(info);
    }

    @Override
    public void Register(BlockColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_colors.Register(info);
    }

    @Override
    public <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        entities.Register(info);
    }

    @Override
    public void Register(ModelDefinitionRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        model_infos.Register(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(SimpleParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        particles_simple.Register(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(AdvancedParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        particles_advanced.Register(info);
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void RegisterMenuScreen(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M, U> constructor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(constructor, "constructor");
        menu_screens_info.Register(new MenuScreenRegInfo<>(type, constructor));
    }

    private static <T extends BlockEntity> void RegisterBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event, BlockEntityRendererRegistrationInfo<T> info) {
        event.registerBlockEntityRenderer(info.type().function() , info.provider());
    }

    private static <T extends Entity> void RegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event, EntityRendererRegistrationInfo<T> info) {
        event.registerEntityRenderer(info.entity_type_provider().function(), info.renderer_provider());
    }

    private static <T extends ParticleOptions> void RegisterSimpleParticleProvider(RegisterParticleProvidersEvent event, SimpleParticleProviderRegistrationInfo<T> info)
    {
        event.registerSpriteSet(info.particle_type().function() , new SimpleParticleRegistration<>(info.particle_provider()));
    }

    private static <T extends ParticleOptions> void RegisterAdvancedParticleProvider(RegisterParticleProvidersEvent event, AdvancedParticleProviderRegistrationInfo<T> info)
    {
        event.registerSpriteSet(info.particle_type().function() , new Func2ToSpriteParticleRegistration<>(info.particle_provider_creater()));
    }

    private static void RegisterMenuScreen(RegisterMenuScreensEvent event, MenuScreenRegInfo<?, ?> info) { info.RegisterByEvent(event); }

    private static void RegisterModelLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event, ModelDefinitionRegistrationInfo info) { event.registerLayerDefinition(info.location(), info.definition()); }

    private static void RegisterItemColorHandler(RegisterColorHandlersEvent.Item event, ItemColorHandlerRegistrationInfo info) { event.register(info.item_color(), info.items().function()); }

    private static void RegisterBlockColorHandler(RegisterColorHandlersEvent.Block event, BlockColorHandlerRegistrationInfo info) { event.register(info.block_color(), info.blocks().function()); }

    public void RegisterToEventBus(IEventBus bus)
    {
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterMenuScreensEvent.class, menu_screens_info, NeoForgeClientArtifactsRegistrar::RegisterMenuScreen);
        menu_screens_info = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, EntityRenderersEvent.RegisterLayerDefinitions.class, model_infos, NeoForgeClientArtifactsRegistrar::RegisterModelLayerDefinition);
        model_infos = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, EntityRenderersEvent.RegisterRenderers.class, entities, NeoForgeClientArtifactsRegistrar::RegisterEntityRenderer);
        entities = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, EntityRenderersEvent.RegisterRenderers.class, block_entities, NeoForgeClientArtifactsRegistrar::RegisterBlockEntityRenderer);
        block_entities = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterColorHandlersEvent.Item.class, item_colors, NeoForgeClientArtifactsRegistrar::RegisterItemColorHandler);
        item_colors = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterColorHandlersEvent.Block.class, block_colors, NeoForgeClientArtifactsRegistrar::RegisterBlockColorHandler);
        block_colors = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterParticleProvidersEvent.class, particles_simple, NeoForgeClientArtifactsRegistrar::RegisterSimpleParticleProvider);
        particles_simple = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterParticleProvidersEvent.class, particles_advanced, NeoForgeClientArtifactsRegistrar::RegisterAdvancedParticleProvider);
        particles_advanced = null;
    }

    private record MenuScreenRegInfo<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M, U> constructor)
    {
        private record MSCToMenuConstructor<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(MenuScreenConstructor<M, U> constructor)
                implements MenuScreens.ScreenConstructor<M, U>
        {
            @Override
            public U create(M abstractContainerMenu, Inventory inventory, Component component) {
                return constructor.Create(abstractContainerMenu, inventory, component);
            }
        }

        public void RegisterByEvent(RegisterMenuScreensEvent event) {
            event.register(type.function(), new MSCToMenuConstructor<>(constructor));
        }
    }

    private record SimpleParticleRegistration<T extends ParticleOptions>(ParticleProvider<T> provider)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) { return provider; }
    }

    private record Func2ToSpriteParticleRegistration<T extends ParticleOptions>(Func2<SpriteSet, ParticleProvider<T>> function)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) { return function.function(spriteSet); }
    }
}
