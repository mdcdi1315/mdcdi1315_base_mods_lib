package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.color.item.ItemTintSource;

public record ItemColorHandlerRegistrationInfo(
        ResourceLocation location,
        MapCodec<? extends ItemTintSource> tint_source
) {
    public ItemColorHandlerRegistrationInfo {
        ArgumentNullException.ThrowIfNull(location, "location");
        ArgumentNullException.ThrowIfNull(tint_source, "tint_source");
    }
}
