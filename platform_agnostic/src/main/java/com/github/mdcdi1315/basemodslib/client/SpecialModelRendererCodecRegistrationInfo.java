package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.mojang.serialization.MapCodec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.special.SpecialModelRenderer;

/**
 * Provides information for registering a new special model renderer codec to Minecraft. <br />
 * Typically it is used for saving data for rendered block entities.
 * @param location The location of the renderer codec to register.
 * @param renderer_codec The codec to use for this special model renderer.
 * @since 1.0.11
 */
public record SpecialModelRendererCodecRegistrationInfo(
        @NotNull ResourceLocation location,
        @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> renderer_codec
) {
    /**
     * Constructs a new instance of the {@link SpecialModelRendererCodecRegistrationInfo} class.
     * @param location The location that the new renderer will have. This is typically the location of the block that will be later rendered.
     * @param renderer_codec The codec to use for this special model renderer.
     */
    public SpecialModelRendererCodecRegistrationInfo {
        ArgumentNullException.ThrowIfNull(location, "location");
        ArgumentNullException.ThrowIfNull(renderer_codec , "renderer_codec");
    }
}
