package com.github.mdcdi1315.basemodslib.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import com.github.mdcdi1315.basemodslib.item.IBlockEntityItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BlockEntityWithoutLevelRendererMixin
{
    @Accessor("blockEntityRenderDispatcher")
    protected abstract BlockEntityRenderDispatcher GetDispatcher();

    @Inject(method = "renderByItem", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void OnRender(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci)
    {
        if (stack.getItem() instanceof IBlockEntityItem i) {
            GetDispatcher().renderItem(i.GetBlockEntity(), poseStack , buffer, packedLight, packedOverlay);
            ci.cancel();
        }
    }
}
