package com.github.mdcdi1315.basemodslib.forge.mixin;

import com.github.mdcdi1315.basemodslib.forge.ForgeClientModLoaderLayer;

import com.mojang.serialization.MapCodec;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelRenderers.class)
public final class SpecialModelRenderersMixin
{
    @Final
    @Accessor("ID_MAPPER")
    private static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> GetIdMapper()
    {
        throw new AssertionError("Implemented by Mixin");
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void OnInit(CallbackInfo ci)
    {
        // We can just pass the ID mapper method reference back to our client mod loader layer, client registrar objects
        // will store a reference of this method and will subsequently use it to register all the codecs they deem that they need.
        ForgeClientModLoaderLayer.id_mapper_method = GetIdMapper()::put;
    }
}
