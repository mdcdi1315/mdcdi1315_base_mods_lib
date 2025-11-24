package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Map;
import java.util.Optional;

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
                    CompoundTag.CODEC.optionalFieldOf("tag").forGetter(StableItemStackCodec::FindCustomDataOrEmpty),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch),
                    StableItemStackCodec::CreateItemStack
            );

    @SuppressWarnings("unchecked")
    private static <T> void SetNewPatchItemIndirection2(DataComponentPatch.Builder builder , DataComponentType<T> type , Object value)
    {
        builder.set(type, (T)value);
    }

    private static void SetNewPatchItem(DataComponentPatch.Builder builder , Map.Entry<DataComponentType<?> , Optional<?>> ent)
    {
        Optional<?> opt = ent.getValue();
        if (opt.isPresent()) {
            SetNewPatchItemIndirection2(builder , ent.getKey() , opt.get());
        }
    }

    @SuppressWarnings("unchecked")
    private static Optional<CompoundTag> FindCustomDataOrEmpty(ItemStack is)
    {
        for (var i : is.getComponentsPatch().entrySet())
        {
            // Always one or no custom data will be defined.
            if (i.getKey() == DataComponents.CUSTOM_DATA)
            {
                // Get the tag
                return (Optional<CompoundTag>) i.getValue();
            }
        }
        // Return an empty one in all other cases.
        return Optional.empty();
    }

    private static ItemStack CreateItemStack(Item item , int count , Optional<CompoundTag> ct , DataComponentPatch p)
    {
        if (ct.isPresent())
        {
            BaseModsLib.LOGGER.warn("""
STABLEITEMSTACK: Due to Minecraft's changes the item stack can no longer accept NBT tags directly.
The mod will do hard work to define your data directly, but you should define a data component patch in place of the tag data.
This field will not be deprecated in the subsequent releases of the mod (except if something in Minecraft changes).""");
            CustomData cd = CustomData.of(ct.get());
            DataComponentPatch.Builder builder = DataComponentPatch.builder();
            builder.set(DataComponents.CUSTOM_DATA , cd);
            if (!p.isEmpty())
            {
                for (var i : p.entrySet())
                {
                    SetNewPatchItem(builder , i);
                }
            }
            return new ItemStack(Holder.direct(item), count, builder.build());
        }
        return new ItemStack(Holder.direct(item) , count , p);
    }
}
