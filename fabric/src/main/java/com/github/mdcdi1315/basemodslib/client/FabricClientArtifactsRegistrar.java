package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

@ClientOnlyEnvironment
public final class FabricClientArtifactsRegistrar
    implements IEntityRendererRegistrar,
        IBlockEntityRendererRegistrar,
        IModelDefinitionRegistrar,
        IColorHandlersRegistrar,
        IParticleProviderRegistrar
{
    @Override
    public <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        EntityRenderers.register(info.entity_type_provider().function() , info.renderer_provider());
    }

    @Override
    public void Register(ItemColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ItemTintSources.ID_MAPPER.put(info.location() , info.tint_source());
    }

    @Override
    public void Register(BlockColorHandlerRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ColorProviderRegistry.BLOCK.register(info.block_color(), info.blocks().function());
    }

    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void Register(BlockEntityRendererRegistrationInfo<T, S> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        BlockEntityRenderers.register(info.type().function(), info.provider());
    }

    @Override
    public void Register(ModelDefinitionRegistrationInfo info)
            throws ArgumentNullException
    {
        EntityModelLayerRegistry.registerModelLayer(info.location(), new ModelLayerDefinitionFunction(info.definition()));
    }

    @Override
    public <T extends ParticleOptions> void Register(SimpleParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ParticleFactoryRegistry.getInstance().register(info.particle_type().function(), info.particle_provider());
    }

    @Override
    public <T extends ParticleOptions> void Register(AdvancedParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ParticleFactoryRegistry.getInstance().register(info.particle_type().function(), new ParticleFactoryRegistryAdvancedInfoTranslation<>(info.particle_provider_creater()));
    }

    private record ParticleFactoryRegistryAdvancedInfoTranslation<T extends ParticleOptions>(Func2<SpriteSet, ParticleProvider<T>> function)
        implements ParticleFactoryRegistry.PendingParticleFactory<T>
    {
        @Override
        public ParticleProvider<T> create(FabricSpriteProvider provider) {
            return function.function(provider);
        }
    }

    private record ModelLayerDefinitionFunction(Func1<LayerDefinition> definition)
        implements EntityModelLayerRegistry.TexturedModelDataProvider
    {
        @Override
        public LayerDefinition createModelData() {
            return definition.function();
        }
    }
}
