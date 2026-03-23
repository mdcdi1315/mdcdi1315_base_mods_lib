package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.ForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.gui.screens.inventory.MenuAccess;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

public final class ForgeClientArtifactsRegistrar
    implements IBlockEntityRendererRegistrar,
        IEntityRendererRegistrar,
        IColorHandlersRegistrar,
        IModelDefinitionRegistrar,
        IParticleProviderRegistrar,
        IMenuScreensRegistrar
{
    private SingleLinkedListBasedRegister<MenuScreenRegInfo<? , ?>> menu_screens_info;
    private SingleLinkedListBasedRegister<ModelDefinitionRegistrationInfo> model_defs_infos;
    private SingleLinkedListBasedRegister<EntityRendererRegistrationInfo<?>> entity_renderer_infos;
    private SingleLinkedListBasedRegister<ItemColorHandlerRegistrationInfo> item_color_handler_infos;
    private SingleLinkedListBasedRegister<BlockColorHandlerRegistrationInfo> block_color_handler_infos;
    private SingleLinkedListBasedRegister<SimpleParticleProviderRegistrationInfo<?>> simple_particle_reg;
    private SingleLinkedListBasedRegister<AdvancedParticleProviderRegistrationInfo<?>> advanced_particle_reg;
    private SingleLinkedListBasedRegister<BlockEntityRendererRegistrationInfo<?>> block_entity_renderer_infos;

    public ForgeClientArtifactsRegistrar() {
        model_defs_infos = new SingleLinkedListBasedRegister<>();
        menu_screens_info = new SingleLinkedListBasedRegister<>();
        simple_particle_reg = new SingleLinkedListBasedRegister<>();
        advanced_particle_reg = new SingleLinkedListBasedRegister<>();
        entity_renderer_infos = new SingleLinkedListBasedRegister<>();
        item_color_handler_infos = new SingleLinkedListBasedRegister<>();
        block_color_handler_infos = new SingleLinkedListBasedRegister<>();
        block_entity_renderer_infos = new SingleLinkedListBasedRegister<>();
    }

    @Override
    public <T extends BlockEntity> void Register(BlockEntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_entity_renderer_infos.Register(info);
    }

    @Override
    public <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        entity_renderer_infos.Register(info);
    }

    @Override
    public void Register(ItemColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        item_color_handler_infos.Register(info);
    }

    @Override
    public void Register(BlockColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_color_handler_infos.Register(info);
    }

    @Override
    public void Register(ModelDefinitionRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info,"info");
        model_defs_infos.Register(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(SimpleParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        simple_particle_reg.Register(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(AdvancedParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        advanced_particle_reg.Register(info);
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void RegisterMenuScreen(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M, U> constructor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(constructor, "constructor");
        menu_screens_info.Register(new MenuScreenRegInfo<>(type, constructor));
    }

    // Improved how the collections and their lifetime is handled - see ForgeUtils.AddEnumerableListener method.

    private static <T extends Entity> void RegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event, EntityRendererRegistrationInfo<T> info)
    {
        event.registerEntityRenderer(info.entity_type_provider().function(), info.renderer_provider());
    }

    private static <T extends BlockEntity> void RegisterBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event, BlockEntityRendererRegistrationInfo<T> info)
    {
        event.registerBlockEntityRenderer(info.type().function(), info.provider());
    }

    private static void RegisterModelLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event, ModelDefinitionRegistrationInfo info)
    {
        event.registerLayerDefinition(info.location(), info.definition());
    }

    private static <T extends ParticleOptions> void RegisterParticleProvider_Simple(RegisterParticleProvidersEvent event, SimpleParticleProviderRegistrationInfo<T> info)
    {
        event.registerSpriteSet(info.particle_type().function(), new SpriteParticleImplementation_Simple<>(info.particle_provider()));
    }

    private static <T extends ParticleOptions> void RegisterParticleProvider_Advanced(RegisterParticleProvidersEvent event, AdvancedParticleProviderRegistrationInfo<T> info)
    {
        event.registerSpriteSet(info.particle_type().function(), new SpriteParticleImplementation_Advanced<>(info.particle_provider_creater()));
    }

    private static void RegisterItemColorHandler(RegisterColorHandlersEvent.Item event, ItemColorHandlerRegistrationInfo info)
    {
        event.register(info.item_color(), info.items().function());
    }

    private static void RegisterBlockColorHandler(RegisterColorHandlersEvent.Block event, BlockColorHandlerRegistrationInfo info)
    {
        event.register(info.block_color(), info.blocks().function());
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        if (menu_screens_info.HasItems()) {
            ForgeUtils.AddListener(bus, FMLClientSetupEvent.class, new RegisterAllMenuScreensAggregator(menu_screens_info));
        }
        menu_screens_info = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterColorHandlersEvent.Item.class, item_color_handler_infos, ForgeClientArtifactsRegistrar::RegisterItemColorHandler);
        item_color_handler_infos = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterColorHandlersEvent.Block.class, block_color_handler_infos, ForgeClientArtifactsRegistrar::RegisterBlockColorHandler);
        block_color_handler_infos = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterParticleProvidersEvent.class, simple_particle_reg, ForgeClientArtifactsRegistrar::RegisterParticleProvider_Simple);
        simple_particle_reg = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterParticleProvidersEvent.class, advanced_particle_reg, ForgeClientArtifactsRegistrar::RegisterParticleProvider_Advanced);
        advanced_particle_reg = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, EntityRenderersEvent.RegisterLayerDefinitions.class, model_defs_infos, ForgeClientArtifactsRegistrar::RegisterModelLayerDefinitions);
        model_defs_infos = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, EntityRenderersEvent.RegisterRenderers.class, entity_renderer_infos, ForgeClientArtifactsRegistrar::RegisterEntityRenderer);
        entity_renderer_infos = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, EntityRenderersEvent.RegisterRenderers.class, block_entity_renderer_infos, ForgeClientArtifactsRegistrar::RegisterBlockEntityRenderer);
        block_entity_renderer_infos = null;
    }

    private record MenuScreenRegInfo<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M, U> constructor)
    {
        private record MSCToMenuConstructor<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(MenuScreenConstructor<M, U> constructor)
                implements MenuScreens.ScreenConstructor<M , U>
        {
            @Override
            public U create(M abstractContainerMenu, Inventory inventory, Component component) {
                return constructor.Create(abstractContainerMenu, inventory, component);
            }
        }

        public void RegisterToMenuScreens() {
            MenuScreens.register(type.function() , new MSCToMenuConstructor<>(constructor));
        }
    }

    private record SpriteParticleImplementation_Simple<T extends ParticleOptions>(ParticleProvider<T> prov)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) { return prov; }
    }

    private record SpriteParticleImplementation_Advanced<T extends ParticleOptions>(Func2<SpriteSet, ParticleProvider<T>> provider_function)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) { return provider_function.function(spriteSet); }
    }

    private static final class RegisterAllMenuScreensAggregator
        implements Runnable, Action1<FMLClientSetupEvent>
    {
        private SingleLinkedListBasedRegister<MenuScreenRegInfo<? , ?>> registrations;

        public RegisterAllMenuScreensAggregator(SingleLinkedListBasedRegister<MenuScreenRegInfo<? , ?>> registrations) { this.registrations = registrations; }

        @Override
        public void run()
        {
            IEnumerator<MenuScreenRegInfo<?, ?>> e = registrations.GetEnumerator();
            try {
                while (e.MoveNext()) {
                    e.getCurrent().RegisterToMenuScreens();
                }
            } finally {
                e.Dispose();
                registrations = null;
            }
        }

        @Override
        public void action(FMLClientSetupEvent obj) { obj.enqueueWork(this); }
    }
}
