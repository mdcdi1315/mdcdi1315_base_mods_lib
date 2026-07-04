package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IByteEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IByteEnumerator;

import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;

/**
 * An {@link InputStream} implementation depending on {@link ByteBuffer}s to retain in-memory data. <br />
 * This class can only read bytes; to create this object you must use one of the dedicated constructors, <br />
 * or use the {@link MemoryStreamBuilder} class. <br />
 * This class does also implement the {@link IByteEnumerable} interface, for the purpose of retrieving the memory stream's
 * bytes without modifying any state of a given {@link MemoryStream} instance.
 * @since 1.0.35
 */
public class MemoryStream
    extends InputStream
    implements IByteEnumerable
{
    private int current_buffer_index;
    private final ByteBuffer[] buffers;
    private long position_absolute, length_absolute;

    /**
     * Constructs a new and empty instance of the {@link MemoryStream} class.
     */
    public MemoryStream()
    {
        super();
        length_absolute = 0L;
        position_absolute = 0L;
        current_buffer_index = -1;
        buffers = new ByteBuffer[0];
    }

    /**
     * Constructs a new instance of the {@link MemoryStream} class, containing a single buffer
     * of the specified data.
     * @param data The byte array that contains the data to initialize the {@link MemoryStream} from.
     * @throws ArgumentNullException {@code data} is {@code null}.
     */
    public MemoryStream(byte[] data)
            throws ArgumentNullException
    {
        super();
        ArgumentNullException.ThrowIfNull(data, "data");
        position_absolute = 0L;
        current_buffer_index = -1;
        length_absolute = data.length;
        buffers = new ByteBuffer[] { ByteBuffer.wrap(data) };
    }

    /**
     * Constructs a new instance of the {@link MemoryStream} class, from the specified array
     * of {@link ByteBuffer}s.
     * The array will be treated as the data that the buffers contain are contiguous.
     * @param buffers The array of {@link ByteBuffer}s to specify.
     * @throws ArgumentNullException {@code buffers} is {@code null}.
     */
    public MemoryStream(ByteBuffer[] buffers)
            throws ArgumentNullException
    {
        super();
        ArgumentNullException.ThrowIfNull(buffers, "buffers");
        length_absolute = 0L;
        position_absolute = 0L;
        this.buffers = buffers;
        current_buffer_index = -1;
        for (ByteBuffer buffer : buffers) { length_absolute += buffer.limit(); }
    }

    /**
     * Constructs a new instance of the {@link MemoryStream} class, from the specified collection
     * of {@link ByteBuffer}s.
     * The collection will be treated as the data that the buffers contain are contiguous.
     * @param buffers The array of {@link ByteBuffer}s to specify.
     * @throws ArgumentNullException {@code buffers} is {@code null}.
     */
    public MemoryStream(ICollection<ByteBuffer> buffers)
            throws ArgumentNullException
    {
        super();
        ArgumentNullException.ThrowIfNull(buffers, "buffers");
        int I = 0;
        length_absolute = 0L;
        position_absolute = 0L;
        current_buffer_index = -1;
        this.buffers = new ByteBuffer[buffers.getCount()];
        try (IEnumerator<ByteBuffer> e = buffers.GetEnumerator())
        {
            ByteBuffer b;
            while (e.MoveNext())
            {
                this.buffers[I++] = b = e.getCurrent();
                length_absolute += b.limit();
            }
        }
    }

    private static final class Enumerator
            extends BaseEnumerator<Byte>
            implements IByteEnumerator
    {
        private int index;
        private byte current_element;
        private ByteBuffer[] buffers;

        public Enumerator(MemoryStream stream)
        {
            index = 0;
            current_element = 0;
            buffers = stream.buffers;
        }

        @Pure
        @NotNull
        @Override
        public Byte getCurrent() { return current_element; }

        @Pure
        @Override
        public byte getUncastedCurrent() { return current_element; }

        @Pure
        @Override
        protected void ResetImpl() { index = 0; }

        @Override
        protected boolean MoveNextImpl()
                throws InvalidOperationException
        {
            ByteBuffer buffer;
            do {
                buffer = buffers[index];
            } while (buffer.remaining() == 0 && ++index < buffers.length);
            if (index < buffers.length) {
                current_element = buffer.get();
                return true;
            } else {
                return false;
            }
        }

        @Pure
        @Override
        public void Dispose()
        {
            synchronized (this)
            {
                super.Dispose();
                index = 0;
                buffers = null;
                current_element = 0;
            }
        }
    }

    @MaybeNull
    private ByteBuffer GetCurrentBuffer()
    {
        ByteBuffer buffer;
        if (current_buffer_index < 0) { current_buffer_index = 0; }
        do {
            buffer = buffers[current_buffer_index];
        } while (buffer.remaining() == 0 && ++current_buffer_index < buffers.length);
        return (current_buffer_index < buffers.length) ? buffer : null;
    }

    /**
     * Gets the length, in bytes, of this {@link MemoryStream} object.
     * @return The absolute length, in bytes, of the current {@link MemoryStream} object.
     */
    @Pure
    public final long GetLength() { return length_absolute; }

    /**
     * Gets the absolute position, in bytes, of this {@link MemoryStream} object.
     * @return The absolute position, in bytes, of the current {@link MemoryStream} object.
     */
    @Pure
    public final long GetPosition() { return position_absolute; }

    /**
     * Sets the absolute position, in bytes, of this {@link MemoryStream} object.
     * @return The previous absolute position, in bytes.
     * @param new_position The new absolute position to position the memory stream.
     * @throws ArgumentOutOfRangeException {@code new_position} is negative. -or- is more than the stream's length.
     */
    public final long SetPosition(long new_position)
            throws ArgumentOutOfRangeException
    {
        if (new_position < 0L) {
            throw new ArgumentOutOfRangeException("new_position", "New position cannot be less than 0.");
        } else if (new_position > length_absolute) {
            throw new ArgumentOutOfRangeException("new_position", "New position cannot be more than the stream's length.");
        } else {
            long remaining;
            ByteBuffer buffer;
            // Two code paths now:
            if (new_position >= (length_absolute / 2L)) {
                // Position is near the end of the stream, start by finding the position backwards.
                remaining = length_absolute - new_position;
                current_buffer_index = buffers.length - 1;
                while (remaining > 0L)
                {
                    buffer = buffers[current_buffer_index].rewind();
                    if (buffer.limit() > remaining) {
                        // We are on the case where the buffer has more remaining bytes than the position we want.
                        // So, adjust the buffer's position appropriately.
                        // Note also that the below value will always be in an (int) boundary.
                        buffer.position((int)(buffer.limit() - remaining));
                        // We need this buffer to be included; so, stop the loop.
                        break;
                    } else {
                        remaining -= buffer.limit();
                    }
                    current_buffer_index--;
                }
            } else {
                // Position is near the beginning of the stream, so do the positioning normally.
                remaining = new_position;
                current_buffer_index = 0;
                do {
                    buffer = buffers[current_buffer_index].rewind();
                    if (buffer.limit() > remaining) {
                        // We are on the case where the buffer has more remaining bytes than the position we want.
                        // So, adjust the buffer's position appropriately.
                        // Note also that the below value will always be in an (int) boundary.
                        buffer.position((int)remaining);
                        // We need this buffer to be included; so, stop the loop.
                        break;
                    } else {
                        remaining -= buffer.limit();
                    }
                    current_buffer_index--;
                } while (remaining > 0L);
                for (int I = Extensions.Max(current_buffer_index, 0); I < buffers.length; I++) { buffers[I].rewind(); }
            }
            long old_pos = position_absolute;
            position_absolute = new_position;
            return old_pos;
        }
    }

    @Override
    public long skip(long n)
    {
        if (n < 0L) { return 0L; }

        int rem;
        long skipped = 0;

        ByteBuffer buffer;

        while (n > 0L)
        {
            buffer = GetCurrentBuffer();
            if (buffer == null) { break; }
            rem = buffer.remaining();
            buffer.position(buffer.limit());
            n -= rem;
            skipped += rem;
        }

        position_absolute += skipped;
        return skipped;
    }

    @Override
    public int read()
            throws IOException
    {
        ByteBuffer buffer = GetCurrentBuffer();
        if (buffer == null) {
            return -1;
        } else {
            int value = buffer.get() & 0xFF;
            position_absolute++;
            return value;
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int read(@NotNull byte[] b) throws IOException { return read(b, 0, b.length); }

    @Override
    @SuppressWarnings("NullableProblems")
    public int read(@NotNull byte[] b, int offset, int length)
            throws IOException
    {
        ByteBuffer buffer;
        int rw = 0,
            rem_length = length,
            current_buffer_rem;
        do {
            buffer = GetCurrentBuffer();
            if (buffer == null)
            {
                if (rw > 0) {
                    position_absolute += rw;
                    return rw;
                } else {
                    return -1;
                }
            }
            current_buffer_rem = Extensions.Min(buffer.remaining(), rem_length);
            buffer.get(b, offset, current_buffer_rem);
            rw += current_buffer_rem;
            offset += current_buffer_rem;
            rem_length -= current_buffer_rem;
        } while (rw < length);
        position_absolute += rw;
        return rw;
    }

    /**
     * Returns an {@link IEnumerator} able to iterate through all the memory stream's bytes. <br />
     * Specifically, this method can be used to retrieve the data without modifying any position value.
     * @return A new instance of the {@link IEnumerator} interface that returns all the memory stream's bytes.
     */
    @Pure
    @Override
    public IByteEnumerator GetEnumerator() { return new Enumerator(this); }

    @NotNull
    @Override
    public String toString()
    {
        return String.format(
                "MemoryStream { Position = %d, Length = %d, NumberOfBuffers = %d }",
                position_absolute, length_absolute, buffers.length
        );
    }
}
