package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.EOFException;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Several utility methods for manipulating data streams.
 * @since 1.0.26
 */
public final class StreamUtils
{
    private StreamUtils() {}

    private static void WriteBufferEnsured_Unsafe(WritableByteChannel channel, ByteBuffer buffer)
            throws IOException { while (buffer.hasRemaining()) { channel.write(buffer); } }

    private static void ReadBufferEnsured_Unsafe(ReadableByteChannel channel, ByteBuffer buffer)
            throws IOException
    {
        while (buffer.hasRemaining())
        {
            if (channel.read(buffer) == -1) {
                throw new EOFException("The stream ended prematurely");
            }
        }
    }

    /**
     * Copies all the data of the specified {@linkplain ReadableByteChannel readable byte channel} to the specified
     * {@linkplain WritableByteChannel writable byte channel}, using the specified {@linkplain ByteBuffer byte buffer} to copy the data to. <br />
     * Note: If the buffer has data in it, the data that it has will be stored to the writable byte channel as well.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to copy data from.
     * @param destination The {@linkplain WritableByteChannel writable byte channel} that is the target channel in which all the data will be copied to.
     * @param buffer The {@linkplain ByteBuffer byte buffer} to use for copying the data between the two channels.
     * @return Number of bytes copied from {@code channel} and {@code buffer}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code channel} and/or {@code destination} and/or {@code buffer} are {@code null}.
     */
    public static long CopyTo(ReadableByteChannel channel, WritableByteChannel destination, ByteBuffer buffer)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ArgumentNullException.ThrowIfNull(destination, "destination");

        long transferred = 0;

        // Empty the buffer if we have stale data in it.
        if (buffer.hasRemaining()) { transferred += destination.write(buffer); }

        int rb;
        buffer.rewind().limit(buffer.capacity());
        while ((rb = channel.read(buffer)) > -1)
        {
            buffer.rewind().limit(rb);
            transferred += destination.write(buffer);
            buffer.rewind().limit(buffer.capacity());
        }

        return transferred;
    }

    /**
     * Copies all the data of the specified {@linkplain ReadableByteChannel readable byte channel} to the specified {@linkplain WritableByteChannel writable byte channel}. <br />
     * The method allocates a new {@linkplain ByteBuffer byte buffer} for copying the data using the desired {@code buffer_size}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to copy data from.
     * @param destination The {@linkplain WritableByteChannel writable byte channel} that is the target channel in which all the data will be copied to.
     * @param buffer_size The capacity of the temporary buffer. Destroyed after this method completes.
     * @return Number of bytes copied from {@code channel}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code channel} and/or {@code destination} are {@code null}.
     * @implNote This method forwards to {@link #CopyTo(ReadableByteChannel, WritableByteChannel, ByteBuffer)} method, and it creates a buffer of the requested size.
     */
    public static long CopyTo(ReadableByteChannel channel, WritableByteChannel destination, int buffer_size)
            throws ArgumentNullException, IOException
    {
        return CopyTo(
                channel,
                destination,
                ByteBuffer
                        .allocateDirect(Math.max(buffer_size, ByteBufferUtils.DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE))
                        .limit(0)
        );
    }

    /**
     * Copies all the data of the specified {@linkplain ReadableByteChannel readable byte channel} to the specified {@linkplain WritableByteChannel writable byte channel}. <br />
     * The method allocates a new {@linkplain ByteBuffer byte buffer} for copying the data.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to copy data from.
     * @param destination The {@linkplain WritableByteChannel writable byte channel} that is the target channel in which all the data will be copied to.
     * @return Number of bytes copied from {@code channel}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code channel} and/or {@code destination} are {@code null}.
     * @implNote This method forwards to {@link #CopyTo(ReadableByteChannel, WritableByteChannel, int)} method, and specifies a buffer size of the {@linkplain ByteBufferUtils#DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE} constant.
     */
    public static long CopyTo(ReadableByteChannel channel, WritableByteChannel destination) throws IOException, ArgumentNullException { return CopyTo(channel, destination, 0); }

    /**
     * Copies all the data of the specified {@linkplain ReadableByteChannel readable byte channel} to the specified
     * {@linkplain OutputStream output stream}, using the specified {@linkplain ByteBuffer byte buffer} to copy the data to. <br />
     * Note: If the buffer has data in it, the data that it has will be stored to the output stream as well.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to copy data from.
     * @param destination The {@linkplain OutputStream output stream} that is the target stream in which all the data will be copied to.
     * @param buffer The {@linkplain ByteBuffer byte buffer} to use for copying the data between the channel and the output stream.
     * @return Number of bytes copied from {@code channel} and {@code buffer} written to {@code destination}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code channel} and/or {@code destination} and/or {@code buffer} are {@code null}.
     */
    public static long CopyTo(ReadableByteChannel channel, OutputStream destination, ByteBuffer buffer)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ArgumentNullException.ThrowIfNull(destination, "destination");
        long transferred = 0;

        byte[] temp;
        if (buffer.hasRemaining())
        {
            if (buffer.hasArray()) {
                destination.write(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
            } else {
                temp = new byte[buffer.remaining()];
                buffer.get(temp);
                destination.write(temp);
            }
        }

        buffer.rewind().limit(buffer.capacity());

        int rb;
        if (buffer.hasArray()) {
            temp = buffer.array();
            while ((rb = channel.read(buffer)) > -1)
            {
                destination.write(temp, 0, rb);
                buffer.rewind();
                transferred += rb;
            }
        } else {
            temp = new byte[buffer.capacity()];
            while ((rb = channel.read(buffer)) > -1)
            {
                buffer.get(temp, 0, rb);
                destination.write(temp, 0, rb);
                buffer.rewind();
                transferred += rb;
            }
        }

        return transferred;
    }

    /**
     * Copies all the data of the specified {@linkplain InputStream input stream} to the specified
     * {@linkplain WritableByteChannel writable byte channel}, using the specified {@linkplain ByteBuffer byte buffer} to copy the data to. <br />
     * Note: If the buffer has data in it, the data that it has will be stored to the writable byte channel as well.
     * @param stream The {@linkplain InputStream input stream} to copy data from.
     * @param destination The {@linkplain WritableByteChannel writable byte channel} that is the target channel in which all the data will be copied to.
     * @param buffer The {@linkplain ByteBuffer byte buffer} to use for copying the data between the input stream and the channel.
     * @return Number of bytes copied from {@code stream} and {@code buffer} written to {@code destination}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code destination} and/or {@code buffer} are {@code null}.
     */
    public static long CopyTo(InputStream stream, WritableByteChannel destination, ByteBuffer buffer)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(stream, "stream");
        ArgumentNullException.ThrowIfNull(destination, "destination");
        int rb, cap = buffer.capacity();
        long transferred = 0;

        if (buffer.hasRemaining()) { transferred += destination.write(buffer); }

        if (buffer.hasArray()) {
            byte[] w = buffer.array();
            int w_ofs = buffer.arrayOffset();
            while ((rb = stream.read(w, w_ofs, cap)) > -1)
            {
                buffer.rewind().limit(rb);
                transferred += destination.write(buffer);
            }
        } else {
            byte[] w = new byte[cap];
            while ((rb = stream.read(w, 0, cap)) > -1)
            {
                buffer.put(w, 0, rb);
                buffer.rewind().limit(rb);
                transferred += destination.write(buffer);
            }
        }

        return transferred;
    }

    /**
     * Copies all the data of the specified {@linkplain InputStream input stream} to the specified {@linkplain WritableByteChannel writable byte channel}. <br />
     * The method allocates a new {@linkplain ByteBuffer byte buffer} for copying the data using the desired {@code buffer_size}.
     * @param stream The {@linkplain InputStream input stream} to copy data from.
     * @param destination The {@linkplain WritableByteChannel writable byte channel} that is the target channel in which all the data will be copied to.
     * @param buffer_size The capacity of the temporary buffer. Destroyed after this method completes.
     * @return Number of bytes copied from {@code stream} written to {@code destination}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code destination} are {@code null}.
     * @implNote This method forwards to {@link #CopyTo(InputStream, WritableByteChannel, ByteBuffer)} method, and it creates a buffer of the requested size.
     */
    public static long CopyTo(InputStream stream, WritableByteChannel destination, int buffer_size)
            throws IOException, ArgumentNullException
    {
        return CopyTo(
                stream,
                destination,
                ByteBuffer
                        .allocate(Math.max(buffer_size, ByteBufferUtils.DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE))
                        .limit(0)
        );
    }

    /**
     * Copies all the data of the specified {@linkplain InputStream input stream} to the specified {@linkplain WritableByteChannel writable byte channel}. <br />
     * The method allocates a new {@linkplain ByteBuffer byte buffer} for copying the data.
     * @param stream The {@linkplain InputStream input stream} to copy data from.
     * @param destination The {@linkplain WritableByteChannel writable byte channel} that is the target channel in which all the data will be copied to.
     * @return Number of bytes copied from {@code stream} written to {@code destination}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code destination} are {@code null}.
     * @implNote This method forwards to {@link #CopyTo(InputStream, WritableByteChannel, int)} method, and specifies a buffer size of the {@linkplain ByteBufferUtils#DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE} constant.
     */
    public static long CopyTo(InputStream stream, WritableByteChannel destination) throws IOException, ArgumentNullException { return CopyTo(stream, destination, 0); }

    /**
     * Copies all the data of the specified {@linkplain InputStream input stream} to the specified {@linkplain OutputStream output stream}. <br />
     * To perform the copy, a new buffer of size {@code buffer_size} is allocated.
     * @param stream The {@linkplain InputStream input stream} to copy data from.
     * @param destination The {@linkplain OutputStream output stream} that is the target stream in which all the data will be copied to.
     * @param buffer_size The capacity of the temporary buffer to perform the copy from {@code stream} to {@code destination}.
     * @return Number of bytes copied from {@code stream} written to {@code destination}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code destination} are {@code null}.
     */
    public static long CopyTo(InputStream stream, OutputStream destination, int buffer_size)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        ArgumentNullException.ThrowIfNull(destination, "destination");

        buffer_size = Math.max(buffer_size, ByteBufferUtils.DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE);

        byte[] buffer = new byte[buffer_size];

        int rb;
        long transferred = 0;

        while ((rb = stream.read(buffer)) > -1) { destination.write(buffer, 0, rb); transferred += rb; }

        return transferred;
    }

    /**
     * Copies all the data of the specified {@linkplain InputStream input stream} to the specified {@linkplain OutputStream output stream}. <br />
     * To perform the copy, a new buffer of size {@linkplain ByteBufferUtils#DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE} is allocated.
     * @param stream The {@linkplain InputStream input stream} to copy data from.
     * @param destination The {@linkplain OutputStream output stream} that is the target stream in which all the data will be copied to.
     * @return Number of bytes copied from {@code stream} written to {@code destination}.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code stream} and/or {@code destination} are {@code null}.
     */
    public static long CopyTo(InputStream stream, OutputStream destination) throws IOException, ArgumentNullException { return CopyTo(stream, destination, 0); }

    /**
     * Writes the specified {@link ByteBuffer} to the specified stream.
     * @param stream The {@link OutputStream} to write the specified {@link ByteBuffer} to.
     * @param buffer The {@link ByteBuffer} to write.
     * @throws ArgumentNullException {@code stream} and/or {@code buffer} are {@code null}.
     * @throws IOException {@link OutputStream#write(byte[], int, int)} call threw an exception.
     * @see OutputStreamToWriteableByteChannel
     * @since 1.0.35
     */
    public static void WriteBuffer(OutputStream stream, ByteBuffer buffer)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");

        if (buffer.hasArray() && (!buffer.isReadOnly())) {
            // Fast write path
            stream.write(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.remaining()
            );
        } else {
            // Slow write path, uses intermediate buffer to achieve this.
            // Additionally, original position value must be preserved and restored after copy.
            int previous_position = buffer.position();
            try {
                byte[] temp = new byte[buffer.remaining()];
                buffer.get(temp);
                stream.write(temp);
            } finally {
                buffer.position(previous_position);
            }
        }
    }

    /**
     * Writes the specified {@link ByteBuffer} to the specified writable byte channel,
     * ensuring that the entire remaining buffer contents are written to the channel.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the {@linkplain ByteBuffer byte buffer} to.
     * @param buffer The {@linkplain ByteBuffer byte buffer} whose contents are to be written to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @throws ArgumentNullException {@code channel} and/or {@code buffer} are {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an I/O exception.
     * @since 1.0.37
     */
    public static void WriteBufferEnsured(WritableByteChannel channel, ByteBuffer buffer)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(channel, "channel");

        WriteBufferEnsured_Unsafe(channel, buffer);
    }

    /**
     * Reads data from the specified {@link ReadableByteChannel} to the specified {@link ByteBuffer},
     * ensuring that the entire remaining buffer capacity is read from the channel.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to fill the {@linkplain ByteBuffer byte buffer} with data.
     * @param buffer The {@linkplain ByteBuffer byte buffer} that is to be filled with data from the specified {@linkplain ReadableByteChannel readable byte channel}.
     * @throws ArgumentNullException {@code channel} and/or {@code buffer} are {@code null}.
     * @throws IOException {@link ReadableByteChannel#read(ByteBuffer)} threw an I/O exception.
     * @throws EOFException The stream ended before the entire buffer was filled.
     * @since 1.0.37
     */
    public static void ReadBufferEnsured(ReadableByteChannel channel, ByteBuffer buffer)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(channel, "channel");

        ReadBufferEnsured_Unsafe(channel, buffer);
    }

    /**
     * Reads data from the given {@link InputStream} object as a {@link ByteBuffer}.
     * @param stream The {@link InputStream} object to read data from.
     * @param bytes_to_read The number of bytes to read from {@code stream}.
     * @return The read {@link ByteBuffer} instance. <br />
     * If the stream ended, the method will return {@code null} to indicate this. <br />
     * Note also that giving the {@code number_of_bytes} a value of 0 is valid,
     * but does always return an empty buffer, regardlessly the state of the provided stream.
     * @throws IOException {@link InputStream#read(byte[], int, int)} call threw an exception.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code bytes_to_read} is negative.
     * @see InputStreamToReadableByteChannel
     * @since 1.0.35
     */
    @MaybeNull
    public static ByteBuffer ReadAsBuffer(InputStream stream, int bytes_to_read)
            throws ArgumentNullException, ArgumentOutOfRangeException, IOException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        if (bytes_to_read < 0) {
            throw new ArgumentOutOfRangeException("bytes_to_read", "Number of bytes to read cannot be less than 0.");
        } else if (bytes_to_read == 0) {
            return ByteBuffer.allocate(0);
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(bytes_to_read);
            int read = stream.read(
                    buffer.array(),
                    buffer.arrayOffset(),
                    buffer.remaining()
            );
            return (read == -1) ? null : buffer.limit(read);
        }
    }

    /**
     * Writes the specified value as byte value of range 0..255 to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified byte value to.
     * @param byte_value_to_write The byte value to write to the channel. It's high-order 24 bits are completely ignored.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteByte(WritableByteChannel channel, int byte_value_to_write)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Byte.BYTES)
                        .put((byte)(byte_value_to_write & 0xFF))
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a little-endian {@code short} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code short} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteShortLE(WritableByteChannel channel, short value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Short.BYTES)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putShort(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a big-endian {@code short} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code short} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteShortBE(WritableByteChannel channel, short value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Short.BYTES)
                        .order(ByteOrder.BIG_ENDIAN)
                        .putShort(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a little-endian {@code int} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code int} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteIntLE(WritableByteChannel channel, int value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Integer.BYTES)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a big-endian {@code int} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code int} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteIntBE(WritableByteChannel channel, int value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Integer.BYTES)
                        .order(ByteOrder.BIG_ENDIAN)
                        .putInt(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a little-endian {@code long} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code long} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteLongLE(WritableByteChannel channel, long value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Long.BYTES)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putLong(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a big-endian {@code long} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code long} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteLongBE(WritableByteChannel channel, long value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Long.BYTES)
                        .order(ByteOrder.BIG_ENDIAN)
                        .putLong(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a little-endian {@code float} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code float} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteFloatLE(WritableByteChannel channel, float value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Float.BYTES)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putFloat(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a big-endian {@code float} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code float} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteFloatBE(WritableByteChannel channel, float value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Float.BYTES)
                        .order(ByteOrder.BIG_ENDIAN)
                        .putFloat(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a little-endian {@code double} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code double} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteDoubleLE(WritableByteChannel channel, double value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Double.BYTES)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putDouble(value)
                        .rewind()
        );
    }

    /**
     * Writes the specified value as a big-endian {@code double} value to the specified {@linkplain WritableByteChannel writable byte channel}.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to write the specified {@code double} value to.
     * @param value The value to write to the channel.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @throws IOException {@link WritableByteChannel#write(ByteBuffer)} threw an exception.
     * @since 1.0.37
     */
    public static void WriteDoubleBE(WritableByteChannel channel, double value)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        WriteBufferEnsured_Unsafe(
                channel,
                ByteBuffer
                        .allocate(Double.BYTES)
                        .order(ByteOrder.BIG_ENDIAN)
                        .putDouble(value)
                        .rewind()
        );
    }

    /**
     * Reads a single byte from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the byte from.
     * @return The read byte value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static byte ReadByte(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().get();
    }

    /**
     * Reads a {@code short} value, encoded in little-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code short} from.
     * @return The read {@code short} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static short ReadShortLE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /**
     * Reads a {@code short} value, encoded in big-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code short} from.
     * @return The read {@code short} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static short ReadShortBE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.BIG_ENDIAN).getShort();
    }

    /**
     * Reads a {@code int} value, encoded in little-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code int} from.
     * @return The read {@code int} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static int ReadIntLE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * Reads a {@code int} value, encoded in big-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code int} from.
     * @return The read {@code int} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static int ReadIntBE(ReadableByteChannel channel)
        throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.BIG_ENDIAN).getInt();
    }

    /**
     * Reads a {@code long} value, encoded in little-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code long} from.
     * @return The read {@code long} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static long ReadLongLE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    /**
     * Reads a {@code long} value, encoded in big-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code long} from.
     * @return The read {@code long} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static long ReadLongBE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.BIG_ENDIAN).getLong();
    }

    /**
     * Reads a {@code float} value, encoded in little-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code float} from.
     * @return The read {@code float} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static float ReadFloatLE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    /**
     * Reads a {@code float} value, encoded in big-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code float} from.
     * @return The read {@code float} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static float ReadFloatBE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    /**
     * Reads a {@code double} value, encoded in little-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code double} from.
     * @return The read {@code double} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static double ReadDoubleLE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    /**
     * Reads a {@code double} value, encoded in big-endian from the given {@linkplain ReadableByteChannel readable byte channel}.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to read the {@code double} from.
     * @return The read {@code double} value.
     * @throws IOException An I/O exception occurred while reading.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     * @since 1.0.37
     */
    public static double ReadDoubleBE(ReadableByteChannel channel)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        ReadBufferEnsured_Unsafe(channel, buffer);
        return buffer.rewind().order(ByteOrder.BIG_ENDIAN).getDouble();
    }
}
