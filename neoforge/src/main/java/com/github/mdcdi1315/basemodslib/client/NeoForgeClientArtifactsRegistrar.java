package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class NeoForgeClientArtifactsRegistrar
    implements IBlockEntityRendererRegistrar,
        IColorHandlersRegistrar,
        IEntityRendererRegistrar,
        IModelDefinitionRegistrar,
        IParticleProviderRegistrar
{
    private List<ModelDefinitionRegistrationInfo> model_infos;
    private List<ItemColorHandlerRegistrationInfo> item_colors;
    private List<BlockColorHandlerRegistrationInfo> block_colors;
    private List<EntityRendererRegistrationInfo<? extends Entity>> entities;
    private List<SimpleParticleProviderRegistrationInfo<? extends ParticleOptions>> particles_simple;
    private List<AdvancedParticleProviderRegistrationInfo<? extends ParticleOptions>> particles_advanced;
    private List<BlockEntityRendererRegistrationInfo<? extends BlockEntity, ? extends BlockEntityRenderState>> block_entities;

    public NeoForgeClientArtifactsRegistrar()
    {
        entities = new List<>();
        model_infos = new List<>();
        item_colors = new List<>();
        block_colors = new List<>();
        block_entities = new List<>();
        particles_simple = new List<>();
        particles_advanced = new List<>();
    }

    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void Register(BlockEntityRendererRegistrationInfo<T, S> info)
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

    private static <T extends BlockEntity, S extends BlockEntityRenderState> void RegisterBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event, BlockEntityRendererRegistrationInfo<T, S> info) {
        event.registerBlockEntityRenderer(info.type().function() , info.provider());
    }

    private static <T extends Entity> void RegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event, EntityRendererRegistrationInfo<T> info) {
        event.registerEntityRenderer(info.entity_type_provider().function(), info.renderer_provider());
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        bus.addListener(this::RegisterModelsEventDef);
        bus.addListener(this::RegisterRenderersEventDef);
        bus.addListener(this::RegisterItemColorHandlersEventDef);
        bus.addListener(this::RegisterParticleProvidersEventDef);
        bus.addListener(this::RegisterBlockColorHandlersEventDef);
    }

    private void RegisterRenderersEventDef(EntityRenderersEvent.RegisterRenderers event)
    {
        var block_ent_en = block_entities.GetEnumerator();
        try {
            while (block_ent_en.MoveNext()) {
                RegisterBlockEntityRenderer(event , block_ent_en.getCurrent());
            }
        } finally {
            block_ent_en.Dispose();;
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

    private record SimpleParticleRegistration<T extends ParticleOptions>(ParticleProvider<T> provider)
        implements ParticleResources.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) {
            return provider;
        }
    }

    private record Func2ToSpriteParticleRegistration<T extends ParticleOptions>(Func2<SpriteSet, ParticleProvider<T>> function)
        implements ParticleResources.SpriteParticleRegistration<T>
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
}
