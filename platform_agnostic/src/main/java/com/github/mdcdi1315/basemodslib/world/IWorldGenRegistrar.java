package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.Contract;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * This is mostly a wrapper around the registry registrar, just is provided for faster development on basic and typical usage scenarios.
 */
@Contract
public interface IWorldGenRegistrar
{
    /**
     * Registers a new feature type to Minecraft.
     * @param name The name of the newly created feature type that will be registered.
     * @param feature_type_creator The function that, upon invoking, it returns the feature type to register.
     * @param <TF> The exact type of the feature.
     * @throws ArgumentNullException {@code feature_type_creator} is {@code null}.
     */
    <TF extends Feature<?>> void RegisterFeatureType(@ConstantExpected String name, Func1<TF> feature_type_creator) throws ArgumentNullException;

    /**
     * Registers a new placement modifier type to Minecraft.
     * @param name The name of the newly created placement modifier type that will be registered.
     * @param placement_modifier_type_creator The function that, upon invoking, it returns the placement modifier type to register.
     * @param <TPM> The exact type of the placement modifier.
     * @throws ArgumentNullException {@code placement_modifier_type_creator} is {@code null}.
     */
    <TPM extends PlacementModifierType<?>> void RegisterPlacementModifierType(@ConstantExpected String name, Func1<TPM> placement_modifier_type_creator) throws ArgumentNullException;

    /**
     * Registers a new Point Of Interest type to Minecraft.
     * @param name The name of the newly created Point Of Interest type that will be registered.
     * @param poi_type_creator The function that, upon invoking, it returns the Point Of Interest type to register.
     * @param <T> The exact type of the Point Of Interest.
     * @throws ArgumentNullException {@code poi_type_creator} is {@code null}.
     */
    <T extends PoiType> void RegisterPoiType(@ConstantExpected String name, Func1<T> poi_type_creator) throws ArgumentNullException;
}
