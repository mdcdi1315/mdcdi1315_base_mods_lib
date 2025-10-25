package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

public final class BlocksAndItemsRegistrar
    implements IBlockRegistrar, IItemRegistrar, IBlockEntityRegistrar
{
    private String mod_id;
    private DeferredRegister<Item> ITEM_REGISTER;
    private DeferredRegister<Block> BLOCKS_REGISTER;
    private DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTER;
    private List<Pair<CreativeModeTab[] , RegistryObject<Item>>> items_on_creative_tabs;

    public BlocksAndItemsRegistrar(String mod_id) {
        this.mod_id = mod_id;
        items_on_creative_tabs = new List<>();
        BLOCKS_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS , this.mod_id);
        ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, this.mod_id);
        BLOCK_ENTITY_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, this.mod_id);
    }

    @Override
    public <T extends BlockEntity> void Register(String name, IBlockEntityFactory<T> factory) throws ArgumentNullException {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(factory, "factory");
        BLOCK_ENTITY_TYPE_REGISTER.register(name , new BlockEntityRegistrySupplier<>(factory));
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

    private record BlockRegistrySupplier(Func2<ResourceLocation, Block> bs, ResourceLocation loc)
        implements Supplier<Block>
    {
        @Override
        public Block get() {
            return bs.function(loc);
        }
    }

    private record ItemAsBlockRegistrySupplier(Func3<Block, ResourceLocation, Item> bs, RegistryObject<Block> ro, ResourceLocation location)
        implements Supplier<Item>
    {
        @Override
        public Item get() {
            return bs.function(ro.get(), location);
        }
    }

    private record ItemRegistrySupplier(Func2<ResourceLocation, Item> bs, ResourceLocation location)
        implements Supplier<Item>
    {
        @Override
        public Item get() {
            return bs.function(location);
        }
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
            ITEM_REGISTER.register(name, new ItemAsBlockRegistrySupplier(item_for_block_s, registry_object, registry_object_location));
        }
    }

    @Override
    public void Register(String name, ItemRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");

        ResourceLocation registry_object_location = ResourceLocation.tryBuild(mod_id, name);

        var item_object = ITEM_REGISTER.register(name, new ItemRegistrySupplier(info.item_getter(), registry_object_location));

        if (info.tabs().length > 0) {
            items_on_creative_tabs.Add(new Pair<>(info.tabs(), item_object));
        }
    }

    private void OnCreativeModeTabsRegistering(BuildCreativeModeTabContentsEvent event)
    {
        if (items_on_creative_tabs.getCount() < 1) {
            return;
        }

        var en = items_on_creative_tabs.GetEnumerator();
        try {
            Pair<CreativeModeTab[], RegistryObject<Item>> p;
            while (en.MoveNext()) {
                p = en.getCurrent();
                for (var i : p.first())
                {
                    if (i == event.getTab()) {
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
        BLOCKS_REGISTER.register(evb);
        ITEM_REGISTER.register(evb);
        BLOCK_ENTITY_TYPE_REGISTER.register(evb);
        evb.addListener(this::OnCreativeModeTabsRegistering);
    }
}
