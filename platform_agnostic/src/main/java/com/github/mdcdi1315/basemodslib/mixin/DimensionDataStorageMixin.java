package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.world.NBTUtils;
import com.github.mdcdi1315.basemodslib.world.saveddata.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(DimensionDataStorage.class)
public abstract class DimensionDataStorageMixin
    implements IBMLCustomDataStorage
{
    @Unique
    private ConcurrentHashMap<String, ISavedData> BML$saved_data;

    @Invoker("getDataFile")
    protected abstract Path GetDataFile(String name);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void OnCreate(CallbackInfo ci)
    {
        // Explicitly set our appended field to null.
        // If we have custom saved data we will then assign an object reference to this field.
        BML$saved_data = null;
    }

    @Inject(method = "scheduleSave", at = @At("RETURN"), cancellable = true)
    private void OnSave(CallbackInfoReturnable<CompletableFuture<?>> ci)
    {
        if (BML$saved_data != null) {
            // OK, we just need to dispatch our save routine for ALL the entries.
            ci.setReturnValue(
                    ci.getReturnValue().thenAccept(this::MDCDI1315$BML$RunSaveData)
            );
        }
    }

    @Unique
    private void MDCDI1315$BML$RunSaveData(Object o) {
        BML$saved_data.forEach(this::MDCDI1315$BML$SaveEntry);
    }

    @Unique
    private void MDCDI1315$BML$SaveEntry(String name, ISavedData sd)
    {
        if (!sd.ShouldSave()) { return; }

        File f = GetDataFile(name).toFile();

        SavedDataCommonHeader header;
        try { header = sd.Save(); } catch (Exception ex) {
            BaseModsLib.LOGGER.warn("SD_v2: Cannot create the saved data for data name {}: {}", name, ex);
            return;
        }

        try {
            NBTUtils.SaveNBTFileAsGZip(f, header.GenerateFinalData());
        } catch (IOException e) {
            f.delete();
            BaseModsLib.LOGGER.error("SD_v2: Cannot save saved data for file {}: {}", f.getName(), e);
        }
    }

    @Unique
    @Override
    public ISavedData MDCDI1315$BML$RegisterSavedData(String name, Func1<? extends ISavedData> saved_data)
            throws ArgumentException
    {
        ArgumentNullException.ThrowIfNullOrEmpty(name, "name");
        ArgumentNullException.ThrowIfNull(saved_data, "saved_data");
        if (BML$saved_data == null) { BML$saved_data = new ConcurrentHashMap<>(); }
        return BML$saved_data.computeIfAbsent(name, new DDS_DataLoader<>(saved_data, GetDataFile(name), name.startsWith(IBMLCustomDataStorage.COMPAT_EXPECTED_SAVED_DATA_PREFIX)));
    }

    @Unique
    @Override
    public ISavedData MDCDI1315$BML$RetrieveSavedData(String name)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        return (BML$saved_data == null) ? null : BML$saved_data.get(name);
    }
}
