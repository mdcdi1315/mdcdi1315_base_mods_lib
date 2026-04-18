package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

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
}
