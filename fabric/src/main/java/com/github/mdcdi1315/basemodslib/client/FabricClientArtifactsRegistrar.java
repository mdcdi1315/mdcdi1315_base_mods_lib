package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class FabricClientArtifactsRegistrar
    implements IEntityRendererRegistrar,
        IBlockEntityRendererRegistrar,
        IModelDefinitionRegistrar,
        IColorHandlersRegistrar
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
        ColorProviderRegistry.ITEM.register(info.item_color(), info.items().function());
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

    private record ModelLayerDefinitionFunction(Func1<LayerDefinition> definition)
        implements EntityModelLayerRegistry.TexturedModelDataProvider
    {
        @Override
        public LayerDefinition createModelData() {
            return definition.function();
        }
    }
}
