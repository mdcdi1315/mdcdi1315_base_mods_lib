package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.*;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.menu.MenuTypeCreater;
import com.github.mdcdi1315.basemodslib.item.IBlockEntityItem;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.IFluidRegistrar;
import com.github.mdcdi1315.basemodslib.menu.IMenuTypeRegistrar;
import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;
import com.github.mdcdi1315.basemodslib.world.IWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.entity.IEntityTypeRegistrar;
import com.github.mdcdi1315.basemodslib.menu.MenuTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.item.ItemRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityFactory;
import com.github.mdcdi1315.basemodslib.entity.EntityTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;
import com.github.mdcdi1315.basemodslib.fluid.FluidRegistrationInformation;
import com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation;
import com.github.mdcdi1315.basemodslib.client.DynamicItemRendererImplementation;
import com.github.mdcdi1315.basemodslib.entity.sensing.SensorTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.attributes.AttributeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.memory.MemoryModuleTypeRegistrationInfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.MappedRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.WritableRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.*;

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
    private final boolean on_client;
    private HashMap<CreativeModeTab, ArrayList<Item>> modify_entries_register;

    public FabricCommonRegistryItemsRegistrar(String mod_id) {
        this.mod_id = mod_id;
        on_client = BaseModsLib.GetEnvironment() == ModdingEnvironment.CLIENT;
        modify_entries_register = new HashMap<>(2);
    }

    private record ModifyEntriesEventImpl(ArrayList<Item> item_enum)
            implements ItemGroupEvents.ModifyEntries
    {
        public ModifyEntriesEventImpl {
            // Trash unused array elements in the list. This will be possibly accessed many times.
            item_enum.trimToSize();
        }

        @Override
        public void modifyEntries(FabricItemGroupEntries entries)
        {
            var mapped = new DirectlyMappedList<>(item_enum , ItemStack::new);
            entries.getDisplayStacks().addAll(mapped);
            entries.getSearchTabStacks().addAll(mapped);
        }
    }

    private static ArrayList<Item> ComputeIfAbsentWrapper(CreativeModeTab rk) {
        return new ArrayList<>(10);
    }

    // This is executed right after all the blocks, items, block entities and fluids have been registered.
    public void ApplyFabricModifyEntries()
    {
        Optional<ResourceKey<CreativeModeTab>> rk;
        for (var kvp : modify_entries_register.entrySet()) {
            rk = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(kvp.getKey());
            if (rk.isEmpty()) {
                BaseModsLib.LOGGER.warn("Cannot get the resource key for the specified creative mode tab! Lookup failed.\nAll the items specified for this creative mode tab will not be applied.");
                continue;
            }
            ItemGroupEvents.modifyEntriesEvent(rk.get()).register(new ModifyEntriesEventImpl(kvp.getValue()));
        }
        modify_entries_register = null;
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
            Item itm = Registry.register(BuiltInRegistries.ITEM, location, item_func_registration.function(blk, location));

            if (on_client) { RegisterItemRenderer(itm); }

            for (var i : info.creative_mode_tabs_for_item()) {
                // Add the item to be registered to the creative mode tabs.
                modify_entries_register.computeIfAbsent(i , FabricCommonRegistryItemsRegistrar::ComputeIfAbsentWrapper).add(itm);
            }
        }
    }

    @Override
    public void Register(String name, ItemRegistrationInformation info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");

        ResourceLocation location = BuildAndValidateLocation(name);

        Item itm = Registry.register(BuiltInRegistries.ITEM, location, info.item_getter().function(location));

        if (on_client) { RegisterItemRenderer(itm); }

        for (var i : info.tabs()) {
            modify_entries_register.computeIfAbsent(i , FabricCommonRegistryItemsRegistrar::ComputeIfAbsentWrapper).add(itm);
        }
    }

    private static void RegisterItemRenderer(Item item)
    {
        if (item instanceof IBlockEntityItem ibi) {
            BuiltinItemRendererRegistry.INSTANCE.register(item, new DynamicItemRendererImplementation(ibi));
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
}
