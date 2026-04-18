package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Provides an {@link OutputStream} that wraps any {@link WritableByteChannel}.
 * @since 1.0.26
 */
public final class ChannelOutputStream
    extends OutputStream
{
    private WritableByteChannel channel;

    /**
     * Initializes a new instance of the {@link ChannelOutputStream} class by specifying the writable byte channel to use.
     * @param channel The {@linkplain WritableByteChannel writable byte channel} to use.
     * @throws ArgumentNullException {@code channel} is {@code null}.
     */
    public ChannelOutputStream(WritableByteChannel channel)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.channel = channel, "channel");
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException { channel.write(ByteBuffer.wrap(b)); }

    @Override
    public void write(int b) throws IOException { channel.write(ByteBuffer.wrap(new byte[]{ (byte)b })); }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException { channel.write(ByteBuffer.wrap(b, off, len)); }

    public long WriteFromChannel(ReadableByteChannel channel) throws IOException, ArgumentNullException { return StreamUtils.CopyTo(channel, this.channel); }

    public long WriteFromInputStream(InputStream input_stream) throws IOException, ArgumentNullException { return StreamUtils.CopyTo(input_stream, channel); }

    @Override
    public void flush()
            throws IOException
    {
        if (channel instanceof FileChannel fc) {
            fc.force(false);
        } else if (channel instanceof Flushable f) {
            f.flush();
        }
    }

    @Override
    public void close()
            throws IOException
    {
        synchronized (this)
        {
            if (channel != null)
            {
                channel.close();
                channel = null;
            }
        }
    }
}
