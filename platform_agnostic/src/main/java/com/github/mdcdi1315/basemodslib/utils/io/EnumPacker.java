package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.FixedArrayBasedList;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Provides packing and unpacking utilities for transferring flaggable enumeration values
 * (represented in Java as arrays holding enumeration constants), in cost of CPU and favoring
 * storage requirements.
 * @since 1.0.37
 */
public final class EnumPacker
{
    private EnumPacker() {}

    @Pure
    private static int ConstructPackValue(int index) { return 1 << index; }

    private record PackingResults(int count, byte[] data_to_write) {}

    /**
     * Packs the specified defined enumeration constants into an integer.
     * @param values The constants to pack.
     * @return An integer containing the packed values.
     * @param <T> The type of enumeration constants to pack.
     * @throws ArgumentNullException {@code values} is {@code null}.
     * @throws ArgumentException A defined constant has an ordinal larger than 31.
     */
    @SafeVarargs
    public static <T extends Enum<T>> int Pack(T... values)
        throws ArgumentException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        int packed = 0;
        for (T value : values)
        {
            if (value == null) {
                throw new ArgumentException("Null enumeration value not allowed", "values");
            } else {
                int ordinal = value.ordinal();
                if (ordinal > 31) {
                    throw new ArgumentException("Values with ordinals larger than 31 cannot be packed", "values");
                } else {
                    packed |= ConstructPackValue(ordinal);
                }
            }
        }
        return packed;
    }

    /**
     * Unpacks previously packed enumeration constants defined through the {@link #Pack(Enum[])} method.
     * @param enum_type The type of the enumeration to discover it's values.
     * @param value The packed value, as returned by the {@link #Pack(Enum[])} method.
     * @return The defined enumeration values, after the specified input packed value was decoded.
     * @param <T> The type of enumeration constants to unpack.
     * @throws ArgumentNullException {@code enum_type} is {@code null}.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] Unpack(Class<T> enum_type, int value)
            throws ArgumentNullException
    {
        T[] values = enum_type.getEnumConstants();
        FixedArrayBasedList<T> list = new FixedArrayBasedList<>(values.length);
        for (int I = 0; I < values.length; I++)
        {
            if ((ConstructPackValue(I) & value) != 0) { list.Add(values[I]); }
        }
        T[] ret_values = (T[])Array.CreateInstance(enum_type, list.GetCount());
        list.CopyTo(ret_values, 0);
        return ret_values;
    }

    /**
     * Gets a value whether the specified enumeration constant is encoded into the packed value.
     * @param value The enumeration constant to test for its presence.
     * @param packed_value The previously packed value produced by the {@link #Pack(Enum[])} method.
     * @return A value whether {@code value} is defined in the provided packed value.
     * @param <T> The type of enumeration constant.
     * @throws ArgumentNullException {@code value} is {@code null}.
     */
    public static <T extends Enum<T>> boolean IsDefined(T value, int packed_value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        return (packed_value & ConstructPackValue(value.ordinal() & 31)) != 0;
    }

    private static <T extends Enum<T>> PackingResults PackToStreamCommon(T[] values)
    {
        int count = 0;
        byte[] packed_array = new byte[values.length];

        for (T value : values)
        {
            if (value == null) {
                throw new ArgumentException("Null enumeration value not allowed", "values");
            } else {
                int ordinal = value.ordinal();
                if (ordinal > 255) {
                    throw new ArgumentException("Values with ordinals larger than 255 cannot be packed", "values");
                } else {
                    packed_array[count++] = (byte) ordinal;
                }
            }
        }

        return new PackingResults(count, packed_array);
    }

    /**
     * Packs the given enumeration values into the specified data stream.
     * @param channel The data stream to write the packed values into.
     * @param values The values to pack and write.
     * @param <T> The type of enumeration constants to pack.
     * @throws ArgumentNullException {@code channel} and/or {@code values} are {@code null}.
     * @throws ArgumentException A defined constant has an ordinal larger than 255.
     * @throws IOException An I/O exception occurred while writing the packed data.
     * @apiNote Use this API when the ordinals of the constants exceed the value 31.
     * @see #UnpackFromStream(InputStream, Class)
     * @see #UnpackFromNetworkBuffer(ByteBuf, Class)
     * @see #UnpackFromStream(ReadableByteChannel, Class)
     */
    @SafeVarargs
    public static <T extends Enum<T>> void PackToStream(WritableByteChannel channel, T... values)
            throws ArgumentNullException, ArgumentException, IOException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        ArgumentNullException.ThrowIfNull(channel, "channel");

        PackingResults results = PackToStreamCommon(values);
        int count = results.count;

        StreamUtils.WriteBufferEnsured(
                channel,
                ByteBuffer.allocate(count + 1)
                        .put((byte)count)
                        .put(results.data_to_write, 0, count)
                        .rewind()
        );
    }

    /**
     * Packs the given enumeration values into the specified data stream.
     * @param stream The data stream to write the packed values into.
     * @param values The values to pack and write.
     * @param <T> The type of enumeration constants to pack.
     * @throws ArgumentNullException {@code channel} and/or {@code values} are {@code null}.
     * @throws ArgumentException A defined constant has an ordinal larger than 255.
     * @throws IOException An I/O exception occurred while writing the packed data.
     * @apiNote Use this API when the ordinals of the constants exceed the value 31.
     * @see #PackToStream(WritableByteChannel, Enum[])
     * @see #PackToNetworkBuffer(ByteBuf, Enum[])
     * @see #UnpackFromStream(ReadableByteChannel, Class)
     */
    @SafeVarargs
    public static <T extends Enum<T>> void PackToStream(OutputStream stream, T... values)
        throws ArgumentNullException, ArgumentException, IOException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        ArgumentNullException.ThrowIfNull(values, "values");

        PackingResults results = PackToStreamCommon(values);
        int count = results.count;

        stream.write(count);
        stream.write(results.data_to_write, 0, count);
    }

    /**
     * Packs the given enumeration values into the specified network buffer.
     * @param buffer The network buffer to write the packed values into.
     * @param values The values to pack and write.
     * @param <T> The type of enumeration constants to pack.
     * @throws ArgumentNullException {@code channel} and/or {@code values} are {@code null}.
     * @throws ArgumentException A defined constant has an ordinal larger than 255.
     * @apiNote Use this API when the ordinals of the constants exceed the value 31, and
     * you want to pack the values to a network connection.
     * @see #PackToStream(OutputStream, Enum[])
     * @see #PackToStream(WritableByteChannel, Enum[])
     * @see #UnpackFromStream(ReadableByteChannel, Class)
     */
    @SafeVarargs
    public static <T extends Enum<T>> void PackToNetworkBuffer(ByteBuf buffer, T... values)
            throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");

        PackingResults results = PackToStreamCommon(values);

        int ct = results.count;
        buffer.writeByte(ct);
        buffer.writeBytes(results.data_to_write, 0, ct);
    }

    /**
     * Unpacks the enumeration values of the specified type, previously encoded by the {@link #PackToStream(WritableByteChannel, Enum[])} method.
     * @param channel The data stream to read the packed values from.
     * @param enum_type The type of the enumeration that contains the data to get back the previously packed values.
     * @return The defined enumeration values, after the packed data were decoded from the stream.
     * @param <T> The type of enumeration constants to unpack.
     * @throws ArgumentNullException {@code channel} and/or {@code enum_type} are {@code null}.
     * @throws IOException An I/O exception occurred while reading the packed data.
     * @see #PackToStream(OutputStream, Enum[])
     * @see #PackToStream(WritableByteChannel, Enum[])
     * @see #PackToNetworkBuffer(ByteBuf, Enum[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] UnpackFromStream(ReadableByteChannel channel, Class<T> enum_type)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ArgumentNullException.ThrowIfNull(enum_type, "enum_type");
        int count = StreamUtils.ReadByte(channel) & 0xFF, index = 0;
        ByteBuffer buffer = ByteBuffer.allocate(count);
        StreamUtils.ReadBufferEnsured(channel, buffer);
        T[] values = enum_type.getEnumConstants();
        buffer.rewind();
        T[] ret = (T[])Array.CreateInstance(enum_type, count);
        while (buffer.hasRemaining()) { ret[index++] = values[buffer.get() & 0xFF]; }
        return ret;
    }

    /**
     * Unpacks the enumeration values of the specified type, previously encoded by the {@link #PackToStream(OutputStream, Enum[])} method.
     * @param stream The data stream to read the packed values from.
     * @param enum_type The type of the enumeration that contains the data to get back the previously packed values.
     * @return The defined enumeration values, after the packed data were decoded from the stream.
     * @param <T> The type of enumeration constants to unpack.
     * @throws ArgumentNullException {@code channel} and/or {@code enum_type} are {@code null}.
     * @throws IOException An I/O exception occurred while reading the packed data.
     * @see #PackToStream(OutputStream, Enum[])
     * @see #PackToStream(WritableByteChannel, Enum[])
     * @see #PackToNetworkBuffer(ByteBuf, Enum[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] UnpackFromStream(InputStream stream, Class<T> enum_type)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        ArgumentNullException.ThrowIfNull(enum_type, "enum_type");
        int count = stream.read();
        if (count == -1) {
            throw new EOFException("The stream ended prematurely");
        } else {
            byte[] data = stream.readNBytes(count);
            T[] values = enum_type.getEnumConstants();
            T[] ret = (T[])Array.CreateInstance(enum_type, count);
            for (int I = 0; I < count; I++) { ret[I] = values[data[I] & 0xFF]; }
            return ret;
        }
    }

    /**
     * Unpacks the enumeration values of the specified type, previously encoded by the {@link #PackToNetworkBuffer(ByteBuf, Enum[])} method.
     * @param buffer The network buffer to read the packed values from.
     * @param enum_type The type of the enumeration that contains the data to get back the previously packed values.
     * @return The defined enumeration values, after the packed data were decoded from the stream.
     * @param <T> The type of enumeration constants to unpack.
     * @throws ArgumentNullException {@code channel} and/or {@code enum_type} are {@code null}.
     * @throws DecoderException A decoding exception occurred while reading the packed data.
     * @see #PackToStream(OutputStream, Enum[])
     * @see #PackToStream(WritableByteChannel, Enum[])
     * @see #PackToNetworkBuffer(ByteBuf, Enum[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] UnpackFromNetworkBuffer(ByteBuf buffer, Class<T> enum_type)
            throws ArgumentNullException, DecoderException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(enum_type, "enum_type");
        try {
            int count = buffer.readByte() & 0xFF;
            byte[] data = new byte[count];
            buffer.readBytes(data);
            T[] ret = (T[]) Array.CreateInstance(enum_type, count);
            T[] values = enum_type.getEnumConstants();
            for (int I = 0; I < count; I++) { ret[I] = values[data[I] & 0xFF]; }
            return ret;
        } catch (IndexOutOfBoundsException ex) {
            throw new DecoderException("Could not decode the network buffer because not enough bytes could be read from it.", ex);
        }
    }
}
