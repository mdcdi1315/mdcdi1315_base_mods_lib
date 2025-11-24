package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.gui.screens.inventory.MenuAccess;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.*;

public final class NeoForgeClientArtifactsRegistrar
    implements IBlockEntityRendererRegistrar,
        IColorHandlersRegistrar,
        IEntityRendererRegistrar,
        IModelDefinitionRegistrar,
        IParticleProviderRegistrar,
        IMenuScreensRegistrar,
        ISpecialModelRendererRegistrar
{
    private List<MenuScreenRegInfo<? , ?>> menu_screens_info;
    private List<ModelDefinitionRegistrationInfo> model_infos;
    private List<ItemColorHandlerRegistrationInfo> item_colors;
    private List<BlockColorHandlerRegistrationInfo> block_colors;
    private List<EntityRendererRegistrationInfo<? extends Entity>> entities;
    private List<SpecialModelRendererRegistrationInfo> model_renderer_registrations;
    private List<BlockEntityRendererRegistrationInfo<? extends BlockEntity>> block_entities;
    private List<SpecialModelRendererCodecRegistrationInfo> model_renderer_codec_registrations;
    private List<SimpleParticleProviderRegistrationInfo<? extends ParticleOptions>> particles_simple;
    private List<AdvancedParticleProviderRegistrationInfo<? extends ParticleOptions>> particles_advanced;

    public NeoForgeClientArtifactsRegistrar()
    {
        entities = new List<>();
        model_infos = new List<>();
        item_colors = new List<>();
        block_colors = new List<>();
        block_entities = new List<>();
        particles_simple = new List<>();
        menu_screens_info = new List<>();
        particles_advanced = new List<>();
        model_renderer_registrations = new List<>();
        model_renderer_codec_registrations = new List<>();
    }

    @Override
    public <T extends BlockEntity> void Register(BlockEntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_entities.Add(info);
    }

    @Override
    public void Register(ItemColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        item_colors.Add(info);
    }

    @Override
    public void Register(BlockColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_colors.Add(info);
    }

    @Override
    public <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        entities.Add(info);
    }

    @Override
    public void Register(ModelDefinitionRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        model_infos.Add(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(SimpleParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        particles_simple.Add(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(AdvancedParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        particles_advanced.Add(info);
    }

    private static <T extends BlockEntity> void RegisterBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event, BlockEntityRendererRegistrationInfo<T> info) {
        event.registerBlockEntityRenderer(info.type().function() , info.provider());
    }

    private static <T extends Entity> void RegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event, EntityRendererRegistrationInfo<T> info) {
        event.registerEntityRenderer(info.entity_type_provider().function(), info.renderer_provider());
    }

    private void RegisterRenderersEventDef(EntityRenderersEvent.RegisterRenderers event)
    {
        var block_ent_en = block_entities.GetEnumerator();
        try {
            while (block_ent_en.MoveNext()) {
                RegisterBlockEntityRenderer(event , block_ent_en.getCurrent());
            }
        } finally {
            block_ent_en.Dispose();
        }
        block_entities = null;
        var entity_en = entities.GetEnumerator();
        try {
            while (entity_en.MoveNext()) {
                RegisterEntityRenderer(event, entity_en.getCurrent());
            }
        } finally {
            entity_en.Dispose();
        }
        entities = null;
    }

    private void RegisterModelsEventDef(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        var en = model_infos.GetEnumerator();
        try {
            ModelDefinitionRegistrationInfo info;
            while (en.MoveNext()) {
                info = en.getCurrent();
                event.registerLayerDefinition(info.location() , info.definition());
            }
        } finally {
            en.Dispose();
        }
        model_infos = null;
    }

    private void RegisterItemColorHandlersEventDef(RegisterColorHandlersEvent.ItemTintSources event)
    {
        var items = item_colors.GetEnumerator();
        try {
            ItemColorHandlerRegistrationInfo info;
            while (items.MoveNext()) {
                info = items.getCurrent();
                event.register(info.location() , info.tint_source());
            }
        } finally {
            items.Dispose();
        }
        item_colors = null;
    }

    private void RegisterBlockColorHandlersEventDef(RegisterColorHandlersEvent.Block event)
    {
        var blocks = block_colors.GetEnumerator();
        try {
            BlockColorHandlerRegistrationInfo info;
            while (blocks.MoveNext()) {
                info = blocks.getCurrent();
                event.register(info.block_color(), info.blocks().function());
            }
        } finally {
            blocks.Dispose();
        }
        block_colors = null;
    }

    private static <T extends ParticleOptions> void RegisterSimpleParticleProvider(RegisterParticleProvidersEvent event, SimpleParticleProviderRegistrationInfo<T> info)
    {
        event.registerSpriteSet(info.particle_type().function() , new SimpleParticleRegistration<>(info.particle_provider()));
    }

    @Override
    public void RegisterCodec(SpecialModelRendererCodecRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        model_renderer_codec_registrations.Add(info);
    }

    @Override
    public void Register(SpecialModelRendererRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        model_renderer_registrations.Add(info);
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

        public void RegisterByEvent(RegisterMenuScreensEvent event) {
            event.register(type.function(), new MSCToMenuConstructor<>(constructor));
        }
    }

    private void OnRegisterMenuScreensEventDef(RegisterMenuScreensEvent event)
    {
        var en = menu_screens_info.GetEnumerator();
        try {
            while (en.MoveNext()) {
                en.getCurrent().RegisterByEvent(event);
            }
        } finally {
            en.Dispose();
        }
        menu_screens_info.Clear();
        menu_screens_info = null;
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void RegisterMenuScreen(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M, U> constructor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(constructor, "constructor");
        menu_screens_info.Add(new MenuScreenRegInfo<>(type, constructor));
    }

    private record SimpleParticleRegistration<T extends ParticleOptions>(ParticleProvider<T> provider)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) {
            return provider;
        }
    }

    private record Func2ToSpriteParticleRegistration<T extends ParticleOptions>(Func2<SpriteSet, ParticleProvider<T>> function)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) {
            return function.function(spriteSet);
        }
    }

    private static <T extends ParticleOptions> void RegisterAdvancedParticleProvider(RegisterParticleProvidersEvent event, AdvancedParticleProviderRegistrationInfo<T> info)
    {
        event.registerSpriteSet(info.particle_type().function() , new Func2ToSpriteParticleRegistration<>(info.particle_provider_creater()));
    }

    private void RegisterParticleProvidersEventDef(RegisterParticleProvidersEvent particle_reg_event)
    {
        var simple = particles_simple.GetEnumerator();
        try {
            while (simple.MoveNext()) {
                RegisterSimpleParticleProvider(particle_reg_event, simple.getCurrent());
            }
        } finally {
            simple.Dispose();
        }
        particles_simple = null;
        var advanced = particles_advanced.GetEnumerator();
        try {
            while (advanced.MoveNext()) {
                RegisterAdvancedParticleProvider(particle_reg_event, advanced.getCurrent());
            }
        } finally {
            advanced.Dispose();
        }
        particles_advanced = null;
    }

    private void OnRegisterSpecialModelRenderers_Codec(RegisterSpecialModelRendererEvent event)
    {
        var en = model_renderer_codec_registrations.GetEnumerator();
        try {
            SpecialModelRendererCodecRegistrationInfo inf;
            while (en.MoveNext()) {
                inf = en.getCurrent();
                event.register(inf.location() , inf.renderer_codec());
            }
        } finally {
            en.Dispose();
        }
        model_renderer_codec_registrations = null;
    }

    private void OnRegisterSpecialModelRenderers(RegisterSpecialBlockModelRendererEvent event)
    {
        var en = model_renderer_registrations.GetEnumerator();
        try {
            SpecialModelRendererRegistrationInfo inf;
            while (en.MoveNext()) {
                inf = en.getCurrent();
                event.register(inf.block().function() , inf.unbaked_renderer());
            }
        } finally {
            en.Dispose();
        }
        // We can't delete this, because Minecraft can reload these renderers at some time.
        // We are cooked if we destroy this object.
        // model_renderer_registrations = null;
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        NeoForgeUtils.AddListener(bus, RegisterMenuScreensEvent.class, this::OnRegisterMenuScreensEventDef);
        NeoForgeUtils.AddListener(bus, EntityRenderersEvent.RegisterLayerDefinitions.class , this::RegisterModelsEventDef);
        NeoForgeUtils.AddListener(bus, EntityRenderersEvent.RegisterRenderers.class , this::RegisterRenderersEventDef);
        NeoForgeUtils.AddListener(bus, RegisterColorHandlersEvent.ItemTintSources.class , this::RegisterItemColorHandlersEventDef);
        NeoForgeUtils.AddListener(bus, RegisterParticleProvidersEvent.class, this::RegisterParticleProvidersEventDef);
        NeoForgeUtils.AddListener(bus, RegisterColorHandlersEvent.Block.class, this::RegisterBlockColorHandlersEventDef);
        NeoForgeUtils.AddListener(bus, RegisterSpecialModelRendererEvent.class, this::OnRegisterSpecialModelRenderers_Codec);
        NeoForgeUtils.AddListener(bus, RegisterSpecialBlockModelRendererEvent.class , this::OnRegisterSpecialModelRenderers);
    }
}
