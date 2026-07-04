package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;

import com.google.common.collect.ImmutableMap;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import java.util.Map;
import java.util.HashMap;

// Manages and handles the workflow of item stacks in NeoForge.
// It has multiple abilities, such as adding custom items,
// and can also register custom item stack functions.
// Additionally, it also is an BuildCreativeModeTabContentsEvent
// handler, for reduced mem footprint when the mods do not register
// item stacks for preview.
public final class RegisterCreativeModeTabItemsHandler
    implements Action1<BuildCreativeModeTabContentsEvent>
{
    private RegisterCreator creator_reference;
    private ImmutableMap<CreativeModeTab, SingleLinkedListBasedRegister<ItemStack>> compiled_item_stacks;
    private Map<CreativeModeTab, SingleLinkedListBasedRegister<Func1<ItemStack>>> creative_mode_tab_stacks;

    public RegisterCreativeModeTabItemsHandler()
    {
        compiled_item_stacks = null;
        creator_reference = new RegisterCreator();
        creative_mode_tab_stacks = new HashMap<>();
    }

    private record DeferredItemRegisterSupplier(DeferredItem<?> item)
            implements Func1<ItemStack>
    {
        @Override
        public ItemStack function() { return new ItemStack(item.get(), 1); }
    }

    private record RegisterCreator()
        implements Func2<CreativeModeTab, SingleLinkedListBasedRegister<Func1<ItemStack>>>
    {
        @Override
        public SingleLinkedListBasedRegister<Func1<ItemStack>> function(CreativeModeTab input) {
            return new SingleLinkedListBasedRegister<>();
        }
    }

    public void RegisterDeferredItem(CreativeModeTab[] tabs, DeferredItem<?> item)
    {
        if (item != null && tabs != null && tabs.length > 0)
        {
            DeferredItemRegisterSupplier supplier = new DeferredItemRegisterSupplier(item);
            for (CreativeModeTab tab : tabs)
            {
                creative_mode_tab_stacks.computeIfAbsent(tab, creator_reference).Register(supplier);
            }
        }
    }

    public void RegisterItemStack(CreativeModeTab tab, Func1<ItemStack> stack_function)
    {
        creative_mode_tab_stacks.computeIfAbsent(tab, creator_reference).Register(stack_function);
    }

    @Override
    public void action(BuildCreativeModeTabContentsEvent event)
    {
        CreativeModeTab tab = event.getTab();

        if (compiled_item_stacks != null) {
            // We have compiled all the stacks, so we can freely register the items if we have
            // items for the current creative mode tab.
            try (IEnumerator<ItemStack> en = CollectionManipulations.GetEnumeratorSafe(compiled_item_stacks.get(tab)))
            {
                while (en.MoveNext()) { event.accept(en.getCurrent()); }
            }
        } else if (creative_mode_tab_stacks != null) {
            // We have not compiled the stacks yet, make sure to compile them.
            // Keep a building register reference, and a variable to the compiled item stacks builder.
            SingleLinkedListBasedRegister<ItemStack> building_register;
            ImmutableMap.Builder<CreativeModeTab, SingleLinkedListBasedRegister<ItemStack>> builder = ImmutableMap.builder();

            for (var kvp : creative_mode_tab_stacks.entrySet())
            {
                building_register = new SingleLinkedListBasedRegister<>();
                try (IEnumerator<Func1<ItemStack>> enumerator = kvp.getValue().GetEnumerator())
                {
                    if (kvp.getKey() == tab) {
                        ItemStack stack;
                        while (enumerator.MoveNext())
                        {
                            stack = enumerator.getCurrent().function();
                            event.accept(stack);
                            building_register.Register(stack);
                        }
                    } else {
                        while (enumerator.MoveNext())
                        {
                            building_register.Register(
                                    enumerator.getCurrent().function()
                            );
                        }
                    }
                }
                builder.put(kvp.getKey(), building_register);
            }

            compiled_item_stacks = builder.build();
            creative_mode_tab_stacks = null;
        }
    }

    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public void RegisterToEventBus(IEventBus bus)
    {
        if (creative_mode_tab_stacks != null && creative_mode_tab_stacks.size() > 0)
        {
            NeoForgeUtils.AddListener(bus, BuildCreativeModeTabContentsEvent.class, this);
        }
        creator_reference = null;
    }
}
