package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

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
     * Loads a previously serialized object.
     * @param stream The data stream to load the object from
     * @return The object exactly representing the stored data in {@code stream}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     */
    @NotNull
    public static BinaryFormatEntry Load(InputStream stream)
        throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        PushbackInputStream strm = new PushbackInputStream(stream, 1);
        byte[] header = new byte[HEADER.length()];
        int read = strm.read(header);
        if (read < header.length || !HEADER.equals(new String(header, 0, read, StandardCharsets.US_ASCII))) {
            throw new IOException("Invalid FBF header");
        } else {
            BinaryFormatEntryType t = BinaryFormatEntryType.ReadFrom(strm);
            BinaryFormatEntry entry = switch (t.GetEntryCode()) {
                case BinaryFormatEntryType.NULL_ENTRY_CODE -> NullBinaryFormatEntry.INSTANCE;
                case BinaryFormatEntryType.OBJECT_ENTRY_CODE -> new ObjectBinaryFormatEntry();
                case BinaryFormatEntryType.ARRAY_ENTRY_CODE -> new ArrayBinaryFormatEntry();
                case BinaryFormatEntryType.BYTE_ENTRY_CODE -> new ByteBinaryFormatEntry(0);
                case BinaryFormatEntryType.SHORT_ENTRY_CODE -> new ShortBinaryFormatEntry(0);
                case BinaryFormatEntryType.INT_ENTRY_CODE -> new IntBinaryFormatEntry(0);
                case BinaryFormatEntryType.LONG_ENTRY_CODE -> new LongBinaryFormatEntry(0L);
                case BinaryFormatEntryType.FLOAT_ENTRY_CODE -> new FloatBinaryFormatEntry(0F);
                case BinaryFormatEntryType.DOUBLE_ENTRY_CODE -> new DoubleBinaryFormatEntry(0D);
                case BinaryFormatEntryType.BOOLEAN_ENTRY_CODE -> new BooleanBinaryFormatEntry(false);
                case BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT_ENTRY_CODE -> new SevenBitEncodedIntBinaryFormatEntry(0);
                case BinaryFormatEntryType.STRING_ENTRY_CODE -> switch (t.GetStringEncoding()) {
                    case UTF16_LE -> new UTF16LEStringBinaryFormatEntry(StringUtils.Empty);
                    case UTF16_BE -> new UTF16BEStringBinaryFormatEntry(StringUtils.Empty);
                    case ASCII -> new ASCIIStringBinaryFormatEntry(StringUtils.Empty);
                    default -> throw new FormatException("Unexpected encoding " + t.GetStringEncoding());
                };
                default -> throw new IOException("Do not know how to decode type " + t.GetEntryCode());
            };
            strm.unread(t.GetEncodedValue());
            entry.ReadFrom(strm);
            return entry;
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
     */
    @NotNull
    public static BinaryFormatEntry LoadGZIPCompressed(InputStream stream)
        throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        return Load(new GZIPInputStream(stream));
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
     */
    public static void Save(OutputStream stream, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(entry, "entry");
        ArgumentNullException.ThrowIfNull(stream, "stream");
        stream.write(HEADER.getBytes(StandardCharsets.US_ASCII));
        entry.WriteTo(stream);
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
     */
    public static void SaveGZIPCompressed(OutputStream stream, BinaryFormatEntry entry)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        Save(new GZIPOutputStream(stream), entry);
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
