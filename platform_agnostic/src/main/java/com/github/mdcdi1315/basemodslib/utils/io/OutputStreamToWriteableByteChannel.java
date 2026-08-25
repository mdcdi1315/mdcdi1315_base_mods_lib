package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ObjectDisposedException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Provides a class that can translate {@link OutputStream} instances into {@link WritableByteChannel} instances. <br />
 * Additionally, this class does also implement the {@link IOutputStreamWrapper} interface.
 * @since 1.0.37
 */
public final class OutputStreamToWriteableByteChannel
    implements WritableByteChannel, IOutputStreamWrapper
{
    private static final byte FLAGS_NONE = 0;
    private static final byte FLAGS_OWNER = 1 << 0;
    private static final byte FLAGS_DISPOSED = 1 << 1;

    private byte flags;
    private OutputStream source;
    // This translation buffer will be created on demand, if so required.
    // See EnsureBuffer method for more information.
    private byte[] translation_buffer;

    /**
     * Initializes a new instance of the {@link OutputStreamToWriteableByteChannel} class instance.
     * @param source The source {@link OutputStream} to reinterpret as a {@link WritableByteChannel}.
     * @throws ArgumentNullException {@code source} is {@code null}.
     */
    public OutputStreamToWriteableByteChannel(OutputStream source)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(source, "source");
        this.source = source;
        this.flags = FLAGS_NONE;
        this.translation_buffer = null;
    }

    @Pure
    private boolean HasFlagFast(byte flag) { return (flags & flag) != 0; }

    @Pure
    @SuppressWarnings("lossy-conversions")
    private void SetFlag(byte flag, boolean value)
    {
        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
    }

    @Pure
    @NotNull
    private byte[] EnsureBuffer(int size)
    {
        if (translation_buffer == null) {
            translation_buffer = new byte[size];
        } else if (size > translation_buffer.length) {
            translation_buffer = new byte[size];
        }
        return translation_buffer;
    }

    @Override
    public synchronized int write(ByteBuffer src)
            throws IOException
    {
        int old_position = src.position();
        int bytes_to_write = src.remaining();
        if (src.hasArray() && (!src.isReadOnly())) {
            source.write(
                    src.array(),
                    src.arrayOffset() + old_position,
                    bytes_to_write
            );
            src.position(old_position + bytes_to_write);
        } else {
            // We need the translation buffer to kick in.
            byte[] intermediate_buffer = EnsureBuffer(bytes_to_write);
            // Specify the number of bytes to write explicitly, the buffer returned through the EnsureBuffer call may be larger than bytes_to_write.
            src.get(intermediate_buffer, 0, bytes_to_write);
            source.write(intermediate_buffer, 0, bytes_to_write);
        }
        return bytes_to_write;
    }

    @Pure
    @Override
    public OutputStream GetWrapped() { return source; }

    @Override
    public boolean GetIsOwner() { return HasFlagFast(FLAGS_OWNER); }

    @Override
    public boolean isOpen() { return !HasFlagFast(FLAGS_DISPOSED); }

    @Override
    public void SetIsOwner(boolean value)
            throws InvalidOperationException
    {
        ObjectDisposedException.ThrowIf(HasFlagFast(FLAGS_DISPOSED), this);
        SetFlag(FLAGS_OWNER, value);
    }

    @Override
    public void close()
            throws IOException
    {
        synchronized (this)
        {
            if (isOpen())
            {
                if (HasFlagFast(FLAGS_OWNER)) { source.close(); }
                source = null;
                translation_buffer = null;
                SetFlag(FLAGS_DISPOSED, true);
            }
        }
    }
}
