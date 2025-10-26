package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * This is mostly a wrapper around the registry registrar, just is provided for faster development on basic and typical usage scenarios.
 */
public interface IWorldGenRegistrar
{
    <TF extends Feature<?>> void RegisterFeatureType(String name, Func1<TF> feature_type_creator) throws ArgumentNullException;

    <TPM extends PlacementModifierType<?>> void RegisterPlacementModifierType(String name, Func1<TPM> placement_modifier_type_creator) throws ArgumentNullException;

    <T extends PoiType> void RegisterPoiType(String name, Func1<T> poi_type_creator) throws ArgumentNullException;
}
