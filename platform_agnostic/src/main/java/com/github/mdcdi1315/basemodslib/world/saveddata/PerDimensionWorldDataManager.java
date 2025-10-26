package com.github.mdcdi1315.basemodslib.world.saveddata;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.function.Supplier;

/**
 * Provides a layering class for getting around the limitations of Minecraft saved data mechanism through {@link SavedDataWrapper} instances. <br />
 * You provide the level you wish to manipulate and all the other things are done through dedicated methods defined in this class.
 */
public final class PerDimensionWorldDataManager
{
    public static final String EXPECTED_SAVED_DATA_PREFIX = "MDCDI1315_SD_";
    private final DimensionDataStorage storage;

    private static void VerifySavedDataPrefix(String name)
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        if (!name.startsWith(EXPECTED_SAVED_DATA_PREFIX)) {
            throw new ArgumentException("For the saved data API to work, the saved data file name must begin with the saved data prefix string." , "name");
        }
    }

    /**
     * Creates a new instance of the {@link PerDimensionWorldDataManager} from the specified {@link DimensionDataStorage} object.
     * @param storage The underlying data storage manager to use.
     * @throws ArgumentNullException {@code storage} was null.
     */
    public PerDimensionWorldDataManager(DimensionDataStorage storage)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(storage , "storage");
        this.storage = storage;
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
        this.storage = level.getDataStorage();
    }

    private record InternalCreater<T extends ISavedData>(Func1<T> actualcreater)
        implements Supplier<SavedDataWrapper<T>>
    {
        @Override
        public SavedDataWrapper<T> get() {
            return new SavedDataWrapper<>(actualcreater.function());
        }
    }

    /**
     * If the specified saved data with the name passed does not exist, a new saved data object is created on the fly. <br />
     * Otherwise, if there is such a file, it returns the loaded data.
     * @param name The name of the saved data file to retrieve or create.
     * @param creater A factory function allowing creating instances of type {@linkplain T}.
     * @return A new {@link SavedDataWrapper} class instance representing the loaded or new data.
     * @param <T> The type of the saved data to retrieve or create.
     * @exception ArgumentNullException {@code creater} and/or {@code name} is {@code null}.
     */
    public <T extends ISavedData> SavedDataWrapper<T> ComputeIfAbsentAsWrapper(String name , Func1<T> creater)
            throws ArgumentNullException
    {
        VerifySavedDataPrefix(name);
        return storage.computeIfAbsent(SavedDataWrapper.CreateLoadFunction(creater), new InternalCreater<>(creater) , name);
    }

    /**
     * Gets the existing saved data instance of the specified name. If such instance does not exist, {@code null} is returned.
     * @param name The name of the saved data file to retrieve.
     * @param creater A factory function allowing creating instances of type {@linkplain T}.
     * @return A new {@link SavedDataWrapper} class instance representing the loaded data.
     * @param <T> The type of the saved data to retrieve.
     * @exception ArgumentNullException {@code creater} was {@code null}.
     */
    @MaybeNull
    public <T extends ISavedData> SavedDataWrapper<T> GetAsWrapper(String name, Func1<T> creater)
            throws ArgumentNullException
    {
        VerifySavedDataPrefix(name);
        return storage.get(SavedDataWrapper.CreateLoadFunction(creater) , name);
    }

    /**
     * Updates saved data, if existing. If not, this is the first time that the data will be added.
     * @param data The saved data wrapper to save.
     * @param name The name of the saved data file to finally store the results to.
     * @param <T> The type of the saved data to set.
     * @throws ArgumentNullException {@code data} was {@code null}.
     */
    public <T extends ISavedData> void SetAsWrapper(SavedDataWrapper<T> data, String name)
        throws ArgumentNullException
    {
        VerifySavedDataPrefix(name);
        ArgumentNullException.ThrowIfNull(data, "data");
        storage.set(name , data);
    }

    /**
     * If the specified saved data with the name passed does not exist, a new saved data object is created on the fly. <br />
     * Otherwise, if there is such a file, it returns the loaded data.
     * @param name The name of the saved data file to retrieve or create.
     * @param creater A factory function allowing creating instances of type {@linkplain T}.
     * @return The saved data requested, cast to type {@linkplain T}.
     * @param <T> The type of the saved data to retrieve or create.
     * @exception ArgumentNullException {@code creater} was {@code null}.
     */
    public <T extends ISavedData> T ComputeIfAbsent(String name , Func1<T> creater) {
        var w = ComputeIfAbsentAsWrapper(name , creater);
        w.MarkAsDirty();
        return w.Instance();
    }

    /**
     * Gets the existing saved data instance of the specified name. If such instance does not exist, {@code null} is returned.
     * @param name The name of the saved data file to retrieve.
     * @param creater A factory function allowing creating instances of type {@linkplain T}.
     * @return The saved data requested, cast to type {@linkplain T}.
     * @param <T> The type of the saved data to retrieve.
     * @exception ArgumentNullException {@code creater} was {@code null}.
     */
    @MaybeNull
    public <T extends ISavedData> T Get(String name , Func1<T> creater) {
        SavedDataWrapper<T> wrapper = GetAsWrapper(name , creater);
        if (wrapper == null) {
            return null;
        } else {
            wrapper.MarkAsDirty();
            return wrapper.Instance();
        }
    }

    /**
     * Updates saved data, if existing. If not, this is the first time that the data will be added.
     * @param instance The saved data instance to save.
     * @param name The name of the saved data file to finally store the results to.
     * @param <T> The type of the saved data to set.
     * @throws ArgumentNullException {@code data} was {@code null}.
     */
    public <T extends ISavedData> void Set(T instance, String name) {
        var w = new SavedDataWrapper<>(instance);
        w.MarkAsDirty();
        storage.set(name , w);
    }
}
