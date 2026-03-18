package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import net.minecraft.nbt.*;

import java.io.*;
import java.util.UUID;
import java.nio.file.*;
import java.util.Optional;

/**
 * Provides utilities for loading .NBT files.
 * @since 1.0.5
 */
// Keep in sync with the DimensionDataStorage class.
public final class NBTUtils
{
    private static final int GZIP_HEADER = 35615;

    // Do not let anyone be able to instantiate this class.
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
    @NotNull
    public static CompoundTag LoadNBTFile(File file)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        try (FileInputStream fis = new FileInputStream(file)) { return LoadNBTFile(fis); }
    }

    /**
     * Loads the specified NBT file.
     * @param path The {@link Path} to load as an NBT file.
     * @return The loaded NBT file, as an instance of the {@link CompoundTag} class.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} is {@code null}.
     * @since 1.0.21
     */
    @NotNull
    public static CompoundTag LoadNBTFile(Path path)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) { return LoadNBTFile(is); }
    }

    /**
     * Loads the specified NBT from the specified data stream.
     * @param stream The {@link InputStream} to load as an NBT data stream.
     * @return The loaded NBT data, as an instance of the {@link CompoundTag} class.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @apiNote The API does not close the provided stream. It is the caller's responsibility to close the stream provided to this method.
     * @since 1.0.22
     */
    @NotNull
    public static CompoundTag LoadNBTFile(InputStream stream)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        CompoundTag compoundtag;
        PushbackInputStream pushbackinputstream = new PushbackInputStream(stream, 2);
        if (IsGZip(pushbackinputstream)) {
            compoundtag = NbtIo.readCompressed(pushbackinputstream, NbtAccounter.unlimitedHeap());
        } else {
            try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) { compoundtag = NbtIo.read(datainputstream); }
        }
        return compoundtag;
    }

    /**
     * Loads the specified NBT from the network.
     * @param buffer The {@link ByteBuf} to load as an NBT file.
     * @return The loaded NBT data, as an instance of the {@link CompoundTag} class.
     * @throws DecoderException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} is {@code null}.
     * @apiNote The API does not release the provided buffer. It is the caller's responsibility to release the buffer provided to this method.
     * @since 1.0.22
     */
    @NotNull
    public static CompoundTag LoadNBTFromNetwork(ByteBuf buffer)
            throws ArgumentNullException, DecoderException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        try (ByteBufInputStream bbis = new ByteBufInputStream(buffer, false)) {
            return LoadNBTFile(bbis);
        } catch (IOException ioexception) {
            throw new DecoderException("I/O exception occurred while loading NBT data.", ioexception);
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
     * Saves the specified NBT data to a new .NBT file, compressed with GZip as well.
     * @param path The {@link Path} to save the data to.
     * @param tag The compound tag representing the data to save.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} and/or {@code tag} are {@code null}.
     * @since 1.0.21
     */
    public static void SaveNBTFileAsGZip(Path path, CompoundTag tag)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tag, "tag");
        ArgumentNullException.ThrowIfNull(path, "path");
        try (OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) { NbtIo.writeCompressed(tag, os); }
    }

    /**
     * Saves the specified NBT data to the specified network buffer, compressed with GZip as well.
     * @param buffer The {@link ByteBuf} to save the data to.
     * @param tag The compound tag representing the data to save.
     * @throws EncoderException An I/O exception was occurred.
     * @throws ArgumentNullException {@code buffer} and/or {@code tag} are {@code null}.
     * @since 1.0.22
     */
    public static void SaveNBTAsGZipToNetwork(ByteBuf buffer, CompoundTag tag)
            throws ArgumentNullException, EncoderException
    {
        ArgumentNullException.ThrowIfNull(tag, "tag");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        try (ByteBufOutputStream bout = new ByteBufOutputStream(buffer)) {
            NbtIo.writeCompressed(tag, bout);
        } catch (IOException ioexception) {
            throw new EncoderException("Can't encode the specified NBT tag due to an I/O error.", ioexception);
        }
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

    /**
     * Saves the specified NBT data to a new .NBT file.
     * @param path The {@link Path} to save the data to.
     * @param tag The compound tag representing the data to save.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} and/or {@code tag} are {@code null}.
     */
    public static void SaveNBTFile(Path path, CompoundTag tag)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(tag, "tag");
        ArgumentNullException.ThrowIfNull(path, "path");
        try (
                OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                DataOutputStream dos = new DataOutputStream(os)
        ) { NbtIo.write(tag, dos); }
    }

    /**
     * Gets a byte field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Byte} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<Byte> GetByte(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof ByteTag btg) ? Optional.of(btg.getAsByte()) : Optional.empty();
    }

    /**
     * Gets a short field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Short} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<Short> GetShort(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof ShortTag stg) ? Optional.of(stg.getAsShort()) : Optional.empty();
    }

    /**
     * Gets an int field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Integer} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<Integer> GetInt(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof IntTag itg) ? Optional.of(itg.getAsInt()) : Optional.empty();
    }

    /**
     * Gets a long field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Long} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<Long> GetLong(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof LongTag ltg) ? Optional.of(ltg.getAsLong()) : Optional.empty();
    }

    /**
     * Gets a float field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Float} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<Float> GetFloat(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof FloatTag ftg) ? Optional.of(ftg.getAsFloat()) : Optional.empty();
    }

    /**
     * Gets a double field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Double} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<Double> GetDouble(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof DoubleTag dtg) ? Optional.of(dtg.getAsDouble()) : Optional.empty();
    }

    /**
     * Gets a boolean field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Boolean} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<Boolean> GetBoolean(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof ByteTag btg) ? Optional.of(btg.getAsInt() != 0) : Optional.empty();
    }

    /**
     * Gets a string field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link String} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<String> GetString(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof StringTag stg) ? Optional.of(stg.getAsString()) : Optional.empty();
    }

    /**
     * Gets a compound tag field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link CompoundTag} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<CompoundTag> GetCompound(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof CompoundTag ctg) ? Optional.of(ctg) : Optional.empty();
    }

    /**
     * Gets an integer array tag field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Integer}[] that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<int[]> GetIntArray(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof IntArrayTag itg) ? Optional.of(itg.getAsIntArray()) : Optional.empty();
    }

    /**
     * Gets a long array tag field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Long}[] that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<long[]> GetLongArray(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof LongArrayTag ltg) ? Optional.of(ltg.getAsLongArray()) : Optional.empty();
    }

    /**
     * Gets a byte array tag field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link Byte}[] that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<byte[]> GetByteArray(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof ByteArrayTag btg) ? Optional.of(btg.getAsByteArray()) : Optional.empty();
    }

    /**
     * Gets a list tag field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link ListTag} that contains the field's value, if that exists.
     * @since 1.0.19
     */
    public static Optional<ListTag> GetListTag(CompoundTag tag, String key)
    {
        return (tag.get(key) instanceof ListTag ltg) ? Optional.of(ltg) : Optional.empty();
    }

    /**
     * Gets a {@link UUID} field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return An {@link Optional} of {@link UUID} that contains the field's value, if that exists.
     * @apiNote To retrieve the UUID value using this method, you must use the {@link #PutUUID(CompoundTag, String, UUID)} method.
     * @since 1.0.19
     */
    public static Optional<UUID> GetUUID(CompoundTag tag, String key)
    {
        if (tag.get(key) instanceof LongArrayTag lat) {
            if (lat.size() == 2) {
                return Optional.of(
                        new UUID(
                                lat.get(0).getAsLong(),
                                lat.get(1).getAsLong()
                        )
                );
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets a custom value field in the current compound tag.
     * @param tag The {@link CompoundTag} to read the field from.
     * @param key The field's name to get its value.
     * @return A {@link DataResult} of {@link T} that contains the field's value, if that exists and is valid.
     * @since 1.0.19
     */
    public static <T> DataResult<T> GetByCodec(CompoundTag tag, String key, Codec<T> codec)
    {
        Tag t = tag.get(key);
        if (t == null) {
            return DataResult.error(StringSupplier.FromDotNetFormatted("No field '{0}' in compound tag", key));
        } else {
            return codec.parse(NbtOps.INSTANCE, t);
        }
    }

    /**
     * Puts a {@link UUID} field in the current {@link CompoundTag}.
     * @param tag The {@link CompoundTag} to store the field to.
     * @param key The field's name.
     * @param uuid The {@link UUID} to be stored.
     * @apiNote You cannot use the {@link CompoundTag#getUUID(String)} to read back the UUID saved by this method. You must instead use the {@link #GetUUID(CompoundTag, String)} method.
     * @since 1.0.19
     */
    public static void PutUUID(CompoundTag tag, String key, UUID uuid)
    {
        tag.putLongArray(key, new long[] { uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() });
    }

    /**
     * Puts a custom value encoded in NBT to the current {@link CompoundTag}.
     * @param tag The {@link CompoundTag} to store the field to.
     * @param key The field's name.
     * @param codec The {@link Codec} to use for encoding {@link T}.
     * @param input The instance to encode to NBT.
     * @param <T> The type of the object to be stored as NBT.
     * @since 1.0.19
     */
    public static <T> void PutByCodec(CompoundTag tag, String key, Codec<T> codec, T input)
    {
        tag.put(key, codec.encode(input, NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).getOrThrow());
    }
}
