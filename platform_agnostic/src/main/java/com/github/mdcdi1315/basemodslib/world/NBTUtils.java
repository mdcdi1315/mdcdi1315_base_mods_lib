package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;

import java.io.*;

/**
 * Provides utilities for loading .NBT files.
 * @since 1.0.5
 */
// Keep in sync with the DimensionDataStorage class.
public final class NBTUtils
{
    private static final int GZIP_HEADER = 35615;

    private NBTUtils() {}

    private static boolean IsGZip(PushbackInputStream inputStream)
            throws IOException
    {
        byte[] header = new byte[2];
        boolean flag = false;
        int read = inputStream.read(header, 0, 2);
        if (read == 2) {
            if (((header[1] & 255) << 8 | header[0] & 255) == GZIP_HEADER) {
                flag = true;
            }
        }

        if (read != 0) {
            inputStream.unread(header, 0, read);
        }

        return flag;
    }

    /**
     * Loads the specified NBT file.
     * @param file The {@link File} to load as an NBT file.
     * @return The loaded NBT file, as an instance of the {@link CompoundTag} class.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} is {@code null}.
     */
    public static CompoundTag LoadNBTFile(File file)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        try (
                FileInputStream fileinputstream = new FileInputStream(file);
                PushbackInputStream pushbackinputstream = new PushbackInputStream(fileinputstream, 2)
        ) {
            CompoundTag compoundtag;
            if (IsGZip(pushbackinputstream)) {
                compoundtag = NbtIo.readCompressed(pushbackinputstream, NbtAccounter.unlimitedHeap());
            } else {
                try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) {
                    compoundtag = NbtIo.read(datainputstream);
                }
            }
            return compoundtag;
        }
    }

    /**
     * Saves the specified NBT data to a new .NBT file, compressed with GZip as well.
     * @param file The {@link File} to save the data to.
     * @param tag The compound tag representing the data to save.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} and/or {@code tag} are {@code null}.
     */
    public static void SaveNBTFileAsGZip(File file, CompoundTag tag)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tag, "tag");
        ArgumentNullException.ThrowIfNull(file, "file");
        try (FileOutputStream fos = new FileOutputStream(file)) { NbtIo.writeCompressed(tag, fos); }
    }

    /**
     * Saves the specified NBT data to a new .NBT file.
     * @param file The {@link File} to save the data to.
     * @param tag The compound tag representing the data to save.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} and/or {@code tag} are {@code null}.
     */
    public static void SaveNBTFile(File file, CompoundTag tag)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tag, "tag");
        ArgumentNullException.ThrowIfNull(file, "file");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                DataOutputStream dos = new DataOutputStream(fos)
        ) { NbtIo.write(tag, dos); }
    }
}
