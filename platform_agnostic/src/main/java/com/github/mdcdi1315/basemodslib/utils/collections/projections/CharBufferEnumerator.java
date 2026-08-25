package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.nio.CharBuffer;

/**
 * Enumerates the contents of a {@link CharBuffer} instance. <br />
 * During construction of the enumerator, the provided buffer's
 * position, mark and limit are captured and a read-only view
 * of the originally provided buffer is created.
 * @since 1.0.37
 */
public final class CharBufferEnumerator
    extends BaseCharEnumerator
{
    private char current;
    private final CharBuffer buffer;

    /**
     * Initializes a new instance of the {@link CharBufferEnumerator} class
     * from the specified {@linkplain CharBuffer character buffer}.
     * @param buffer The {@linkplain CharBuffer character buffer} to initialize the enumerator from.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     */
    public CharBufferEnumerator(CharBuffer buffer)
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        current = 0;
        this.buffer = buffer.asReadOnlyBuffer();
    }

    @Pure
    @NotNull
    @Override
    public Character getCurrent() { return current; }

    @Pure
    @Override
    public char getUncastedCurrent() { return current; }

    @Override
    protected void ResetImpl() { buffer.rewind(); }

    @Override
    protected boolean MoveNextImpl()
    {
        if (buffer.hasRemaining()) {
            current = buffer.get();
            return true;
        } else {
            return false;
        }
    }
}
