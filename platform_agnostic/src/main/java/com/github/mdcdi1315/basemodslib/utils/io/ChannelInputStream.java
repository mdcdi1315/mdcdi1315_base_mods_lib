package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.EOFException;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Provides an {@link InputStream} that wraps any {@link ReadableByteChannel}.
 * @since 1.0.26
 */
public final class ChannelInputStream
    extends InputStream
{
    private ByteBuffer temp_buffer;
    private ReadableByteChannel channel;

    /**
     * Initializes a new instance of the {@link ChannelInputStream} class, providing the readable byte channel to use. <br />
     * The buffer that is used for bridging the two abstractions will have a capacity of 4096 bytes.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to wrap.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     */
    public ChannelInputStream(ReadableByteChannel channel) throws ArgumentNullException { this(channel, 4096); }

    /**
     * Initializes a new instance of the {@link ChannelInputStream} class, providing the readable byte channel to use. <br />
     * The capacity of the buffer that is used for bridging the two abstractions can be specified by using the {@code buffer_size} parameter.
     * @param channel The {@linkplain ReadableByteChannel readable byte channel} to wrap.
     * @param buffer_size The desired capacity of the bridging buffer.
     *                    Values below {@linkplain ByteBufferUtils#DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE} are treated as if
     *                    the {@linkplain ByteBufferUtils#DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE} constant was given as input.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     */
    public ChannelInputStream(ReadableByteChannel channel, int buffer_size)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.channel = channel, "channel");
        temp_buffer = ByteBuffer.allocateDirect(Extensions.Max(ByteBufferUtils.DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE, buffer_size));
        temp_buffer.limit(0);
    }

    @Override
    public int read()
            throws IOException
    {
        if (temp_buffer.hasRemaining()) {
            return temp_buffer.get() & 0xFF;
        } else {
            // Buffer is empty, fill the buffer.
            temp_buffer.rewind().limit(temp_buffer.capacity());
            int rb = channel.read(temp_buffer);
            if (rb == -1) {
                return -1;
            } else {
                temp_buffer.limit(rb);
                return temp_buffer.get() & 0xFF;
            }
        }
    }

    @Override
    public void skipNBytes(long n)
            throws IOException
    {
        if (temp_buffer.remaining() > n) {
            // There is the possibility that the need of skipping bytes is covered by the temporary processing buffer bytes.
            // So, just update position and we are good.
            temp_buffer.position(temp_buffer.position() + (int)n);
        } else {
            // Oh, the temporary processing buffer is not enough so we must drain the input channel as well.
            n -= temp_buffer.remaining();

            try {
                int t;
                while (n > 0)
                {
                    temp_buffer.rewind().limit((int) Extensions.Min(n, temp_buffer.capacity())); // Safe cast since we will be at least at (int) value range boundary.
                    t = channel.read(temp_buffer);
                    if (t == -1) {
                        throw new EOFException();
                    } else {
                        n -= t;
                    }
                }
            } finally {
                // Clean temp buffer
                temp_buffer.limit(0);
            }
        }
    }

    @Override
    public int read(@NotNull byte[] b) throws IOException { return read(b, 0, b.length); }

    @Override
    public long transferTo(@NotNull OutputStream out)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(out, "out");

        int rb;
        long transferred = 0;
        // Allocate a new temporary processing buffer
        byte[] buffer = new byte[temp_buffer.capacity()];
        // Empty the buffer if we have data in it.
        while (temp_buffer.hasRemaining())
        {
            temp_buffer.get(buffer, 0, rb = Extensions.ComputeStreamBufferSize(temp_buffer.position(), temp_buffer.limit(), buffer.length));
            out.write(buffer, 0, rb);
            transferred += rb;
        }

        // Follow a different strategy here:
        // Create a new wrapping buffer and use that to handle the copy operation.
        ByteBuffer transfer_buf = ByteBuffer.wrap(buffer);

        while ((rb = channel.read(transfer_buf)) > -1)
        {
            out.write(buffer, 0, rb);
            transfer_buf.rewind();
            transferred += rb;
        }

        return transferred;
    }

    /**
     * Transfers all the data that the current {@link ChannelInputStream} holds, to the specified {@link WritableByteChannel} instance.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to use.
     * @return The number of bytes written to the {@code channel}. May also be 0.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     */
    public long WriteToChannel(@NotNull WritableByteChannel channel)
            throws IOException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(channel, "channel");

        try {
            return StreamUtils.CopyTo(this.channel, channel, temp_buffer);
        } finally {
            // Empty the buffer again...
            temp_buffer.limit(0);
        }
    }

    @Override
    public long skip(long n)
            throws IOException
    {
        long skipped = temp_buffer.remaining();
        if (skipped > n) {
            // There is the possibility that the need of skipping bytes is covered by the saved buffer bytes.
            // So, just update position and we are good.
            temp_buffer.position(temp_buffer.position() + (int)n);
            return n;
        } else {
            // Oh, the saved buffer is not enough so we must drain the input channel.
            n -= skipped;

            try {
                int t;
                while (n > 0)
                {
                    temp_buffer.rewind().limit((int) Extensions.Min(n, temp_buffer.capacity())); // Safe cast since we will be at least at (int) value range boundary.
                    t = channel.read(temp_buffer);
                    if (t == -1) {
                        return skipped;
                    } else {
                        n -= t;
                        skipped += t;
                    }
                }
                return skipped;
            } finally {
                temp_buffer.limit(0);
            }
        }
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len)
            throws IOException
    {
        int t, read = 0;
        while (len > 0)
        {
            // Drain temporary processing buffer
            while (temp_buffer.hasRemaining())
            {
                temp_buffer.get(b, off, t = Extensions.Min(len, temp_buffer.remaining()));
                read += t;
                off += t;
                len -= t;
            }
            if (len > 0)
            {
                // We need some extra bytes. Let's fetch them from the byte channel.
                temp_buffer.rewind().limit(temp_buffer.capacity());
                if ((t = channel.read(temp_buffer)) == -1) {
                    // Make temp_buffer limit to be zero to avoid further invocations to return empty data
                    temp_buffer.limit(0);
                    // If we had any bytes into the temporary buffer, use them; otherwise, return -1 and give up.
                    return read > 0 ? read : -1;
                } else {
                    temp_buffer.rewind().limit(t);
                }
            }
        }
        return read;
    }

    @Override
    public int readNBytes(@NotNull byte[] b, int off, int len)
            throws IOException
    {
        int t, read = 0;
        while (len > 0)
        {
            // Drain temporary processing buffer
            while (temp_buffer.hasRemaining())
            {
                temp_buffer.get(b, off, t = Extensions.Min(len, temp_buffer.remaining()));
                read += t;
                off += t;
                len -= t;
            }
            if (len > 0)
            {
                // We need some extra bytes. Let's fetch them from the byte channel.
                temp_buffer.rewind().limit(temp_buffer.capacity());
                if ((t = channel.read(temp_buffer)) == -1) {
                    // Make temp_buffer limit to be zero to avoid further invocations to return empty data
                    temp_buffer.limit(0);
                    // If we had any bytes into the temporary buffer, use them; otherwise, return 0 and give up.
                    return Extensions.Max(read, 0);
                } else {
                    temp_buffer.rewind().limit(t);
                }
            }
        }
        return read;
    }

    @Override
    public void close()
            throws IOException
    {
        synchronized (this)
        {
            try {
                if (channel != null)
                {
                    channel.close();
                    channel = null;
                }
            } finally {
                temp_buffer = null;
            }
        }
    }
}
