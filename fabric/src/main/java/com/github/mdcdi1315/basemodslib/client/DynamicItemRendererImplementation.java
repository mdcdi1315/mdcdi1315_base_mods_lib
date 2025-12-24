package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.basemodslib.item.IBlockEntityItem;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.MultiBufferSource;

public final class DynamicItemRendererImplementation
    implements BuiltinItemRendererRegistry.DynamicItemRenderer
{
    private final IBlockEntityItem item_to_be_rendered;

    public DynamicItemRendererImplementation(IBlockEntityItem item) {
        item_to_be_rendered = item;
    }

    @Override
    public void render(ItemStack item_stack, ItemDisplayContext dc, PoseStack pose_stack, MultiBufferSource buffer, int packed_light, int packed_overlay)
    {
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(
                item_to_be_rendered.GetBlockEntity(item_stack),
                pose_stack,
                buffer,
                packed_light,
                packed_overlay
        );
    }
}
