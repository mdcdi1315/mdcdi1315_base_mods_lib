package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Optional;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Provides a derived class of the {@link ItemStackSet} class that is immutable. <br />
 * (That is, the entries can't be added or removed from the set)
 * @since 1.0.21
 */
public final class ImmutableItemStackSet
    extends ItemStackSet
{
    /**
     * Initializes a new instance of the {@link ImmutableItemStackSet} from the specified {@link ItemStackSet} instance. <br />
     * (That is, effectively keeping a snapshot of the passed in set)
     * @param is The {@link ItemStackSet} instance to make a snapshot for it.
     */
    public ImmutableItemStackSet(ItemStackSet is) { super(ConstructItems(is)); }

    /**
     * Initializes a new instance of the {@link ImmutableItemStackSet} from the specified {@link ImmutableSet} instance.
     * @param items the {@link ImmutableSet} to convert to a {@link ImmutableItemStackSet}.
     */
    public ImmutableItemStackSet(ImmutableSet<ItemStack> items) { super(ConstructItems(items)); }

    private static Optional<ResourceKey<Item>> UnwrapResourceKey(Holder<Item> item) {
        return (item.kind() == Holder.Kind.DIRECT) ?
                BuiltInRegistries.ITEM.getResourceKey(item.value()) :
                item.unwrapKey();
    }

    private static ImmutableMap<ResourceKey<Item>, ItemStack> ConstructItems(ImmutableSet<ItemStack> items)
    {
        if (items == null) {
            return null;
        } else {
            Optional<ResourceKey<Item>> rk;
            ImmutableMap.Builder<ResourceKey<Item>, ItemStack> builder = ImmutableMap.builder();

            for (ItemStack item : items)
            {
                rk = UnwrapResourceKey(item.getItemHolder());
                if (rk.isPresent()) { builder.put(rk.get(), item); }
            }

            return builder.build();
        }
    }

    private static ImmutableMap<ResourceKey<Item>, ItemStack> ConstructItems(ItemStackSet set)
    {
        if (set == null) {
            return null;
        } else if (set.items instanceof ImmutableMap<ResourceKey<Item>, ItemStack> m) {
            return m;
        } else {
            return ImmutableMap.copyOf(set.items);
        }
    }

    // Methods that modify the set directly do always return false, to retain immutability.

    @Override
    public boolean add(Item item) { return false; }

    @Override
    public boolean remove(Object o) { return false; }

    @Override
    public boolean add(ItemStack stack) { return false; }

    @Override
    public boolean retainAll(Collection<?> c) { return false; }

    @Override
    public boolean removeAll(Collection<?> c) { return false; }

    @Override
    public boolean addAll(Collection<? extends ItemStack> c) { return false; }

    @Override
    public boolean removeIfItem(Predicate<? super Item> filter) { return false; }

    @Override
    public boolean removeIf(Predicate<? super ItemStack> filter) { return false; }

    @Override
    public boolean add(Item item, int count) throws ArgumentOutOfRangeException { return false; }

    @Override
    public boolean remove(Object o, boolean if_item_stack_remove_count_if_possible) { return false; }
}
