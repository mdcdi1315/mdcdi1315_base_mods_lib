package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeWorldGenRegistrar
    implements IWorldGenRegistrar
{
    private DeferredRegister<Feature<?>> FEATURE_REGISTER;
    private DeferredRegister<PlacementModifierType<?>> FEATURE_PLACEMENT_MOD_REGISTER;
    private DeferredRegister<PoiType> POI_TYPE_REGISTER;

    public NeoForgeWorldGenRegistrar(String mod_id)
    {
        FEATURE_REGISTER = DeferredRegister.create(BuiltInRegistries.FEATURE , mod_id);
        FEATURE_PLACEMENT_MOD_REGISTER = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, mod_id);
        POI_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE , mod_id);
    }

    @Override
    public <TF extends Feature<?>> void RegisterFeatureType(String name, Func1<TF> feature_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(feature_type_creator, "feature_type_creator");
        FEATURE_REGISTER.register(name, feature_type_creator);
    }

    @Override
    public <TPM extends PlacementModifierType<?>> void RegisterPlacementModifierType(String name, Func1<TPM> placement_modifier_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(placement_modifier_type_creator, "placement_modifier_type_creator");
        FEATURE_PLACEMENT_MOD_REGISTER.register(name, placement_modifier_type_creator);
    }

    @Override
    public <T extends PoiType> void RegisterPoiType(String name, Func1<T> poi_type_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(poi_type_creator, "poi_type_creator");
        POI_TYPE_REGISTER.register(name , poi_type_creator);
    }

    public void RegisterToEventBus(IEventBus event_bus)
    {
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(event_bus, FEATURE_REGISTER);
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(event_bus, POI_TYPE_REGISTER);
        NeoForgeUtils.DeferredRegister_RegisterIfHasItems(event_bus, FEATURE_PLACEMENT_MOD_REGISTER);
        FEATURE_REGISTER = null;
        POI_TYPE_REGISTER = null;
        FEATURE_PLACEMENT_MOD_REGISTER = null;
    }
}
