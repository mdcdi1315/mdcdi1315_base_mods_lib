package com.github.mdcdi1315.basemodslib.world.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.world.saveddata.ISavedData;

import org.jetbrains.annotations.ApiStatus;

/**
 * A more modern solution to saved data. <br />
 * This interface is implemented through Mixin, and as such, is subject to change without notice from mdcdi1315. <br />
 * For a public API, use the {@link com.github.mdcdi1315.basemodslib.world.saveddata.PerDimensionWorldDataManager} class.
 * @since 1.0.15
 */
@ApiStatus.Internal
public interface IBMLCustomDataStorage
{
    /**
     * Provided for backwards compatibility for those mods that were using the older system. <br />
     * Mods implemented in the 1.0.15 version and after shall not use this prefix, as they may cause data loading issues.
     */
    String COMPAT_EXPECTED_SAVED_DATA_PREFIX = "MDCDI1315_SD_";

    /**
     * Registers to the pointed-to custom data storage the current saved data instance.
     * @param name The name of the saved data to be registered under.
     * @param saved_data An instance to a method that creates to register.
     * @return If saved data were found under {@code name}, they are returned; otherwise the newly created data are returned.
     * @throws ArgumentException {@code name} is the empty ("") string.
     * @throws ArgumentNullException {@code name} and/or {@code saved_data} are {@code null}.
     */
    @NotNull
    ISavedData MDCDI1315$BML$RegisterSavedData(String name, Func1<? extends ISavedData> saved_data) throws ArgumentException;

    /**
     * Retrieves an {@link ISavedData} instance of the specified name.
     * @param name The name of the saved data instance
     * @return The {@link ISavedData} instance bound to {@code name}.
     * @throws ArgumentNullException {@code name} is {@code null}.
     */
    @MaybeNull
    ISavedData MDCDI1315$BML$RetrieveSavedData(String name) throws ArgumentNullException;
}
