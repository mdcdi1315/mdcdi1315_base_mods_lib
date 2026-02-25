package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.IFluidRegistrar;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.fluid.FluidRegistrationInformation;
import com.github.mdcdi1315.basemodslib.item.datacomponents.DataComponentTypeRegistrationInformation;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public final class BlocksAndItemsRegistrar
        implements IBlockRegistrar,
        IBlockEntityRegistrar,
        IItemRegistrar,
        IFluidRegistrar
{
    private DeferredRegister.Items ITEMS_REGISTER;
    private DeferredRegister<Fluid> FLUID_REGISTER;
    private DeferredRegister.Blocks BLOCKS_REGISTER;
    private DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER;
    private DeferredRegister.DataComponents DATA_COMPONENT_TYPE_REGISTER;
    private DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS_REGISTER;
    private SingleLinkedList<Pair<ItemLike, CreativeModeTab[]>> tabs_registration;
    private Map<CreativeModeTab, SingleLinkedList<ItemStack>> compiled_item_stacks;
    private Map<CreativeModeTab, SingleLinkedList<Func1<ItemStack>>> additional_creative_mode_tab_stacks;

    public BlocksAndItemsRegistrar(String mod_id)
    {
        ITEMS_REGISTER = DeferredRegister.createItems(mod_id);
        BLOCKS_REGISTER = DeferredRegister.createBlocks(mod_id);
        FLUID_REGISTER = DeferredRegister.create(BuiltInRegistries.FLUID , mod_id);
        BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE , mod_id);
        CREATIVE_MODE_TABS_REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB , mod_id);
        DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE , mod_id);

        compiled_item_stacks = null;
        tabs_registration = new SingleLinkedList<>();
        additional_creative_mode_tab_stacks = new HashMap<>(10);
    }

    @Override
    public void Register(String name, FluidRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        FLUID_REGISTER.register(name, info.fluid_getter());
    }

    private record BlockItemRegisterSupplier(Func3<Block , ResourceLocation, Item> item_func, DeferredBlock<?> block)
            implements Func2<ResourceLocation , Item>
    {
        @Override
        public Item function(ResourceLocation location) {
            return item_func.apply(block.get() , location);
        }
    }

    @Override
    public void Register(String name, BlockRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        var db = BLOCKS_REGISTER.register(name , info.block_getter());

        var item_info = info.item_for_block_getter();

        if (item_info != null) {
            var item = ITEMS_REGISTER.register(name , new BlockItemRegisterSupplier(item_info , db));
            var tabs = info.creative_mode_tabs_for_item();
            if (tabs.length > 0) {
                tabs_registration.Add(new Pair<>(item, tabs));
            }
        }
    }

    private record BlockEntityRegistrySupplier<T extends BlockEntity>(IBlockEntityFactory<T> factory)
            implements Func1<BlockEntityType<T>>
    {
        @Override
        @SuppressWarnings("all")
        public BlockEntityType<T> function() {
            return BlockEntityType.Builder.of(factory::Create , factory.GetBlocks()).build(null); // dataType is unused.
        }
    }

    @Override
    public <T extends BlockEntity> void Register(String name, IBlockEntityFactory<T> factory)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(factory, "factory");

        BLOCK_ENTITY_REGISTER.register(name , new BlockEntityRegistrySupplier<>(factory));
    }

    @Override
    public void Register(String name, ItemRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(info, "factory");

        var ir = ITEMS_REGISTER.register(name, info.item_getter());
        var tabs = info.tabs();
        if (tabs.length > 0) {
            tabs_registration.Add(new Pair<>(ir , tabs));
        }
    }

    @Override
    public <T> void RegisterDataComponentType(String name, DataComponentTypeRegistrationInformation<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        DATA_COMPONENT_TYPE_REGISTER.register(name , info.component_type_provider());
    }

    @Override
    public void RegisterCreativeModeTab(String name, CreativeModeTab tab)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tab, "tab");
        CREATIVE_MODE_TABS_REGISTER.register(name, new ElementSupplier<>(tab));
    }

    @Override
    public void RegisterCreativeModeTabStack(CreativeModeTab tab, Func1<ItemStack> stack)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tab, "tab");
        ArgumentNullException.ThrowIfNull(stack, "stack");

        additional_creative_mode_tab_stacks.computeIfAbsent(tab, BlocksAndItemsRegistrar::ComputeIfAbsentWrapper1).Add(stack);
    }

    private static SingleLinkedList<Func1<ItemStack>> ComputeIfAbsentWrapper1(CreativeModeTab tab) { return new SingleLinkedList<>(); }

    private void RegisterCreativeModeTabsEvent(BuildCreativeModeTabContentsEvent event)
    {
        CreativeModeTab current = event.getTab();

        if (compiled_item_stacks != null) {
            for (var kvp : compiled_item_stacks.entrySet())
            {
                if (kvp.getKey() == current) {
                    IEnumerator<ItemStack> iso = kvp.getValue().GetEnumerator();
                    try {
                        while (iso.MoveNext()) { event.accept(iso.getCurrent()); }
                    } finally {
                        iso.Dispose();
                    }
                    // Do not continue searching if this is the tab we wanted for.
                    break;
                }
            }
        } else if (additional_creative_mode_tab_stacks != null && additional_creative_mode_tab_stacks.size() > 0) {
            compiled_item_stacks = new HashMap<>();
            // The below will run only once.
            CreativeModeTab k;
            SingleLinkedList<Func1<ItemStack>> stacks;
            for (var kvp : additional_creative_mode_tab_stacks.entrySet())
            {
                stacks = kvp.getValue();
                IEnumerator<Func1<ItemStack>> e = stacks.GetEnumerator();
                SingleLinkedList<ItemStack> lst = new SingleLinkedList<>();
                try {
                    if ((k = kvp.getKey()) == current) {
                        // Current key agrees with the creative mode tab we want for - so register the enumerated items to the event as well.
                        ItemStack is;
                        while (e.MoveNext()) { is = e.getCurrent().function(); event.accept(is); lst.Add(is); }
                    } else {
                        while (e.MoveNext()) { lst.Add(e.getCurrent().function()); }
                    }
                } finally {
                    stacks.Clear(); // Clean origin list to minimize mem as possible.
                    e.Dispose();
                }
                // Put only when no exceptions do occur.
                compiled_item_stacks.put(k, lst);
            }
        }
        additional_creative_mode_tab_stacks = null;

        if (tabs_registration == null || tabs_registration.getCount() < 1) {
            tabs_registration = null;
            return;
        }

        var en = tabs_registration.GetEnumerator();
        try {
            Pair<ItemLike , CreativeModeTab[]> p;
            while (en.MoveNext()) {
                p = en.getCurrent();
                ItemLike item = p.first();
                for (CreativeModeTab tab : p.second())
                {
                    if (current == tab) {
                        event.accept(item);
                        break;
                    }
                }
            }
        } finally {
            en.Dispose();
        }
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        ITEMS_REGISTER.register(bus);
        FLUID_REGISTER.register(bus);
        BLOCKS_REGISTER.register(bus);
        BLOCK_ENTITY_REGISTER.register(bus);
        CREATIVE_MODE_TABS_REGISTER.register(bus);
        DATA_COMPONENT_TYPE_REGISTER.register(bus);
        NeoForgeUtils.AddListener(bus, BuildCreativeModeTabContentsEvent.class, this::RegisterCreativeModeTabsEvent);
        DATA_COMPONENT_TYPE_REGISTER = null;
        CREATIVE_MODE_TABS_REGISTER = null;
        BLOCK_ENTITY_REGISTER = null;
        BLOCKS_REGISTER = null;
        FLUID_REGISTER = null;
        ITEMS_REGISTER = null;
    }
}