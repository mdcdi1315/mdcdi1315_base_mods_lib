package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.Func1ToSupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

public final class ForgeWorldGenRegistrar
    implements IWorldGenRegistrar
{
    private DeferredRegister<PoiType> POI_TYPES;
    private DeferredRegister<Feature<?>> FEATURE_TYPES;
    private DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES;

    public ForgeWorldGenRegistrar(String mod_id)
    {
        POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES , mod_id);
        FEATURE_TYPES = DeferredRegister.create(ForgeRegistries.FEATURES , mod_id);
        PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE , mod_id);
    }

    @Override
    public <TF extends Feature<?>> void RegisterFeatureType(String name, Func1<TF> feature_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name , "name");
        ArgumentNullException.ThrowIfNull(feature_type_creator, "feature_type_creator");
        FEATURE_TYPES.register(name, new Func1ToSupplier<>(feature_type_creator));
    }

    @Override
    public <TPM extends PlacementModifierType<?>> void RegisterPlacementModifierType(String name, Func1<TPM> placement_modifier_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name , "name");
        ArgumentNullException.ThrowIfNull(placement_modifier_type_creator, "placement_modifier_type_creator");
        PLACEMENT_MODIFIER_TYPES.register(name , new Func1ToSupplier<>(placement_modifier_type_creator));
    }

    @Override
    public <T extends PoiType> void RegisterPoiType(String name, Func1<T> poi_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name , "name");
        ArgumentNullException.ThrowIfNull(poi_type_creator, "poi_type_creator");
        POI_TYPES.register(name, new Func1ToSupplier<>(poi_type_creator));
    }

    public void RegisterToEventBus(IEventBus mod_bus)
    {
        FEATURE_TYPES.register(mod_bus);
        PLACEMENT_MODIFIER_TYPES.register(mod_bus);
        POI_TYPES.register(mod_bus);
    }
}
