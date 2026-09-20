package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;

import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.ModifyOutputAll;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStackTemplate;

final class ModifyEntriesInstance
        implements ModifyOutputAll
{
    private final ByRefParameter<SingleLinkedListBasedRegister<ItemStackTemplate>> temp_parameter;
    // We can make this final, since we create this class on demand when an item add request is performed.
    private final Dictionary<CreativeModeTab, SingleLinkedListBasedRegister<ItemStackTemplate>> items;

    public ModifyEntriesInstance()
    {
        this.items = new Dictionary<>();
        this.temp_parameter = new ByRefParameter<>();
    }

    private SingleLinkedListBasedRegister<ItemStackTemplate> GetRegister(CreativeModeTab tab)
    {
        if (!items.TryGetValue(tab, temp_parameter))
        {
            items.Add(tab, temp_parameter.Value = new SingleLinkedListBasedRegister<>());
        }
        return temp_parameter.Value;
    }

    public boolean HasItemsToRegister() { return items.getCount() > 0; }

    public void AddItemStack(CreativeModeTab tab, ItemStackTemplate stack) { GetRegister(tab).Register(stack); }

    public void AddItem(CreativeModeTab tab, Item item) { GetRegister(tab).Register(new ItemStackTemplate(item, 1)); }

    @Override
    public void modifyOutput(CreativeModeTab tab, FabricCreativeModeTabOutput output)
    {
        if (items.TryGetValue(tab, temp_parameter))
        {
            try (var enumerator = temp_parameter.Value.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    output.accept(enumerator.getCurrent().create());
                }
            }
        }
    }
}