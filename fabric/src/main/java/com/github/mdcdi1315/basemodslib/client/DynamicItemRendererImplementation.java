package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.basemodslib.item.IBlockEntityItem;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

public final class DynamicItemRendererImplementation
    implements BuiltinItemRendererRegistry.DynamicItemRenderer
{
    private final BlockEntityRenderDispatcher dispatcher;
    private final IBlockEntityItem item_to_be_rendered;

    public DynamicItemRendererImplementation(IBlockEntityItem item) {
        dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        item_to_be_rendered = item;
    }

    @Override
    public void render(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int packed_light, int packed_overlay)
    {
        dispatcher.renderItem(
                item_to_be_rendered.GetBlockEntity(),
                poseStack,
                multiBufferSource,
                packed_light,
                packed_overlay
        );
    }
}
