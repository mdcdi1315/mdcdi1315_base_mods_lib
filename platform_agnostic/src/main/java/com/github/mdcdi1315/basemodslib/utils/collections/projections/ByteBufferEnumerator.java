package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.nio.ByteBuffer;

/**
 * Enumerates the contents of a {@link ByteBuffer} instance. <br />
 * During construction of the enumerator, the provided buffer's
 * position, mark and limit are captured and a read-only view
 * of the originally provided buffer is created.
 * @since 1.0.37
 */
public final class ByteBufferEnumerator
    extends BaseByteEnumerator
{
    private byte current;
    private final ByteBuffer buffer;

    /**
     * Initializes a new instance of the {@link ByteBufferEnumerator} class
     * from the specified {@linkplain ByteBuffer byte buffer}.
     * @param buffer The {@linkplain ByteBuffer byte buffer} to initialize the enumerator from.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     */
    public ByteBufferEnumerator(ByteBuffer buffer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        current = 0;
        this.buffer = buffer.asReadOnlyBuffer();
    }

    @Pure
    @NotNull
    @Override
    public Byte getCurrent() { return current; }

    @Pure
    @Override
    public byte getUncastedCurrent() { return current; }

    @Override
    protected void ResetImpl() throws InvalidOperationException { buffer.rewind(); }

    @Override
    protected boolean MoveNextImpl()
            throws InvalidOperationException
    {
        if (buffer.hasRemaining()) {
            current = buffer.get();
            return true;
        } else {
            return false;
        }
    }
}
