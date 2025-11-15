package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.*;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.IFluidRegistrar;
import com.github.mdcdi1315.basemodslib.menu.IMenuTypeRegistrar;
import com.github.mdcdi1315.basemodslib.menu.MenuTypeCreater;
import com.github.mdcdi1315.basemodslib.menu.MenuTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.world.IWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.entity.IEntityTypeRegistrar;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.entity.EntityTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.FluidRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.entity.sensing.SensorTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.attributes.AttributeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.memory.MemoryModuleTypeRegistrationInfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.WritableRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Set;
import java.util.Optional;

public final class FabricCommonRegistryItemsRegistrar
    implements IItemRegistrar,
        IBlockRegistrar,
        IBlockEntityRegistrar,
        IWorldGenRegistrar,
        IRegistryRegistrar,
        IEntityTypeRegistrar,
        IFluidRegistrar,
        IMenuTypeRegistrar
{
    private String mod_id;

    public FabricCommonRegistryItemsRegistrar(String mod_id) {
        this.mod_id = mod_id;
    }

    private ResourceLocation BuildAndValidateLocation(String path)
    {
        ResourceLocation ret = ResourceLocation.tryBuild(mod_id, path);

        if (ret == null) {
            throw new RuntimeException("Could not create the resource location!");
        }

        return ret;
    }

    @Override
    public void Register(String name, BlockRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");

        ArgumentNullException.ThrowIfNull(name, "name");

        ResourceLocation location = BuildAndValidateLocation(name);

        Block blk = Registry.register(BuiltInRegistries.BLOCK, location, info.block_getter().function(location));

        Func3<Block, ResourceLocation, Item> item_func_registration = info.item_for_block_getter();

        if (item_func_registration != null)
        {
            ModifyEntriesEventImpl implementation = new ModifyEntriesEventImpl(
                    Registry.register(BuiltInRegistries.ITEM, location, item_func_registration.function(blk, location))
            );

            Optional<ResourceKey<CreativeModeTab>> rk;

            for (var i : info.creative_mode_tabs_for_item())
            {
                rk = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(i);
                if (rk.isEmpty()) {
                    BaseModsLib.LOGGER.warn("Cannot get the resource key for the creative mode tab! Lookup failed.");
                    continue;
                }
                ItemGroupEvents.modifyEntriesEvent(rk.get()).register(implementation);
            }
        }
    }

    @Override
    public void Register(String name, ItemRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");

        ResourceLocation location = BuildAndValidateLocation(name);

        ModifyEntriesEventImpl implementation = new ModifyEntriesEventImpl(
                Registry.register(BuiltInRegistries.ITEM, location, info.item_getter().function(location))
        );

        Optional<ResourceKey<CreativeModeTab>> rk;

        for (var i : info.tabs())
        {
            rk = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(i);
            if (rk.isEmpty()) {
                BaseModsLib.LOGGER.warn("Cannot get the resource key for the creative mode tab! Lookup failed.");
                continue;
            }
            ItemGroupEvents.modifyEntriesEvent(rk.get()).register(implementation);
        }
    }

    @Override
    public void RegisterCreativeModeTab(String name, CreativeModeTab tab)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tab, "tab");
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB , BuildAndValidateLocation(name), tab);
    }

    @Override
    public <T extends BlockEntity> void Register(String name, IBlockEntityFactory<T> factory)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(factory, "factory");

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BuildAndValidateLocation(name) , new BlockEntityType<>(
                factory::Create,
                Set.of(factory.GetBlocks()),
                null
        ));
    }

    @Override
    public <TF extends Feature<?>> void RegisterFeatureType(String name, Func1<TF> feature_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(feature_type_creator, "feature_type_creator");

        Registry.register(BuiltInRegistries.FEATURE, BuildAndValidateLocation(name), feature_type_creator.function());
    }

    @Override
    public <TPM extends PlacementModifierType<?>> void RegisterPlacementModifierType(String name, Func1<TPM> placement_modifier_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(placement_modifier_type_creator, "placement_modifier_type_creator");

        Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, BuildAndValidateLocation(name), placement_modifier_type_creator.function());
    }

    @Override
    public <T extends PoiType> void RegisterPoiType(String name, Func1<T> poi_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(poi_type_creator, "poi_type_creator");

        Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, BuildAndValidateLocation(name), poi_type_creator.function());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, RegistryObjectSupplier<T> supplier)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(registry, "registry");
        ArgumentNullException.ThrowIfNull(supplier, "supplier");

        ResourceLocation location = BuildAndValidateLocation(name);

        var rg = BuiltInRegistries.REGISTRY.getOptional(registry.location());

        if (rg.isEmpty()) {
            throw new NotSupportedException("Registering objects to a non-existent registry is not allowed!");
        } else {
            Registry.register((Registry<T>) rg.get(), location, supplier.apply(location));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void RegisterRegistry(ResourceKey<Registry<T>> registryResourceKey, Action1<IModLoaderRegistry<T>> on_registry_ready)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(on_registry_ready, "on_registry_ready");
        ArgumentNullException.ThrowIfNull(registryResourceKey, "registryResourceKey");

        Lifecycle lc = Lifecycle.stable();
        MappedRegistry<T> mr = new MappedRegistry<>(registryResourceKey, lc);
        ((WritableRegistry<Registry<T>>)BuiltInRegistries.REGISTRY).register(registryResourceKey, mr, lc);
        on_registry_ready.action(new MinecraftWrappedModLoaderRegistry<>(mr));
    }

    @Override
    public <T> void RegisterDatapackRegistry(ResourceKey<Registry<T>> registry_name, Codec<T> element_codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registry_name, "registry_name");
        ArgumentNullException.ThrowIfNull(element_codec, "element_codec");
        DynamicRegistries.register(registry_name, element_codec);
    }

    @Override
    public <T extends Entity> void RegisterEntity(String name, EntityTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        Registry.register(BuiltInRegistries.ENTITY_TYPE , BuildAndValidateLocation(name) , info.entity_provider().function());
    }

    @Override
    public <T> void RegisterMemoryModuleType(String name, MemoryModuleTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE , BuildAndValidateLocation(name) , new MemoryModuleType<>(info.optional_codec()));
    }

    @Override
    public void RegisterEntityAttribute(String name, AttributeRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        Registry.register(BuiltInRegistries.ATTRIBUTE , BuildAndValidateLocation(name) , info.attribute_getter().function());
    }

    @Override
    public <T extends Sensor<?>> void RegisterSensorType(String name, SensorTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        Registry.register(BuiltInRegistries.SENSOR_TYPE , BuildAndValidateLocation(name) , info.sensor_type_getter().function());
    }

    @Override
    public void Register(String name, FluidRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ResourceLocation location = BuildAndValidateLocation(name);
        Registry.register(BuiltInRegistries.FLUID , location , info.fluid_getter().function(location));
    }

    private record MenuCreaterToMenuSupplier<T extends AbstractContainerMenu>(MenuTypeCreater<T> crt)
            implements MenuType.MenuSupplier<T>
    {
        @Override
        public T create(int i, Inventory inventory) {
            return crt.Create(i , inventory);
        }
    }

    @Override
    public <T extends AbstractContainerMenu> void Register(String name, MenuTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        Registry.register(
                BuiltInRegistries.MENU,
                BuildAndValidateLocation(name) ,
                new MenuType<>(new MenuCreaterToMenuSupplier<>(info.creater()) , info.required_features())
        );
    }

    private record ModifyEntriesEventImpl(Item m_item)
        implements ItemGroupEvents.ModifyEntries
    {
        @Override
        public void modifyEntries(FabricItemGroupEntries entries) {
            ItemStack is = new ItemStack(m_item);
            entries.getDisplayStacks().add(is);
            entries.getSearchTabStacks().add(is);
        }
    }
}
