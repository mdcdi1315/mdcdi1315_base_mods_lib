package com.github.mdcdi1315.basemodslib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.mdcdi1315.basemodslib.world.saveddata.PerDimensionWorldDataManager;

import java.io.*;

@Mixin(DimensionDataStorage.class)
public abstract class DimensionDataStorageMixin
{
    @Invoker("getDataFile")
    protected abstract File GetDataFile(String name);

    @Invoker("isGzip")
    protected abstract boolean IsGzip(PushbackInputStream inputStream) throws IOException;

    @Inject(method = "readTagFromDisk", at = @At("HEAD"), cancellable = true)
    protected void readTagFromDisk(String name, int levelVersion, CallbackInfoReturnable<CompoundTag> cir) throws IOException
    {
        if (name.startsWith(PerDimensionWorldDataManager.EXPECTED_SAVED_DATA_PREFIX)) {
            cir.setReturnValue(BASEMODSLIB_II_ReadTagFromDisk$1(name));
        }
    }

    @Unique
    private CompoundTag BASEMODSLIB_II_ReadTagFromDisk$1(String name) throws IOException
    {
        File file1 = GetDataFile(name);

        try (
                FileInputStream fileinputstream = new FileInputStream(file1);
                PushbackInputStream pushbackinputstream = new PushbackInputStream(fileinputstream, 2)
        ) {
            CompoundTag compoundtag;
            if (IsGzip(pushbackinputstream)) {
                compoundtag = NbtIo.readCompressed(pushbackinputstream);
            } else {
                try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) {
                    compoundtag = NbtIo.read(datainputstream);
                }
            }
            return compoundtag;
        }
    }
}
