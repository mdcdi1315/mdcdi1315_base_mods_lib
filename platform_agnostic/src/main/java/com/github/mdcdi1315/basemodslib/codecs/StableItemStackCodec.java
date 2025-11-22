package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Defines a codec for {@link ItemStack}s that it's object data will not be changed between versions of the mod and Minecraft versions. <br />
 * Its instance is retrieved through the {@link StableItemStackCodec#INSTANCE} field.
 */
public final class StableItemStackCodec
{
    // Do not let anyone instantiate this class.
    private StableItemStackCodec() {}

    /**
     * Gets the single and the only one instance of this codec implementation.
     */
    public static final Codec<ItemStack> INSTANCE =
            CodecUtils.CreateCodecDirect(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
                    CodecUtils.ZERO_OR_POSITIVE_INTEGER.fieldOf("count").forGetter(ItemStack::getCount),
                    CompoundTag.CODEC.optionalFieldOf("tag" , new CompoundTag()).forGetter(ItemStack::getTag),
                    StableItemStackCodec::CreateItemStack
            );

    private static ItemStack CreateItemStack(Item item , int count , CompoundTag ct)
    {
        ItemStack is = new ItemStack(Holder.direct(item) , count);
        is.setTag(ct);
        return is;
    }
}
