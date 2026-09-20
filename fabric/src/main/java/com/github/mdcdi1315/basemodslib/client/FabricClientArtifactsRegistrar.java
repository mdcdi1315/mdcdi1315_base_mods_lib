package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

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
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltInBlockModelsCallback;

import java.util.List;

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
    private SingleLinkedListBasedRegister<SpecialModelRendererRegistrationInfo> special_model_renderers;

    public FabricClientArtifactsRegistrar()
    {
        special_model_renderers = new SingleLinkedListBasedRegister<>();
    }

    @Override
    public <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        EntityRenderers.register(info.entity_type_provider().function(), info.renderer_provider());
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
        BlockColorRegistry.register(List.of(info.block_color()), info.blocks().function());
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
        ArgumentNullException.ThrowIfNull(info, "info");
        ModelLayerRegistry.registerModelLayer(info.location(), new ModelLayerDefinitionFunction(info.definition()));
    }

    @Override
    public <T extends ParticleOptions> void Register(SimpleParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ParticleProviderRegistry.getInstance().register(info.particle_type().function(), info.particle_provider());
    }

    @Override
    public <T extends ParticleOptions> void Register(AdvancedParticleProviderRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ParticleProviderRegistry.getInstance().register(info.particle_type().function(), new ParticleFactoryRegistryAdvancedInfoTranslation<>(info.particle_provider_creater()));
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
        special_model_renderers.Register(info);
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void RegisterMenuScreen(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M, U> constructor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(constructor, "constructor");
        MenuScreens.register(type.function() , new MSCToMenuConstructor<>(constructor));
    }

    public void Finalize()
    {
        if (special_model_renderers.HasItems())
        {
            BuiltInBlockModelsCallback.EVENT.register(new BuiltInBlockModelsCallbackHandler(special_model_renderers));
        }
        special_model_renderers = null;
    }

    @SuppressWarnings("NullableProblems")
    private record MSCToMenuConstructor<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(MenuScreenConstructor<M, U> constructor)
        implements MenuScreens.ScreenConstructor<M , U>
    {
        @Override
        public U create(M abstractContainerMenu, Inventory inventory, Component component) {
            return constructor.Create(abstractContainerMenu, inventory, component);
        }
    }

    @SuppressWarnings("NullableProblems")
    private record ParticleFactoryRegistryAdvancedInfoTranslation<T extends ParticleOptions>(Func2<SpriteSet, ParticleProvider<T>> function)
        implements ParticleProviderRegistry.PendingParticleProvider<T>
    {
        @Override
        public ParticleProvider<T> create(FabricSpriteSet spriteSet) { return function.function(spriteSet); }
    }

    @SuppressWarnings("NullableProblems")
    private record ModelLayerDefinitionFunction(Func1<LayerDefinition> definition)
        implements ModelLayerRegistry.TexturedLayerDefinitionProvider
    {
        @Override
        public LayerDefinition createLayerDefinition() { return definition.function(); }
    }
}
