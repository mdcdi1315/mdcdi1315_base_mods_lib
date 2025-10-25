package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public record ItemRegistrationInformation(
        Func2<ResourceLocation, Item> item_getter,
        @NotNull CreativeModeTab... tabs
) {
    public ItemRegistrationInformation {
        ArgumentNullException.ThrowIfNull(tabs, "tabs");
        ArgumentNullException.ThrowIfNull(item_getter, "item_getter");
    }
}
