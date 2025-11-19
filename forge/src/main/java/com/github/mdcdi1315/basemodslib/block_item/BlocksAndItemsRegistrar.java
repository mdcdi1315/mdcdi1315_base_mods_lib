package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.ForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.IFluidRegistrar;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.fluid.FluidRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.registries.Registries;
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
    implements IBlockRegistrar, IItemRegistrar, IBlockEntityRegistrar, IFluidRegistrar
{
    private String mod_id;
    private DeferredRegister<Item> ITEM_REGISTER;
    private DeferredRegister<Fluid> FLUID_REGISTER;
    private DeferredRegister<Block> BLOCKS_REGISTER;
    private DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_REGISTER;
    private DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTER;
    private List<Pair<CreativeModeTab[] , RegistryObject<Item>>> items_on_creative_tabs;

    public BlocksAndItemsRegistrar(String mod_id) {
        this.mod_id = mod_id;
        items_on_creative_tabs = new List<>();
        ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, this.mod_id);
        FLUID_REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS , this.mod_id);
        BLOCKS_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS , this.mod_id);
        CREATIVE_MODE_TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB , this.mod_id);
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

    private record FluidRegistrySupplier(Func2<ResourceLocation, Fluid> fc, ResourceLocation location)
        implements Supplier<Fluid>
    {
        @Override
        public Fluid get() {
            return fc.function(location);
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
            var ro = ITEM_REGISTER.register(name, new ItemAsBlockRegistrySupplier(item_for_block_s, registry_object, registry_object_location));
            items_on_creative_tabs.Add(new Pair<>(bri.creative_mode_tabs_for_item() , ro));
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
            items_on_creative_tabs.Add(new Pair<>(info.tabs(), item_object));
        }
    }

    @Override
    public void RegisterCreativeModeTab(String name, CreativeModeTab tab)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tab, "tab");

        CREATIVE_MODE_TAB_REGISTER.register(name , new ElementSupplier<>(tab));
    }

    private void OnCreativeModeTabsRegistering(BuildCreativeModeTabContentsEvent event)
    {
        if (items_on_creative_tabs == null || items_on_creative_tabs.getCount() < 1) {
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
        ITEM_REGISTER.register(evb);
        FLUID_REGISTER.register(evb);
        BLOCKS_REGISTER.register(evb);
        BLOCK_ENTITY_TYPE_REGISTER.register(evb);
        CREATIVE_MODE_TAB_REGISTER.register(evb);
        items_on_creative_tabs.TrimExcess();
        ForgeUtils.AddListener(evb, BuildCreativeModeTabContentsEvent.class, this::OnCreativeModeTabsRegistering);
        // Clean up what we can clean.
        mod_id = null;
        ITEM_REGISTER = null;
        FLUID_REGISTER = null;
        BLOCKS_REGISTER = null;
        BLOCK_ENTITY_TYPE_REGISTER = null;
        CREATIVE_MODE_TAB_REGISTER = null;
    }
}
