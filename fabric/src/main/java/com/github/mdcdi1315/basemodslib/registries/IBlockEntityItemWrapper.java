package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.item.IBlockEntityItem;
import com.github.mdcdi1315.basemodslib.client.DynamicItemRendererImplementation;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import net.minecraft.world.item.Item;

@ClientOnlyEnvironment
public final class IBlockEntityItemWrapper
{
    private IBlockEntityItemWrapper() {}

    public static void RegisterItem(Item it)
    {
        if (it instanceof IBlockEntityItem ib) {
            RegisterItemInternal(ib);
        }
    }

    private static void RegisterItemInternal(IBlockEntityItem it) {
        BuiltinItemRendererRegistry.INSTANCE.register((Item) it, new DynamicItemRendererImplementation(it));
    }
}
