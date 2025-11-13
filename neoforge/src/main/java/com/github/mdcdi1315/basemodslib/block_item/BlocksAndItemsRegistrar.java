package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.fluid.FluidRegistrationInformation;
import com.github.mdcdi1315.basemodslib.fluid.IFluidRegistrar;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.item.datacomponents.DataComponentTypeRegistrationInformation;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class BlocksAndItemsRegistrar
    implements IBlockRegistrar,
        IBlockEntityRegistrar,
        IItemRegistrar,
        IFluidRegistrar
{
    private DeferredRegister.Items ITEMS_REGISTER;
    private DeferredRegister<Fluid> FLUIDS_REGISTER;
    private DeferredRegister.Blocks BLOCKS_REGISTER;
    private DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER;
    private DeferredRegister.DataComponents DATA_COMPONENT_TYPE_REGISTER;
    private DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_REGISTER;
    private List<Pair<Func1<ItemLike>, CreativeModeTab[]>> tabs_registration;

    public BlocksAndItemsRegistrar(String mod_id)
    {
        ITEMS_REGISTER = DeferredRegister.createItems(mod_id);
        BLOCKS_REGISTER = DeferredRegister.createBlocks(mod_id);
        FLUIDS_REGISTER = DeferredRegister.create(BuiltInRegistries.FLUID , mod_id);
        BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE , mod_id);
        CREATIVE_MODE_TAB_REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB , mod_id);
        DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, mod_id);
        tabs_registration = new List<>();
    }

    @Override
    public void Register(String name, FluidRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        FLUIDS_REGISTER.register(name, info.fluid_getter());
    }

    private record BlockItemRegisterSupplier(Func3<Block , ResourceLocation, Item> item_func, DeferredBlock<?> block)
        implements Function<ResourceLocation , Item>
    {
        @Override
        public Item apply(ResourceLocation location) {
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
                tabs_registration.Add(new Pair<>(item::get, tabs));
            }
        }
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

    @Override
    public <T extends BlockEntity> void Register(String name, IBlockEntityFactory<T> factory) throws ArgumentNullException {
        BLOCK_ENTITY_REGISTER.register(name , new BlockEntityRegistrySupplier<>(factory));
    }

    @Override
    public void Register(String name, ItemRegistrationInformation info) throws ArgumentNullException {
        var ir = ITEMS_REGISTER.register(name, info.item_getter());
        var tabs = info.tabs();
        if (tabs.length > 0) {
            tabs_registration.Add(new Pair<>(ir::get , tabs));
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
        CREATIVE_MODE_TAB_REGISTER.register(name , new ElementSupplier<>(tab));
    }

    private void RegisterCreativeModeTabsEvent(BuildCreativeModeTabContentsEvent event)
    {
        if (tabs_registration == null || tabs_registration.getCount() < 1) {
            tabs_registration = null;
            return;
        }

        var en = tabs_registration.GetEnumerator();
        try {
            CreativeModeTab current = event.getTab();
            Pair<Func1<ItemLike> , CreativeModeTab[]> p;
            while (en.MoveNext()) {
                p = en.getCurrent();
                ItemLike item = p.first().get();
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
        BLOCKS_REGISTER.register(bus);
        FLUIDS_REGISTER.register(bus);
        BLOCK_ENTITY_REGISTER.register(bus);
        CREATIVE_MODE_TAB_REGISTER.register(bus);
        DATA_COMPONENT_TYPE_REGISTER.register(bus);
        bus.addListener(this::RegisterCreativeModeTabsEvent);
        DATA_COMPONENT_TYPE_REGISTER = null;
        CREATIVE_MODE_TAB_REGISTER = null;
        BLOCK_ENTITY_REGISTER = null;
        FLUIDS_REGISTER = null;
        BLOCKS_REGISTER = null;
        ITEMS_REGISTER = null;
    }
}
