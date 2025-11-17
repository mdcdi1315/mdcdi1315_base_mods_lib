package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.world.NBTUtils;
import com.github.mdcdi1315.basemodslib.world.saveddata.PerDimensionWorldDataManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.io.*;
import java.nio.file.Path;

@Mixin(DimensionDataStorage.class)
public abstract class DimensionDataStorageMixin
{
    @Invoker("getDataFile")
    protected abstract Path GetDataFile(String name);

    @Inject(method = "readTagFromDisk", at = @At("HEAD"), cancellable = true)
    protected void readTagFromDisk(String filename, DataFixTypes dataFixType, int version, CallbackInfoReturnable<CompoundTag> callback_info) throws IOException
    {
        if (filename.startsWith(PerDimensionWorldDataManager.EXPECTED_SAVED_DATA_PREFIX)) {
            callback_info.setReturnValue(NBTUtils.LoadNBTFile(GetDataFile(filename).toFile()));
        }
    }
}
