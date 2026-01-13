package com.github.mdcdi1315.basemodslib.world.saveddata;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Defines the compatibility layer between {@link ISavedData} instances and {@link SavedData} instances.
 * @param <TD> The type of the {@link ISavedData} object to hold.
 * @deprecated This class is deprecated since 1.0.15, and it is unusable.
 */
@Deprecated(since = "1.0.15", forRemoval = true)
public final class SavedDataWrapper<TD extends ISavedData>
    extends SavedData
{
    private final TD instance;

    public SavedDataWrapper(TD instance)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance , "instance");
        this.instance = instance;
    }

    public static <T extends ISavedData> Codec<SavedDataWrapper<T>> GetCodec(Func1<T> factory)
    {
        return SavedDataCommonHeader.GetCodec().flatXmap(
                new Codec_DataLoader<>(factory),
                (SavedDataWrapper<T> wrap) -> DataResult.success(wrap.instance.Save())
        );
    }

    private record Codec_DataLoader<T extends ISavedData>(Func1<T> factory)
            implements Function<SavedDataCommonHeader , DataResult<SavedDataWrapper<T>>>
    {
        @Override
        public DataResult<SavedDataWrapper<T>> apply(SavedDataCommonHeader h) {
            T inst = factory.function();
            try {
                inst.LoadFrom(h);
            } catch (Exception e) {
                BaseModsLib.LOGGER.warn("SAVEDDATAWRAPPING: Cannot load saved data for class type {} due to an exception.\nEmpty data will be used instead for this instance.\nException details: {}" , inst.getClass().getName(), e);
            }
            return DataResult.success(new SavedDataWrapper<>(inst));
        }
    }

    /**
     * Creates a factory load function. Useful for 1.20.1 Minecraft saved data usages.
     * @param newobject The function that is used as the factory for creating saved data wrappers of the returned object.
     * @return A new function implementation for loading {@link SavedDataWrapper} instances of your choice.
     * @param <T> The type of the saved data to load.
     * @exception ArgumentNullException {@code newobject} was {@code null}.
     */
    public static <T extends ISavedData> BiFunction<CompoundTag , HolderLookup.Provider , SavedDataWrapper<T>> CreateLoadFunction(Func1<T> newobject)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(newobject , "newobject");
        return new FunctionDataLoader<>(newobject);
    }

    private record FunctionDataLoader<T extends ISavedData>(Func1<T> creater)
            implements BiFunction<CompoundTag , HolderLookup.Provider , SavedDataWrapper<T>>
    {
        @Override
        public SavedDataWrapper<T> apply(CompoundTag compoundTag , HolderLookup.Provider p) {
            T sd = creater.function();
            try {
                sd.LoadFrom(new SavedDataCommonHeader(compoundTag));
            } catch (Exception e) {
                BaseModsLib.LOGGER.warn("SAVEDDATAWRAPPING: Cannot load saved data for class type {} due to an exception.\nEmpty data will be used instead for this instance.\nException details: {}" , sd.getClass().getName(), e);
            }
            return new SavedDataWrapper<>(sd);
        }
    }


    /**
     * Retrieves the instance associated with this wrapper object.
     * @return The instance object to inspect.
     */
    @NotNull
    public TD Instance() {
        return instance;
    }

    /**
     * Marks this instance as dirty, which means that Minecraft must later save the instance's data.
     */
    public void MarkAsDirty() {
        setDirty(true);
    }

    @NotNull
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        try {
            return instance.Save().GenerateFinalData();
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("SAVEDDATAWRAPPING: Cannot save saved data for class type {} due to an exception.\nThe last saved data will be erased.\nException details: {}" , instance.getClass() , e);
            return SavedDataCommonHeader.CreateHeader(SavedDataCommonHeader.INVALID_VERSION, new CompoundTag()).GenerateFinalData();
        }
    }
}
