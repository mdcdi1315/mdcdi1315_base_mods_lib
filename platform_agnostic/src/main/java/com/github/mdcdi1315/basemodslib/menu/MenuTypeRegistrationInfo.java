package com.github.mdcdi1315.basemodslib.menu;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Provides information for creating a new menu type.
 * @param required_features The set of feature flags required to actually use the created menu type. Most mods would wish to set this as {@link net.minecraft.world.flag.FeatureFlags#DEFAULT_FLAGS}.
 * @param creater A function providing the creation process of an container menu of type {@link TM}.
 * @param <TM> The type of the container menu to be registered.
 */
public record MenuTypeRegistrationInfo<TM extends AbstractContainerMenu>(
    @NotNull FeatureFlagSet required_features,
    @NotNull MenuTypeCreater<TM> creater
) {
    /**
     * Creates a new instance of the {@link MenuTypeRegistrationInfo} class.
     * @param required_features The set of feature flags required to actually use the created menu type.
     * @param creater A function providing the creation process of an container menu of type {@link TM}.
     * @throws ArgumentNullException {@code creater} and/or {@code required_features} were {@code null}.
     */
    public MenuTypeRegistrationInfo {
        ArgumentNullException.ThrowIfNull(creater, "creater");
        ArgumentNullException.ThrowIfNull(required_features, "required_features");
    }
}
