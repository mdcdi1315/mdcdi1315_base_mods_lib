package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;

import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

@ClientOnlyEnvironment
public final class FabricClientArtifactsRegistrar
    implements IEntityRendererRegistrar,
        IBlockEntityRendererRegistrar,
        IModelDefinitionRegistrar,
        IColorHandlersRegistrar,
        IParticleProviderRegistrar,
        IMenuScreensRegistrar,
        ISpecialModelRendererRegistrar
{
    @Override
    public <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        EntityRendererRegistry.register(info.entity_type_provider().function(), info.renderer_provider());
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
    public <T extends BlockEntity> void Register(BlockEntityRendererRegistrationInfo<T> info)
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

    @Override
    public void RegisterCodec(SpecialModelRendererCodecRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        SpecialModelRenderers.ID_MAPPER.put(info.location() , info.renderer_codec());
    }

    @Override
    public void Register(SpecialModelRendererRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        SpecialBlockRendererRegistry.register(info.block().function() , info.unbaked_renderer());
    }

    private record MSCToMenuConstructor<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(MenuScreenConstructor<M, U> constructor)
        implements MenuScreens.ScreenConstructor<M , U>
    {
        @Override
        public U create(M abstractContainerMenu, Inventory inventory, Component component) {
            return constructor.Create(abstractContainerMenu, inventory, component);
        }
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void RegisterMenuScreen(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M, U> constructor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(constructor, "constructor");
        MenuScreens.register(type.function() , new MSCToMenuConstructor<>(constructor));
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
