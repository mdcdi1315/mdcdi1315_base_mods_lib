package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.world.NBTUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.DimensionDataStorage;

import com.github.mdcdi1315.basemodslib.world.saveddata.PerDimensionWorldDataManager;

import java.io.*;

@Mixin(DimensionDataStorage.class)
public abstract class DimensionDataStorageMixin
{
    @Invoker("getDataFile")
    protected abstract File GetDataFile(String name);

    @Inject(method = "readTagFromDisk", at = @At("HEAD"), cancellable = true)
    private void readTagFromDisk(String name, int levelVersion, CallbackInfoReturnable<CompoundTag> cir) throws IOException
    {
        if (name.startsWith(PerDimensionWorldDataManager.EXPECTED_SAVED_DATA_PREFIX)) {
            cir.setReturnValue(NBTUtils.LoadNBTFile(GetDataFile(name)));
        }
    }
}
