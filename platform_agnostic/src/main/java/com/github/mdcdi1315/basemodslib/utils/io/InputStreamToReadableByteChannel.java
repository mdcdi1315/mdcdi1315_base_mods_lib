package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ObjectDisposedException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Provides a class that can translate {@link InputStream} instances into {@link ReadableByteChannel} instances. <br />
 * Additionally, this class does also implement the {@link IInputStreamWrapper} interface.
 * @since 1.0.37
 */
public final class InputStreamToReadableByteChannel
    implements ReadableByteChannel, IInputStreamWrapper
{
    private static final byte FLAGS_NONE = 0;
    private static final byte FLAGS_OWNER = 1 << 0;
    private static final byte FLAGS_DISPOSED = 1 << 1;

    private byte flags;
    private InputStream source;
    // This translation buffer will be created on demand, if so required.
    // See EnsureBuffer method for more information.
    private byte[] translation_buffer;

    /**
     * Initializes a new instance of the {@link InputStreamToReadableByteChannel} class instance.
     * @param source The source {@link InputStream} to reinterpret as a {@link ReadableByteChannel}.
     * @throws ArgumentNullException {@code source} is {@code null}.
     */
    public InputStreamToReadableByteChannel(InputStream source)
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
    public synchronized int read(ByteBuffer dst)
            throws IOException
    {
        int read_bytes;
        int old_position = dst.position();
        int bytes_to_read = dst.remaining();
        if (dst.hasArray() && (!dst.isReadOnly())) {
            read_bytes = source.read(
                    dst.array(),
                    dst.arrayOffset() + old_position,
                    bytes_to_read
            );
            if (read_bytes > 0) {
                dst.position(old_position + read_bytes);
            }
        } else {
            // We need the translation buffer to kick in.
            byte[] intermediate_buffer = EnsureBuffer(bytes_to_read);
            // Specify the number of bytes to read explicitly, the buffer returned through the EnsureBuffer call may be larger than bytes_to_read.
            read_bytes = source.read(intermediate_buffer, 0, bytes_to_read);
            if (read_bytes > 0) {
                dst.put(intermediate_buffer, 0, read_bytes);
            }
        }
        return read_bytes;
    }

    @Pure
    @Override
    public InputStream GetWrapped() { return source; }

    @Pure
    @Override
    public boolean GetIsOwner() { return HasFlagFast(FLAGS_OWNER); }

    @Pure
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
