package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Several utility methods for manipulating data streams.
 * @since 1.0.26
 */
public final class StreamUtils
{
    private StreamUtils() {}

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
            temp = null;
            while ((rb = channel.read(buffer)) > -1)
            {
                destination.write(buffer.array(), 0, rb);
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
}
