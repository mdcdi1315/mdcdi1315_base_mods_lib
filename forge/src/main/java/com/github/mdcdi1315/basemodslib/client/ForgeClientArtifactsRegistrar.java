package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.basemodslib.utils.Func1ToSupplier;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public final class ForgeClientArtifactsRegistrar
    implements IBlockEntityRendererRegistrar,
        IEntityRendererRegistrar,
        IColorHandlersRegistrar,
        IModelDefinitionRegistrar,
        IParticleProviderRegistrar
{
    private List<BlockEntityRendererRegistrationInfo<?>> block_entity_renderer_infos;
    private List<EntityRendererRegistrationInfo<?>> entity_renderer_infos;
    private List<BlockColorHandlerRegistrationInfo> block_color_handler_infos;
    private List<ItemColorHandlerRegistrationInfo> item_color_handler_infos;
    private List<ModelDefinitionRegistrationInfo> model_defs_infos;
    private List<SimpleParticleProviderRegistrationInfo<?>> simple_particle_reg;
    private List<AdvancedParticleProviderRegistrationInfo<?>> advanced_particle_reg;

    public ForgeClientArtifactsRegistrar() {
        block_entity_renderer_infos = new List<>();
        entity_renderer_infos = new List<>();
        block_color_handler_infos = new List<>();
        item_color_handler_infos = new List<>();
        model_defs_infos = new List<>();
        simple_particle_reg = new List<>();
        advanced_particle_reg = new List<>();
    }

    @Override
    public <T extends BlockEntity> void Register(BlockEntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_entity_renderer_infos.Add(info);
    }

    @Override
    public <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        entity_renderer_infos.Add(info);
    }

    @Override
    public void Register(ItemColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        item_color_handler_infos.Add(info);
    }

    @Override
    public void Register(BlockColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        block_color_handler_infos.Add(info);
    }

    @Override
    public void Register(ModelDefinitionRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info,"info");
        model_defs_infos.Add(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(SimpleParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        simple_particle_reg.Add(info);
    }

    @Override
    public <T extends ParticleOptions> void Register(AdvancedParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        advanced_particle_reg.Add(info);
    }

    // Currently, all the below data are run only once.
    // So we can sweep up memory just after our objects are left from our side.

    private void OnRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers rends)
    {
        block_entity_renderer_infos.ForEach(new RegisterBlockEntityRendererEventMethod(rends));
        block_entity_renderer_infos.Clear();
        block_entity_renderer_infos = null;
        entity_renderer_infos.ForEach(new RegisterEntityRendererEventMethod(rends));
        entity_renderer_infos.Clear();
        entity_renderer_infos = null;
    }

    private void OnRegisterModelDefinitions(EntityRenderersEvent.RegisterLayerDefinitions layer_defs)
    {
        model_defs_infos.ForEach(new RegisterModelDefinitionsEventMethod(layer_defs));
        model_defs_infos.Clear();
        model_defs_infos = null;
    }

    private void OnRegisterBlockColorHandlers(RegisterColorHandlersEvent.Block event)
    {
        block_color_handler_infos.ForEach(new RegisterBlockColorHandlersEventMethod(event));
        block_color_handler_infos.Clear();
        block_color_handler_infos = null;
    }

    private void OnRegisterItemColorHandlers(RegisterColorHandlersEvent.Item item)
    {
        item_color_handler_infos.ForEach(new RegisterItemColorHandlersEventMethod(item));
        item_color_handler_infos.Clear();
        item_color_handler_infos = null;
    }

    private void OnRegisterParticleProviders(RegisterParticleProvidersEvent particle_reg_event)
    {
        simple_particle_reg.ForEach(new RegisterSimpleParticleProvider(particle_reg_event));
        simple_particle_reg.Clear();
        simple_particle_reg = null;
        advanced_particle_reg.ForEach(new RegisterAdvancedParticleProvider(particle_reg_event));
        advanced_particle_reg.Clear();
        advanced_particle_reg = null;
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        bus.addListener(this::OnRegisterEntityRenderers);
        bus.addListener(this::OnRegisterModelDefinitions);
        bus.addListener(this::OnRegisterParticleProviders);
        bus.addListener(this::OnRegisterItemColorHandlers);
        bus.addListener(this::OnRegisterBlockColorHandlers);
    }

    private record SpriteParticleImplementation_Simple<T extends ParticleOptions>(ParticleProvider<T> prov)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) {
            return prov;
        }
    }

    private record SpriteParticleImplementation_Advanced<T extends ParticleOptions>(Func2<SpriteSet, ParticleProvider<T>> provider_function)
        implements ParticleEngine.SpriteParticleRegistration<T>
    {
        @Override
        public ParticleProvider<T> create(SpriteSet spriteSet) {
            return provider_function.function(spriteSet);
        }
    }

    private record RegisterSimpleParticleProvider(RegisterParticleProvidersEvent event)
        implements Action1<SimpleParticleProviderRegistrationInfo<?>>
    {
        private <T extends ParticleOptions> void RegisterInternal(SimpleParticleProviderRegistrationInfo<T> info) {
            event.registerSpriteSet(info.particle_type().function(), new SpriteParticleImplementation_Simple<>(info.particle_provider()));
        }

        @Override
        public void action(SimpleParticleProviderRegistrationInfo<?> obj) {
            RegisterInternal(obj);
        }
    }

    private record RegisterAdvancedParticleProvider(RegisterParticleProvidersEvent event)
        implements Action1<AdvancedParticleProviderRegistrationInfo<?>>
    {
        private <T extends ParticleOptions> void RegisterInternal(AdvancedParticleProviderRegistrationInfo<T> info) {
            event.registerSpriteSet(info.particle_type().function(), new SpriteParticleImplementation_Advanced<>(info.particle_provider_creater()));
        }

        @Override
        public void action(AdvancedParticleProviderRegistrationInfo<?> obj) {
            RegisterInternal(obj);
        }
    }

    private record RegisterBlockEntityRendererEventMethod(EntityRenderersEvent.RegisterRenderers event)
            implements Action1<BlockEntityRendererRegistrationInfo<?>>
    {
        private <T extends BlockEntity> void RegisterInternal(BlockEntityRendererRegistrationInfo<T> inf) {
            event.registerBlockEntityRenderer(inf.type().function(), inf.provider());
        }

        @Override
        public void action(BlockEntityRendererRegistrationInfo<?> obj) {
            RegisterInternal(obj);
        }
    }

    private record RegisterEntityRendererEventMethod(EntityRenderersEvent.RegisterRenderers event)
        implements Action1<EntityRendererRegistrationInfo<?>>
    {
        private <T extends Entity> void RegisterInternal(EntityRendererRegistrationInfo<T> inf) {
            event.registerEntityRenderer(inf.entity_type_provider().function(), inf.renderer_provider());
        }

        @Override
        public void action(EntityRendererRegistrationInfo<?> obj) {
            RegisterInternal(obj);
        }
    }

    private record RegisterBlockColorHandlersEventMethod(RegisterColorHandlersEvent.Block event)
        implements Action1<BlockColorHandlerRegistrationInfo>
    {
        @Override
        public void action(BlockColorHandlerRegistrationInfo obj) {
            event.register(obj.block_color(), obj.blocks().function());
        }
    }

    private record RegisterItemColorHandlersEventMethod(RegisterColorHandlersEvent.Item event)
            implements Action1<ItemColorHandlerRegistrationInfo>
    {
        @Override
        public void action(ItemColorHandlerRegistrationInfo obj) {
            event.register(obj.item_color(), obj.items().function());
        }
    }

    private record RegisterModelDefinitionsEventMethod(EntityRenderersEvent.RegisterLayerDefinitions layer_defs)
        implements Action1<ModelDefinitionRegistrationInfo>
    {
        @Override
        public void action(ModelDefinitionRegistrationInfo obj) {
            layer_defs.registerLayerDefinition(obj.location(), new Func1ToSupplier<>(obj.definition()));
        }
    }
}
