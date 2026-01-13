package com.github.mdcdi1315.basemodslib.world.saveddata;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * Provides a layering class for getting around the limitations of Minecraft saved data mechanism through {@link ISavedData} instances. <br />
 * You provide the level you wish to manipulate and all the other things are done through dedicated methods defined in this class.
 */
public final class PerDimensionWorldDataManager
{
    /**
     * This field was used for BML versions < 1.0.15, and it is now unused.
     */
    @Deprecated(since = "1.0.15")
    public static final String EXPECTED_SAVED_DATA_PREFIX = IBMLCustomDataStorage.COMPAT_EXPECTED_SAVED_DATA_PREFIX;

    private final IBMLCustomDataStorage storage;

    /**
     * Creates a new instance of the {@link PerDimensionWorldDataManager} from the specified {@link DimensionDataStorage} object.
     * @param storage The underlying data storage manager to use.
     * @throws ArgumentNullException {@code storage} was null.
     */
    public PerDimensionWorldDataManager(DimensionDataStorage storage)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(storage , "storage");
        this.storage = (IBMLCustomDataStorage) storage;
    }

    /**
     * Creates a new instance of the {@link PerDimensionWorldDataManager} from the specified dimension level to get the data from.
     * @param level The dimension to get the data storage object to be wrapped around.
     * @throws ArgumentNullException {@code level} was null.
     */
    public PerDimensionWorldDataManager(ServerLevel level)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(level, "level");
        this.storage = (IBMLCustomDataStorage) level.getDataStorage();
    }

    /**
     * This method is deprecated since the 1.0.15 version.
     * Do not use it or call it.
     */
    @Deprecated(since = "1.0.15", forRemoval = true)
    public <T extends ISavedData> SavedDataWrapper<T> ComputeIfAbsentAsWrapper(String name , Func1<T> creater)
            throws NotSupportedException
    {
        throw new NotSupportedException("ComputeIfAbsentAsWrapper is deprecated since 1.0.15 and will be removed in a future release.");
    }

    /**
     * This method is deprecated since the 1.0.15 version.
     * Do not use it or call it.
     */
    @Deprecated(since = "1.0.15", forRemoval = true)
    public <T extends ISavedData> SavedDataWrapper<T> GetAsWrapper(String name, Func1<T> creater)
            throws NotSupportedException
    {
        throw new NotSupportedException("GetAsWrapper is deprecated since 1.0.15 and will be removed in a future release.");
    }

    /**
     * This method is deprecated since the 1.0.15 version.
     * Do not use it or call it.
     */
    @Deprecated(since = "1.0.15", forRemoval = true)
    public <T extends ISavedData> void SetAsWrapper(SavedDataWrapper<T> data, String name)
            throws NotSupportedException
    {
        throw new NotSupportedException("SetAsWrapper is deprecated since 1.0.15 and will be removed in a future release.");
    }

    /**
     * If the specified saved data with the name passed does not exist, a new saved data object is created on the fly. <br />
     * Otherwise, if there is such a file, it returns the loaded data.
     * @param name The name of the saved data file to retrieve or create.
     * @param creator A factory function allowing creating instances of type {@linkplain T}.
     * @return The saved data requested, cast to type {@linkplain T}.
     * @param <T> The type of the saved data to retrieve or create.
     * @exception ArgumentNullException {@code creator} and/or {@code name} are {@code null}.
     */
    public <T extends ISavedData> T ComputeIfAbsent(String name , Func1<T> creator) {
        return (T) storage.MDCDI1315$BML$RegisterSavedData(name, creator);
    }

    /**
     * Gets the existing saved data instance of the specified name. If such instance does not exist, {@code null} is returned.
     * @param name The name of the saved data file to retrieve.
     * @param creator This parameter was used for BML versions < 1.0.15, it now becomes unused and can have any value.
     * @return The saved data requested, cast to type {@linkplain T}.
     * @param <T> The type of the saved data to retrieve.
     * @exception ArgumentNullException {@code name} is {@code null}.
     */
    // Note: Do not delete the 'creator' parameter!!! It is still defined for ABI compatibility.
    @MaybeNull
    public <T extends ISavedData> T Get(String name , @MaybeNull Func1<T> creator)
        throws ArgumentNullException
    {
        return (T) storage.MDCDI1315$BML$RetrieveSavedData(name);
    }

    /**
     * Updates saved data, if existing. If not, this is the first time that the data will be added.
     * @param instance The saved data instance to save.
     * @param name The name of the saved data file to finally store the results to.
     * @param <T> The type of the saved data to set.
     * @throws ArgumentNullException {@code data} was {@code null}.
     */
    public <T extends ISavedData> void Set(T instance, String name) {
        storage.MDCDI1315$BML$RegisterSavedData(name, new ElementSupplier<>(instance));
    }
}