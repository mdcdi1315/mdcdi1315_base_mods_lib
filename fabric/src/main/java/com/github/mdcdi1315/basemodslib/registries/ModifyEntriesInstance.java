package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

final class ModifyEntriesInstance
            implements ItemGroupEvents.ModifyEntries
{
    // We can make this final, since we create this class on demand when an item add request is performed.
    private final SingleLinkedList<ItemStack> items;

    public ModifyEntriesInstance() { items = new SingleLinkedList<>(); }

    public void AddItemStack(ItemStack stack) { items.Add(stack); }

    public void AddItem(Item item) { items.Add(new ItemStack(item)); }

    @Override
    public void modifyEntries(FabricItemGroupEntries entries)
    {
        List<ItemStack> mapped = CollectionManipulations.AsJavaList(items);
        entries.getDisplayStacks().addAll(mapped);
        entries.getSearchTabStacks().addAll(mapped);
    }
}