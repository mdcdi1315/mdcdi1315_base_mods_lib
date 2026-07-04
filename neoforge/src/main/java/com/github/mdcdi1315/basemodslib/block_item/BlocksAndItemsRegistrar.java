package com.github.mdcdi1315.basemodslib.block_item;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.IFluidRegistrar;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.fluid.FluidRegistrationInformation;
import com.github.mdcdi1315.basemodslib.item.datacomponents.DataComponentTypeRegistrationInformation;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    private RegisterCreativeModeTabItemsHandler creative_mode_tab_items_handler;

    public BlocksAndItemsRegistrar(String mod_id)
    {
        ITEMS_REGISTER = DeferredRegister.createItems(mod_id);
        BLOCKS_REGISTER = DeferredRegister.createBlocks(mod_id);
        FLUID_REGISTER = DeferredRegister.create(BuiltInRegistries.FLUID , mod_id);
        BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE , mod_id);
        CREATIVE_MODE_TABS_REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB , mod_id);
        DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE , mod_id);

        creative_mode_tab_items_handler = new RegisterCreativeModeTabItemsHandler();
    }

    private record BlockItemRegisterSupplier(Func3<Block , ResourceLocation, Item> item_func, DeferredBlock<?> block)
            implements Func2<ResourceLocation , Item>
    {
        @Override
        public Item function(ResourceLocation location) { return item_func.apply(block.get() , location); }
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
    public void Register(String name, BlockRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(info, "info");

        var db = BLOCKS_REGISTER.register(name, info.block_getter());

        var item_info = info.item_for_block_getter();

        if (item_info != null)
        {
            creative_mode_tab_items_handler.RegisterDeferredItem(
                    info.creative_mode_tabs_for_item(),
                    ITEMS_REGISTER.register(name , new BlockItemRegisterSupplier(item_info , db))
            );
        }
    }

    @Override
    public void Register(String name, FluidRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(info, "info");
        FLUID_REGISTER.register(name, info.fluid_getter());
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

        creative_mode_tab_items_handler.RegisterDeferredItem(
                info.tabs(),
                ITEMS_REGISTER.register(name, info.item_getter())
        );
    }

    @Override
    public <T> void RegisterDataComponentType(String name, DataComponentTypeRegistrationInformation<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(info, "info");
        DATA_COMPONENT_TYPE_REGISTER.register(name , info.component_type_provider());
    }

    @Override
    public void RegisterCreativeModeTab(String name, CreativeModeTab tab)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(tab, "tab");
        CREATIVE_MODE_TABS_REGISTER.register(name, new ElementSupplier<>(tab));
    }

    @Override
    public void RegisterCreativeModeTabStack(CreativeModeTab tab, Func1<ItemStack> stack)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tab, "tab");
        ArgumentNullException.ThrowIfNull(stack, "stack");

        creative_mode_tab_items_handler.RegisterItemStack(tab, stack);
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(bus, ITEMS_REGISTER);
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(bus, FLUID_REGISTER);
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(bus, BLOCKS_REGISTER);
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(bus, BLOCK_ENTITY_REGISTER);
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(bus, CREATIVE_MODE_TABS_REGISTER);
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(bus, DATA_COMPONENT_TYPE_REGISTER);
        creative_mode_tab_items_handler.RegisterToEventBus(bus);
        creative_mode_tab_items_handler = null;
        DATA_COMPONENT_TYPE_REGISTER = null;
        CREATIVE_MODE_TABS_REGISTER = null;
        BLOCK_ENTITY_REGISTER = null;
        BLOCKS_REGISTER = null;
        FLUID_REGISTER = null;
        ITEMS_REGISTER = null;
    }
}