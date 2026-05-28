package com.github.mdcdi1315.basemodslib.world.internal;

import com.github.mdcdi1315.DotNetLayer.System.Func1;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.world.NBTUtils;

import com.github.mdcdi1315.basemodslib.world.saveddata.ISavedData;
import com.github.mdcdi1315.basemodslib.world.saveddata.SavedDataCommonHeader;
import com.github.mdcdi1315.basemodslib.world.saveddata.IncorrectSavedDataFormatException;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

/**
 * Internal record class for loading saved data. Do not use by your code.
 */
@ApiStatus.Internal
public record DDS_DataLoader<T extends ISavedData>(Func1<T> sd, File location, boolean old)
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
            BaseModsLib.LOGGER.warn("SD_v2: I/O exception occurred while loading saved data for {}. The default data will be instead loaded.\nException data: {}", location.getName(), e);
            return instance;
        }

        SavedDataCommonHeader header;

        try {
            if (old && ctg.contains("DataVersion")) {
                header = new SavedDataCommonHeader(ctg.getCompound("data"));
            } else {
                header = new SavedDataCommonHeader(ctg);
            }
        } catch (IncorrectSavedDataFormatException ise) {
            BaseModsLib.LOGGER.warn("SD_v2: Incorrect saved data layout found while loading saved data for {}. Exception data: \n{}", location.getName(), ise);
            return instance;
        }

        instance.LoadFrom(header);

        return instance;
    }
}
