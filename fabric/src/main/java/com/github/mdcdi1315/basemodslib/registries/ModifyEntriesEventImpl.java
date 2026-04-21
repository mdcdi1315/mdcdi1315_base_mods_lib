package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

record ModifyEntriesEventImpl(ArrayList<Item> item_enum, @MaybeNull ArrayList<ItemStack> item_stack_enum)
            implements ItemGroupEvents.ModifyEntries
{
    public ModifyEntriesEventImpl {
        // Trash unused array elements in the list. This will be possibly accessed many times.
        item_enum.trimToSize();
        if (item_stack_enum != null) {
            item_stack_enum.trimToSize();
        }
    }

    @Override
    public void modifyEntries(FabricItemGroupEntries entries)
    {
        var ds = entries.getDisplayStacks();
        var sts = entries.getSearchTabStacks();
        var mapped = new DirectlyMappedList<>(item_enum , ItemStack::new);
        ds.addAll(mapped);
        sts.addAll(mapped);
        if (item_stack_enum != null) {
            ds.addAll(item_stack_enum);
            sts.addAll(item_stack_enum);
        }
    }
}