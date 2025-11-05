package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeWorldGenRegistrar
    implements IWorldGenRegistrar
{
    private final DeferredRegister<Feature<?>> FEATURE_REGISTER;
    private final DeferredRegister<PlacementModifierType<?>> FEATURE_PLACEMENT_MOD_REGISTER;
    private final DeferredRegister<PoiType> POI_TYPE_REGISTER;

    public NeoForgeWorldGenRegistrar(String mod_id)
    {
        FEATURE_REGISTER = DeferredRegister.create(BuiltInRegistries.FEATURE , mod_id);
        FEATURE_PLACEMENT_MOD_REGISTER = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, mod_id);
        POI_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE , mod_id);
    }

    @Override
    public <TF extends Feature<?>> void RegisterFeatureType(String name, Func1<TF> feature_type_creator) throws ArgumentNullException {
        FEATURE_REGISTER.register(name, feature_type_creator);
    }

    @Override
    public <TPM extends PlacementModifierType<?>> void RegisterPlacementModifierType(String name, Func1<TPM> placement_modifier_type_creator) throws ArgumentNullException {
        FEATURE_PLACEMENT_MOD_REGISTER.register(name, placement_modifier_type_creator);
    }

    @Override
    public <T extends PoiType> void RegisterPoiType(String name, Func1<T> poi_type_creator) throws ArgumentNullException {
        POI_TYPE_REGISTER.register(name , poi_type_creator);
    }

    public void RegisterToEventBus(IEventBus event_bus)
    {
        FEATURE_REGISTER.register(event_bus);
        POI_TYPE_REGISTER.register(event_bus);
        FEATURE_PLACEMENT_MOD_REGISTER.register(event_bus);
    }
}
