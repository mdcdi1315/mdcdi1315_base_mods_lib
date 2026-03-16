package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.IntFunction;

/**
 * Provides a set of item stacks, supporting many operations and modifications on the set. <br />
 * The set is implemented through a map mapping as keys the resource key of the item and as values their item stack.
 * @since 1.0.21
 */
public class ItemStackSet
    implements Set<ItemStack>
{
    /**
     * Exposes the map implementation under use for extending classes.
     */
    protected final Map<ResourceKey<Item>, ItemStack> items;

    /**
     * Constructs an empty and modifiable {@link ItemStackSet}.
     */
    public ItemStackSet() { items = new HashMap<>(); }

    /**
     * For derived implementations, this overload provides to modify the behavior and semantics of the item stack set.
     * @param items The map implementation to initialize this item stack set from.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    protected ItemStackSet(Map<ResourceKey<Item>, ItemStack> items)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        this.items = items;
    }

    @Override
    public void clear() { items.clear(); }

    @Override
    public int size() { return items.size(); }

    @Override
    public boolean isEmpty() { return items.isEmpty(); }

    @Override
    public @NotNull Object[] toArray() { return items.values().toArray(); }

    @Override
    public @NotNull Iterator<ItemStack> iterator() { return items.values().iterator(); }

    @Override
    public Spliterator<ItemStack> spliterator() { return items.values().spliterator(); }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] a) { return items.values().toArray(a); }

    @Override
    public <T> T[] toArray(@NotNull IntFunction<T[]> generator) { return items.values().toArray(generator); }

    @Override
    public boolean removeIf(@NotNull Predicate<? super ItemStack> filter) { return items.values().removeIf(filter); }

    /**
     * Similar to the {@link #removeIf(Predicate)} method, this method removes all the items from the set if they do match the specified item predicate.
     * @param filter The item predicate to match.
     * @return {@code true} if any elements were removed.
     */
    public boolean removeIfItem(@NotNull Predicate<? super Item> filter)
    {
        boolean at_least_one = false;
        Iterator<ItemStack> it = items.values().iterator();
        while (it.hasNext())  {
            ItemStack item = it.next();
            if (filter.test(item.getItem())) { it.remove(); at_least_one = true; }
        }
        return at_least_one;
    }

    private static Optional<ResourceKey<Item>> UnwrapResourceKey(Holder<Item> item) {
        return (item.kind() == Holder.Kind.DIRECT) ?
                BuiltInRegistries.ITEM.getResourceKey(item.value()) :
                item.unwrapKey();
    }

    /**
     * Finds out whether the given object is contained in this item stack set. <br />
     * Due to the nature of items, deriving classes that support this method MUST support the following cases:
     * <li>If the input parameter is of type {@link ItemStack}, comparison is done based on the item held by the given stack.</li>
     * <li>If the input parameter is of type {@link Item}, comparison is done based on that item's resource key.</li>
     * <li>If the input parameter is of type {@link ResourceKey}, comparison is done based by that key.</li>
     * <li>If the input parameter is of type {@link ResourceLocation}, comparison is done based by the constructed item resource key.</li>
     * @param o The object whose presence in this set is to be tested
     * @return A value whether {@code o} is contained in this set.
     */
    @Override
    public boolean contains(Object o)
    {
        boolean item_stack = false;
        Optional<ResourceKey<Item>> k;
        switch (o) {
            case ItemStack i -> { item_stack = true; k = UnwrapResourceKey(i.getItemHolder()); }
            case Item i -> k = BuiltInRegistries.ITEM.getResourceKey(i);
            case ResourceKey<?> rk -> k = (rk.isFor(Registries.ITEM)) ? Optional.of((ResourceKey<Item>) rk) : Optional.empty();
            case ResourceLocation location -> k = Optional.of(ResourceKey.create(Registries.ITEM, location));
            default -> k = Optional.empty();
        }
        if (k.isPresent()) {
            if (item_stack) {
                ItemStack i = items.get(k.get());
                return i != null && i.getCount() == ((ItemStack)o).getCount();
            } else {
                return items.containsKey(k.get());
            }
        } else {
            return false;
        }
    }

    /**
     * Adds the specified item stack to the set. <br />
     * If {@code stack} is {@code null} or is {@link ItemStack#EMPTY}, the stack is not added.
     * @param stack The item stack to add to this set.
     * @return A value whether the item stack was added to the set. This does also return {@code false} if the item
     * represented by the passed in item stack does already exist.
     */
    @Override
    public boolean add(ItemStack stack)
    {
        if (stack == null || stack == ItemStack.EMPTY) {
            return false;
        } else {
            Optional<ResourceKey<Item>> rk = UnwrapResourceKey(stack.getItemHolder());
            return rk.isPresent() && items.putIfAbsent(rk.get(), stack) == null;
        }
    }

    /**
     * Adds the specified item to the set. <br />
     * If the specified item was added, an item stack for it is created with count 1.
     * @param item The item to add as an item stack of count 1 to this set.
     * @return A value whether the item stack was added to the set. This does also return {@code false} if the item does already exist in the set.
     */
    public boolean add(Item item)
    {
        Optional<ResourceKey<Item>> rk = BuiltInRegistries.ITEM.getResourceKey(item);
        return rk.isPresent() && items.putIfAbsent(rk.get(), new ItemStack(item, 1)) == null;
    }

    /**
     * Adds the specified item to the set. <br />
     * If the specified item was added, an item stack for it is created with the count specified in the {@code count} parameter.
     * @param item The item to add as an item stack of count {@code count} to this set.
     * @param count The desired number of copies of the item to append, if the item does not exist.
     * @return A value whether the item stack was added to the set. This does also return {@code false} if the item does already exist in the set.
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     */
    public boolean add(Item item, int count)
            throws ArgumentOutOfRangeException
    {
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be negative!!");
        } else {
            Optional<ResourceKey<Item>> rk = BuiltInRegistries.ITEM.getResourceKey(item);
            return rk.isPresent() && items.putIfAbsent(rk.get(), new ItemStack(item, count)) == null;
        }
    }


    @Override
    public boolean remove(Object o) { return remove(o, false); }

    /**
     * Removes the specified object from the set. <br />
     * Due to the nature of items, deriving classes that support this method MUST support the following cases:
     * <li>
     * If the input parameter is of type {@link ItemStack} and is found in this set, there are two cases: <br />
     * If {@code if_item_stack_remove_count_if_possible} parameter is {@code true}, then the method
     * tries to reduce the number of copies in the saved item stack.
     * If the number of copies becomes zero or negative, the saved item stack is removed from the set. <br />
     * Otherwise, the saved item stack is removed from the set, regardless of the number of copies that that item stack held. <br />
     * Note that in either value of the parameter, the method will return {@code true}.
     * </li>
     * <li>If the input parameter is of type {@link Item}, lookup for removal is done based on that item's resource key.</li>
     * <li>If the input parameter is of type {@link ResourceKey}, lookup for removal is done based by that key.</li>
     * <li>If the input parameter is of type {@link ResourceLocation}, lookup for removal is done based by the constructed item resource key.</li>
     * @param o The object which is the subject to be removed from the set
     * @param if_item_stack_remove_count_if_possible Value whether to try to reduce the number of copies instead of rather removing the item stack directly.
     * @return A value whether {@code o} is contained in this set, and it was removed according to the above rules.
     */
    public boolean remove(Object o, boolean if_item_stack_remove_count_if_possible)
    {
        boolean item_stack = false;
        Optional<ResourceKey<Item>> k;
        if (o instanceof ItemStack i) {
            item_stack = if_item_stack_remove_count_if_possible;
            k = UnwrapResourceKey(i.getItemHolder());
        } else if (o instanceof Item i) {
            k = BuiltInRegistries.ITEM.getResourceKey(i);
        } else if (o instanceof ResourceKey<?> rk) {
            if (rk.isFor(Registries.ITEM)) {
                k = Optional.of((ResourceKey<Item>) rk);
            } else {
                k = Optional.empty();
            }
        } else if (o instanceof ResourceLocation location) {
            k = Optional.of(ResourceKey.create(Registries.ITEM, location));
        } else {
            k = Optional.empty();
        }
        if (k.isPresent()) {
            if (item_stack) {
                ItemStack i = items.get(k.get());
                if (i == null) {
                    return false;
                } else {
                    int new_c = i.getCount() - ((ItemStack)o).getCount();
                    if (new_c < 1) {
                        return items.remove(k.get()) != null;
                    } else {
                        i.setCount(new_c);
                        return true;
                    }
                }
            } else {
                return items.remove(k.get()) != null;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c)
    {
        boolean all = true;
        for (Object o : c) { if (!contains(o)) { all = false; break; } }
        return all;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends ItemStack> c)
    {
        boolean modified = false;
        Optional<ResourceKey<Item>> rk;
        for (ItemStack stack : c) {
            // for each element, we need to unwrap its resource key
            rk = UnwrapResourceKey(stack.getItemHolder());
            // then add it if present
            if (rk.isPresent() && items.putIfAbsent(rk.get(), stack) != null) { modified = true; }
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c)
    {
        if (c instanceof ItemStackSet other) {
            boolean modified = false;
            for (ResourceKey<Item> k : items.keySet()) {
                if (!other.items.containsKey(k) && items.remove(k) != null) { modified = true; }
            }

            return modified;
        } else {
            return false;
        }
    }

    private static final class RemoveAllImpl
        implements Action2<ResourceKey<Item>, ItemStack>
    {
        public boolean modified;
        private final ItemStackSet this_set;

        public RemoveAllImpl(ItemStackSet this_set) { this.this_set = this_set; modified = false; }

        @Override
        public void action(ResourceKey<Item> obj1, ItemStack obj2) {
            if (this_set.items.remove(obj1) != null) { modified = true; }
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c)
    {
        if (c instanceof ItemStackSet other) {
            RemoveAllImpl method = new RemoveAllImpl(this);
            other.items.forEach(method);
            return method.modified;
        } else {
            return false;
        }
    }

    @Override
    public void forEach(Consumer<? super ItemStack> action)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(action, "action");
        for (ItemStack stack : items.values()) { action.accept(stack); }
    }
}
