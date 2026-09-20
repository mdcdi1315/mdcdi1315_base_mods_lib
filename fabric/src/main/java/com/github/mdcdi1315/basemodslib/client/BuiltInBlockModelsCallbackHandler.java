package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltInBlockModelsCallback;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.BuiltInBlockModels;

@SuppressWarnings("NullableProblems")
record BuiltInBlockModelsCallbackHandler(
        SingleLinkedListBasedRegister<SpecialModelRendererRegistrationInfo> infos
) implements BuiltInBlockModelsCallback
{
    @Override
    public void createBlockModels(BuiltInBlockModels.Builder builder)
    {
        try (var enumerator = infos.GetEnumerator())
        {
            SpecialModelRendererRegistrationInfo info;
            while (enumerator.MoveNext())
            {
                info = enumerator.getCurrent();
                builder.put(new RendererModelFactoryToMC(info.unbaked_model_factory()), info.block().function());
            }
        }
    }

    private record RendererModelFactoryToMC(RendererModelFactory factory)
            implements BuiltInBlockModels.ModelFactory
    {
        @Override
        public BlockModel.Unbaked create(BlockColors blockColors, BlockState blockState) { return factory.Create(blockColors, blockState); }
    }
}
