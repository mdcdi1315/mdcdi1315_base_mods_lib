package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

/**
 * Provides I/O interoperation with the Fast Binary Format.
 */
public final class FastBinaryFormatIO
{
    private FastBinaryFormatIO() {}

    /**
     * The I/O header of a Fast Binary Format file. <br />
     * Always written and verified on every load operation.
     */
    public static final String HEADER = "FBF";
    /**
     * The length, in bytes, of the {@link #HEADER} constant.
     * @since 1.0.35
     */
    public static final int HEADER_LENGTH = 3;

    /**
     * Loads a previously serialized object.
     * @param stream The data stream to load the object from
     * @return The object exactly representing the stored data in {@code stream}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @apiNote Since BML 1.0.23, this method does not close the data stream after the FBF data are loaded.
     * It is the caller's responsibility to do that.
     */
    @NotNull
    public static BinaryFormatEntry Load(InputStream stream)
        throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        try (WrappedInputStream s = new WrappedInputStream(stream, false))
        {
            byte[] header = s.readNBytes(HEADER_LENGTH);
            if (!HEADER.equals(new String(header, 0, HEADER_LENGTH, StandardCharsets.US_ASCII))) {
                throw new IOException("Invalid FBF header");
            } else {
                BinaryFormatEntryType t = BinaryFormatEntryType.ReadFrom(s);
                BinaryFormatEntry entry = FastBinaryFormatUtils.ConstructEntryFromType(t);
                entry.ReadFrom(s, t);
                return entry;
            }
        }
    }

    /**
     * Loads a previously serialized object from a network buffer.
     * @param buffer The network buffer to load the object from
     * @return The object exactly representing the stored data in {@code buffer}.
     * @throws DecoderException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     */
    @NotNull
    public static BinaryFormatEntry LoadFromNettyBuffer(ByteBuf buffer)
            throws DecoderException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        try (ByteBufInputStream stream = new ByteBufInputStream(buffer, false)) {
            return Load(stream);
        } catch (IOException ioex) {
            throw new DecoderException("I/O exception occurred.", ioex);
        }
    }

    /**
     * Loads a previously GZIP-compressed serialized object.
     * @param stream The data stream to load the object from
     * @return The object exactly representing the stored data in {@code stream}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @apiNote Since BML 1.0.23, this method does not close the data stream after the FBF data are loaded.
     * It is the caller's responsibility to do that.
     */
    @NotNull
    public static BinaryFormatEntry LoadGZIPCompressed(InputStream stream)
        throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        try (GZIPInputStream gzo = new GZIPInputStream(new WrappedInputStream(stream, false))) { return Load(gzo); }
    }

    /**
     * Loads a previously GZIP-compressed serialized object from a network buffer.
     * @param buffer The network buffer to load the object from
     * @return The object exactly representing the stored data in {@code buffer}.
     * @throws DecoderException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     */
    @NotNull
    public static BinaryFormatEntry LoadGZIPCompressedFromNettyBuffer(ByteBuf buffer)
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        try (ByteBufInputStream stream = new ByteBufInputStream(buffer, false)) {
            return LoadGZIPCompressed(stream);
        } catch (IOException ioex) {
            throw new DecoderException("I/O exception occurred: ", ioex);
        }
    }

    /**
     * Loads a previously serialized object.
     * @param file The file that contains the object to load.
     * @return The object exactly representing the stored data in {@code file}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} is {@code null}.
     */
    @NotNull
    public static BinaryFormatEntry LoadFromFile(File file)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        try (FileInputStream fis = new FileInputStream(file)) { return Load(fis); }
    }

    /**
     * Loads a previously serialized object.
     * @param path The file that contains the object to load.
     * @return The object exactly representing the stored data in {@code path}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code path} is {@code null}.
     */
    @NotNull
    public static BinaryFormatEntry LoadFromFile(Path path)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) { return Load(is); }
    }

    /**
     * Loads a previously GZIP-compressed serialized object.
     * @param file The file that contains the object to load.
     * @return The object exactly representing the stored data in {@code file}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} is {@code null}.
     */
    @NotNull
    public static BinaryFormatEntry LoadGZIPCompressedFile(File file)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        try (FileInputStream fis = new FileInputStream(file)) { return LoadGZIPCompressed(fis); }
    }

    /**
     * Loads a previously GZIP-compressed serialized object.
     * @param path The file that contains the object to load.
     * @return The object exactly representing the stored data in {@code path}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code path} is {@code null}.
     */
    @NotNull
    public static BinaryFormatEntry LoadGZIPCompressedFile(Path path)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) { return LoadGZIPCompressed(is); }
    }

    /**
     * Serializes a Fast Binary Format object.
     * @param stream The stream to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code entry} are {@code null}.
     * @apiNote Since BML 1.0.23, this method does not close the data stream after the FBF data are saved.
     * It is the caller's responsibility to do that.
     */
    public static void Save(OutputStream stream, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(entry, "entry");
        ArgumentNullException.ThrowIfNull(stream, "stream");
        stream.write(HEADER.getBytes(StandardCharsets.US_ASCII));
        entry.WriteTo(new WrappedOutputStream(stream, false));
    }

    /**
     * Serializes a Fast Binary Format object to a network buffer.
     * @param buffer The network buffer to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws EncoderException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code entry} are {@code null}.
     */
    public static void SaveToNettyBuffer(ByteBuf buffer, BinaryFormatEntry entry)
            throws EncoderException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer)) {
            Save(stream, entry);
        } catch (IOException ioex) {
            throw new EncoderException("I/O exception occurred.", ioex);
        }
    }

    /**
     * Serializes a Fast Binary Format object with GZIP compression to a network buffer.
     * @param buffer The network buffer to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws EncoderException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code entry} are {@code null}.
     */
    public static void SaveGZIPCompressedToNettyBuffer(ByteBuf buffer, BinaryFormatEntry entry)
            throws EncoderException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer)) {
            SaveGZIPCompressed(stream, entry);
        } catch (IOException ioex) {
            throw new EncoderException("I/O exception occurred.", ioex);
        }
    }

    /**
     * Serializes a Fast Binary Format object, applying GZIP compression to the result stream.
     * @param stream The stream to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code entry} are {@code null}.
     * @apiNote Since BML 1.0.23, this method does not close the data stream after the FBF data are saved.
     * It is the caller's responsibility to do that.
     */
    public static void SaveGZIPCompressed(OutputStream stream, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        try (var gzo = new GZIPOutputStream(new WrappedOutputStream(stream, false), false)) { Save(gzo, entry); }
    }

    /**
     * Serializes a Fast Binary Format object to the result file.
     * @param file The file to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code file} and/or {@code entry} are {@code null}.
     */
    public static void SaveToFile(File file, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        try (FileOutputStream fos = new FileOutputStream(file)) { Save(fos, entry); }
    }

    /**
     * Serializes a Fast Binary Format object to the result file.
     * @param path The file to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code path} and/or {@code entry} are {@code null}.
     */
    public static void SaveToFile(Path path, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        try (OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) { Save(os, entry); }
    }

    /**
     * Serializes a Fast Binary Format object, applying GZIP compression to the result file.
     * @param file The file to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code entry} are {@code null}.
     */
    public static void SaveToGZIPCompressedFile(File file, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(file, "file");
        try (FileOutputStream fos = new FileOutputStream(file)) { SaveGZIPCompressed(fos, entry); }
    }

    /**
     * Serializes a Fast Binary Format object, applying GZIP compression to the result file.
     * @param path The file to save the serialized result to.
     * @param entry The object to save. Can be any type of object directly supported by the Fast Binary Format.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code path} and/or {@code entry} are {@code null}.
     */
    public static void SaveToGZIPCompressedFile(Path path, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        try (OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) { SaveGZIPCompressed(os, entry); }
    }
}
