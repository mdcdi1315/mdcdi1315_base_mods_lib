package com.github.mdcdi1315.basemodslib.forge.mixin;

import com.github.mdcdi1315.basemodslib.item.IBlockEntityItem;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.entity.ItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public final class ItemRendererMixin
{
    @Inject(
            method = "render",
            cancellable = true,
            at = @At(
                value = "INVOKE",
                remap = false, // While the method to inject is obfuscated, the Minecraft Forge get custom renderer one is not.
                target = "Lnet/minecraftforge/client/extensions/common/IClientItemExtensions;of(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraftforge/client/extensions/common/IClientItemExtensions;"
            )
    )
    private void OnRenderItem(ItemStack stack, ItemDisplayContext dc, boolean left_hand, PoseStack pose_stack, MultiBufferSource buffer, int combined_light, int combined_overlay, BakedModel model, CallbackInfo callback_info)
    {
        if (stack.getItem() instanceof IBlockEntityItem ib) {
            // Render our block entity item
            Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(ib.GetBlockEntity(stack), pose_stack , buffer , combined_light, combined_overlay);

            // Because we need to cancel execution, the pose stack will not be popped appropriately.
            // As such, pose stack popping is done manually here.

            pose_stack.popPose();
            callback_info.cancel();
        }
    }
}
