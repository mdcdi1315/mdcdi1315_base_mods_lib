package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Function;
import java.util.function.Supplier;

public final class BlocksAndItemsRegistrar
    implements IBlockRegistrar,
        IBlockEntityRegistrar,
        IItemRegistrar
{
    private final DeferredRegister.Blocks BLOCKS_REGISTER;
    private final DeferredRegister.Items ITEMS_REGISTER;
    private final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER;
    private List<Pair<Func1<ItemLike>, CreativeModeTab[]>> tabs_registration;

    public BlocksAndItemsRegistrar(String mod_id)
    {
        ITEMS_REGISTER = DeferredRegister.createItems(mod_id);
        BLOCKS_REGISTER = DeferredRegister.createBlocks(mod_id);
        BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE , mod_id);
        tabs_registration = new List<>();
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
    public void Register(String name, BlockRegistrationInformation info) throws ArgumentNullException {
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
            return BlockEntityType.Builder.of(factory::Create , factory.GetBlocks()).build(null); // dataType is unused.
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

    private void RegisterCreativeModeTabsEvent(BuildCreativeModeTabContentsEvent event)
    {
        var en = tabs_registration.GetEnumerator();
        try {
            var current = event.getTab();
            Pair<Func1<ItemLike> , CreativeModeTab[]> p;
            while (en.MoveNext()) {
                p = en.getCurrent();
                ItemLike item = p.first().get();
                for (CreativeModeTab tab : p.second())
                {
                    if (current == tab) {
                        event.accept(item);
                    }
                }
            }
        } finally {
            en.Dispose();
        }
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        BLOCK_ENTITY_REGISTER.register(bus);
        BLOCKS_REGISTER.register(bus);
        ITEMS_REGISTER.register(bus);
        bus.addListener(this::RegisterCreativeModeTabsEvent);
    }
}
