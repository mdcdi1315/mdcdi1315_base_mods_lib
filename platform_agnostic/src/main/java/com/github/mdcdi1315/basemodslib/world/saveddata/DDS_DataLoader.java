package com.github.mdcdi1315.basemodslib.world.saveddata;

import com.github.mdcdi1315.DotNetLayer.System.Func1;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.world.NBTUtils;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.Optional;
import java.io.IOException;
import java.util.function.Function;

/**
 * Internal record class for loading saved data. Do not use by your code.
 */
@ApiStatus.Internal
public record DDS_DataLoader<T extends ISavedData>(Func1<T> sd, Path location, boolean old)
    implements Function<String, T>
{
    @Override
    public T apply(String s)
    {
        T instance = sd.function();

        CompoundTag ctg;
        try {
            ctg = NBTUtils.LoadNBTFile(location);
        } catch (IOException e) {
            BaseModsLib.LOGGER.warn("SD_v2: I/O exception occurred while loading saved data for {}. The default data will be instead loaded.\nException data: {}", location.toString(), e);
            return instance;
        }

        SavedDataCommonHeader header;

        try {
            Optional<CompoundTag> oct;
            if (old && ctg.contains("DataVersion") && (oct = ctg.getCompound("data")).isPresent()) {
                header = new SavedDataCommonHeader(oct.get());
            } else {
                header = new SavedDataCommonHeader(ctg);
            }
        } catch (IncorrectSavedDataFormatException ise) {
            BaseModsLib.LOGGER.warn("SD_v2: Incorrect saved data layout found while loading saved data for {}. Exception data: \n{}", location.toString(), ise);
            return instance;
        }

        instance.LoadFrom(header);

        return instance;
    }
}
