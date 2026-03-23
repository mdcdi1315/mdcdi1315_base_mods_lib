package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.ForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.IFluidRegistrar;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;
import com.github.mdcdi1315.basemodslib.fluid.FluidRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;
import com.github.mdcdi1315.basemodslib.item.datacomponents.DataComponentTypeRegistrationInformation;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

public final class BlocksAndItemsRegistrar
    implements IBlockRegistrar, IItemRegistrar, IBlockEntityRegistrar, IFluidRegistrar
{
    private String mod_id;
    private DeferredRegister<Item> ITEM_REGISTER;
    private DeferredRegister<Fluid> FLUID_REGISTER;
    private DeferredRegister<Block> BLOCKS_REGISTER;
    private DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_REGISTER;
    private DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTER;
    private DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTER;
    private Map<CreativeModeTab, SingleLinkedListBasedRegister<ItemStack>> compiled_item_stacks;
    private SingleLinkedListBasedRegister<Pair<CreativeModeTab[] , RegistryObject<Item>>> items_on_creative_tabs;
    private Map<CreativeModeTab , SingleLinkedList<Func1<ItemStack>>> additional_creative_mode_tab_stacks;

    public BlocksAndItemsRegistrar(String mod_id) {
        this.mod_id = mod_id;
        compiled_item_stacks = null;
        additional_creative_mode_tab_stacks = new HashMap<>();
        items_on_creative_tabs = new SingleLinkedListBasedRegister<>();
        ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, this.mod_id);
        FLUID_REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS , this.mod_id);
        BLOCKS_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS , this.mod_id);
        CREATIVE_MODE_TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB , this.mod_id);
        DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE , this.mod_id);
        BLOCK_ENTITY_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, this.mod_id);
    }

    @Override
    public <T extends BlockEntity> void Register(String name, IBlockEntityFactory<T> factory) throws ArgumentNullException {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(factory, "factory");
        BLOCK_ENTITY_TYPE_REGISTER.register(name , new BlockEntityRegistrySupplier<>(factory));
    }

    @Override
    public void Register(String name, FluidRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");

        ResourceLocation registry_object_location = ResourceLocation.tryBuild(mod_id, name);

        FLUID_REGISTER.register(name, new FluidRegistrySupplier(info.fluid_getter() , registry_object_location));
    }

    private record BlockEntityRegistrySupplier<T extends BlockEntity>(IBlockEntityFactory<T> factory)
        implements Supplier<BlockEntityType<T>>
    {
        @Override
        @SuppressWarnings("all")
        public BlockEntityType<T> get() {
            return new BlockEntityType<>(factory::Create , Set.of(factory.GetBlocks()));
        }
    }

    private record BlockRegistrySupplier(Func2<ResourceLocation, Block> bs, ResourceLocation loc)
        implements Supplier<Block>
    {
        @Override
        public Block get() { return bs.function(loc); }
    }

    private record FluidRegistrySupplier(Func2<ResourceLocation, Fluid> fc, ResourceLocation location)
        implements Supplier<Fluid>
    {
        @Override
        public Fluid get() { return fc.function(location); }
    }

    private record ItemAsBlockRegistrySupplier(Func3<Block, ResourceLocation, Item> bs, RegistryObject<Block> ro, ResourceLocation location)
        implements Supplier<Item>
    {
        @Override
        public Item get() { return bs.function(ro.get(), location); }
    }

    private record ItemRegistrySupplier(Func2<ResourceLocation, Item> bs, ResourceLocation location)
        implements Supplier<Item>
    {
        @Override
        public Item get() { return bs.function(location); }
    }

    @Override
    public void Register(String name, BlockRegistrationInformation bri)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");

        ResourceLocation registry_object_location = ResourceLocation.tryBuild(mod_id, name);

        var registry_object = BLOCKS_REGISTER.register(name, new BlockRegistrySupplier(bri.block_getter() , registry_object_location));

        var item_for_block_s = bri.item_for_block_getter();

        if (item_for_block_s != null) {
            var ro = ITEM_REGISTER.register(name, new ItemAsBlockRegistrySupplier(item_for_block_s, registry_object, registry_object_location));
            items_on_creative_tabs.Register(new Pair<>(bri.creative_mode_tabs_for_item() , ro));
        }
    }

    @Override
    public void Register(String name, ItemRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(info, "info");

        ResourceLocation registry_object_location = ResourceLocation.tryBuild(mod_id, name);

        var item_object = ITEM_REGISTER.register(name, new ItemRegistrySupplier(info.item_getter(), registry_object_location));

        if (info.tabs().length > 0) {
            items_on_creative_tabs.Register(new Pair<>(info.tabs(), item_object));
        }
    }

    @Override
    public <T> void RegisterDataComponentType(String name, DataComponentTypeRegistrationInformation<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");

        DATA_COMPONENT_TYPE_REGISTER.register(name, info.component_type_provider());
    }


    @Override
    public void RegisterCreativeModeTab(String name, CreativeModeTab tab)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tab, "tab");

        CREATIVE_MODE_TAB_REGISTER.register(name , new ElementSupplier<>(tab));
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

    private void OnCreativeModeTabsRegistering(BuildCreativeModeTabContentsEvent event)
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
                SingleLinkedListBasedRegister<ItemStack> lst = new SingleLinkedListBasedRegister<>();
                try {
                    if ((k = kvp.getKey()) == current) {
                        // Current key agrees with the creative mode tab we want for - so register the enumerated items to the event as well.
                        ItemStack is;
                        while (e.MoveNext()) { is = e.getCurrent().function(); event.accept(is); lst.Register(is); }
                    } else {
                        while (e.MoveNext()) { lst.Register(e.getCurrent().function()); }
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

        if (items_on_creative_tabs == null || !items_on_creative_tabs.HasItems()) {
            items_on_creative_tabs = null;
            return;
        }

        var en = items_on_creative_tabs.GetEnumerator();
        try {
            Pair<CreativeModeTab[], RegistryObject<Item>> p;
            while (en.MoveNext()) {
                p = en.getCurrent();
                for (var i : p.first())
                {
                    if (i == current) {
                        event.accept(p.second());
                        break;
                    }
                }
            }
        } finally {
            en.Dispose();
        }
    }

    public void RegisterToEventBus(IEventBus evb)
    {
        ForgeUtils.DeferredRegister_RegisterIfHasItems(evb, ITEM_REGISTER);
        ForgeUtils.DeferredRegister_RegisterIfHasItems(evb, FLUID_REGISTER);
        ForgeUtils.DeferredRegister_RegisterIfHasItems(evb, BLOCKS_REGISTER);
        ForgeUtils.DeferredRegister_RegisterIfHasItems(evb, BLOCK_ENTITY_TYPE_REGISTER);
        ForgeUtils.DeferredRegister_RegisterIfHasItems(evb, CREATIVE_MODE_TAB_REGISTER);
        ForgeUtils.DeferredRegister_RegisterIfHasItems(evb, DATA_COMPONENT_TYPE_REGISTER);
        ForgeUtils.AddListener(evb, BuildCreativeModeTabContentsEvent.class, this::OnCreativeModeTabsRegistering);
        // Clean up what we can clean.
        mod_id = null;
        ITEM_REGISTER = null;
        FLUID_REGISTER = null;
        BLOCKS_REGISTER = null;
        BLOCK_ENTITY_TYPE_REGISTER = null;
        CREATIVE_MODE_TAB_REGISTER = null;
        DATA_COMPONENT_TYPE_REGISTER = null;
    }
}
