package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.level.ItemLike;
import net.minecraft.client.color.item.ItemColor;

public record ItemColorHandlerRegistrationInfo(
        Func1<ItemLike[]> items,
        ItemColor item_color
) {
    public ItemColorHandlerRegistrationInfo {
        ArgumentNullException.ThrowIfNull(item_color, "item_color");
        ArgumentNullException.ThrowIfNull(items, "items");
    }
}
