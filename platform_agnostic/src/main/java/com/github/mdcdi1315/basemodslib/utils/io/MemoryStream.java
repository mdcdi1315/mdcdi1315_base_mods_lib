package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IByteEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IByteEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.BaseByteEnumerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * An {@link InputStream} implementation depending on {@link ByteBuffer}s to retain in-memory data. <br />
 * This class can only read bytes; to create this object you must use one of the dedicated constructors, <br />
 * or use the {@link MemoryStreamBuilder} class. <br />
 * This class does also implement the {@link IByteEnumerable} interface, for the purpose of retrieving the memory stream's
 * bytes without modifying any state of a given {@link MemoryStream} instance.
 * @apiNote Since BML 1.0.37, the {@link MemoryStream} class does also implement the {@link ReadableByteChannel} interface.
 * @since 1.0.35
 */
public class MemoryStream
    extends InputStream
    implements
        ReadableByteChannel,
        IByteEnumerable, ICloneable
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

    /**
     * Constructs a new instance of the {@link MemoryStream} class, from the specified traversable collection
     * of {@link ByteBuffer}s.
     * The collection will be treated as the data that the buffers contain are contiguous.
     * @param buffers The array of {@link ByteBuffer}s to specify.
     * @throws ArgumentNullException {@code buffers} is {@code null}.
     * @since 1.0.36
     */
    public MemoryStream(ITraversableCollection<ByteBuffer> buffers)
    {
        super();
        ArgumentNullException.ThrowIfNull(buffers, "buffers");
        int I = 0;
        length_absolute = 0L;
        position_absolute = 0L;
        current_buffer_index = -1;
        this.buffers = new ByteBuffer[buffers.GetCount()];
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

    private MemoryStream(ByteBuffer[] buffers, long length_known)
    {
        super();
        position_absolute = 0L;
        this.buffers = buffers;
        current_buffer_index = -1;
        length_absolute = length_known;
    }

    private static final class Enumerator
            extends BaseByteEnumerator
    {
        private int index;
        private byte current_element;
        private ByteBuffer[] buffers;

        @Pure
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

        @Pure
        @Override
        protected boolean MoveNextImpl()
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
    private ByteBuffer GetCurrentBuffer(int number_of_bytes_to_get)
    {
        if (Math.addExact(position_absolute, number_of_bytes_to_get) > length_absolute) {
            return null;
        } else {
            ByteBuffer buffer;
            if (current_buffer_index < 0) { current_buffer_index = 0; }
            do {
                buffer = buffers[current_buffer_index];
            } while (buffer.remaining() == 0 && ++current_buffer_index < buffers.length);
            return (current_buffer_index < buffers.length) ? buffer : null;
        }
    }

    /**
     * Gets a value whether this memory stream is still open, which it will always be,
     * so this does always return {@code true}.
     * @return Always {@code true}.
     */
    @Override
    public boolean isOpen() { return true; }

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
        } else if (new_position == position_absolute) {
            // If positioning to the same position as the stored value, we
            // do nothing, and we return the position value itself.
            return new_position;
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
                boolean current_buffer_index_is_used = false;
                do {
                    buffer = buffers[current_buffer_index].rewind();
                    if (buffer.limit() > remaining) {
                        // We are on the case where the buffer has more remaining bytes than the position we want.
                        // So, adjust the buffer's position appropriately.
                        // Note also that the below value will always be in an (int) boundary.
                        buffer.position((int)remaining);
                        // We need this buffer to be included; so, stop the loop.
                        current_buffer_index_is_used = true;
                        break;
                    } else {
                        remaining -= buffer.limit();
                    }
                    current_buffer_index++;
                } while (remaining > 0L);
                // Rewind all the remaining buffers, if so required.
                for (
                   int I = current_buffer_index_is_used ? current_buffer_index + 1 : current_buffer_index;
                   I < buffers.length;
                   I++
                ) { buffers[I].rewind(); }
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
            buffer = GetCurrentBuffer(0);
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
        ByteBuffer buffer = GetCurrentBuffer(1);
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
        int read_bytes = 0, read_pass;
        boolean stopped_due_to_stream_end = false;

        while (length > 0)
        {
            buffer = GetCurrentBuffer(length);
            if (buffer == null) {
                stopped_due_to_stream_end = true;
                break;
            } else {
                read_pass = Extensions.Min(buffer.remaining(), length);
                buffer.get(b, offset, read_pass);
                read_bytes += read_pass;
                offset += read_pass;
                length -= read_pass;
            }
        }

        position_absolute += read_bytes;
        return stopped_due_to_stream_end ? ((read_bytes > 0) ? read_bytes : -1) : read_bytes;
    }

    @Override
    public synchronized int read(ByteBuffer dst)
            throws IOException
    {
        ByteBuffer buffer;
        int read_bytes = 0;
        int buffer_pos, read_pass;
        int remaining = dst.remaining();
        int dst_position = dst.position();
        boolean stopped_due_to_stream_end = false;

        while (remaining > 0)
        {
            buffer = GetCurrentBuffer(remaining);
            if (buffer == null) {
                stopped_due_to_stream_end = true;
                break;
            } else {
                buffer_pos = buffer.position();
                read_pass = Extensions.Min(buffer.remaining(), remaining);
                dst.put(dst_position + read_bytes, buffer, buffer_pos, read_pass);
                buffer.position(buffer_pos + read_pass);
                remaining -= read_pass;
                read_bytes += read_pass;
            }
        }

        position_absolute += read_bytes;
        dst.position(dst_position + read_bytes);
        return stopped_due_to_stream_end ? ((read_bytes > 0) ? read_bytes : -1) : read_bytes;
    }

    @Override
    public long transferTo(OutputStream out)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(out, "out");
        ByteBuffer bb;
        int now_bytes_to_copy;
        long bytes_copied = 0L;
        // Initialize a temporary buffer to do our copy logic...
        byte[] temp_buffer = new byte[ByteBufferUtils.DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE];
        while ((bb = GetCurrentBuffer(0)) != null)
        {
            now_bytes_to_copy = bb.remaining();
            // If the temporary buffer size is too small,
            // create a new one of the required bytes from the byte buffer.
            if (now_bytes_to_copy > temp_buffer.length) {
                temp_buffer = new byte[now_bytes_to_copy];
            }
            bb.get(temp_buffer, 0, now_bytes_to_copy);
            out.write(temp_buffer, 0, now_bytes_to_copy);
            bytes_copied += now_bytes_to_copy;
        }
        position_absolute += bytes_copied;
        return bytes_copied;
    }

    /**
     * Gets a portion of this memory stream as a new {@link MemoryStream} object.
     * @param position The absolute position in the current stream where copy begins.
     * @param length The desired absolute length, in bytes, of the new memory stream.
     * @return A new {@link MemoryStream} instance, having only the specified portion of bytes.
     * @throws ArgumentException {@code position + length} is greater than {@link #GetLength()}.
     * @throws ArgumentOutOfRangeException {@code position} and/or {@code length} are negative values.
     * @since 1.0.36
     */
    @NotNull
    public MemoryStream Slice(long position, long length)
            throws ArgumentOutOfRangeException, ArgumentException
    {
        if (position < 0L) {
            throw new ArgumentOutOfRangeException("position", "Position cannot be a negative number");
        } else if (length < 0L) {
            throw new ArgumentOutOfRangeException("length", "Length cannot be a negative number");
        } else if (Math.addExact(position, length) > this.length_absolute) {
            throw new ArgumentException("The specified position and length values do exceed the stream's length.");
        } else if (length == 0L) {
            return new MemoryStream();
        } else {
            ByteBuffer temp;
            long remaining_bytes = length;
            long old_position = this.SetPosition(position);
            try {
                int first_buffer_index = this.current_buffer_index, limit;
                ByteBuffer[] buffers = new ByteBuffer[this.buffers.length - first_buffer_index];
                buffers[0] = this.buffers[first_buffer_index].slice();
                for (int I = first_buffer_index + 1; I < this.buffers.length; I++)
                {
                    temp = this.buffers[I].asReadOnlyBuffer().rewind();
                    limit = temp.limit();
                    if (limit > remaining_bytes) { temp.limit((int)remaining_bytes); }
                    remaining_bytes -= limit;
                    buffers[I] = temp;
                }
                return new MemoryStream(buffers, length);
            } finally {
                this.SetPosition(old_position);
            }
        }
    }

    /**
     * Gets a portion of this memory stream as a new {@link MemoryStream} object, that
     * starts copying bytes from the beginning of the stream.
     * @param length The desired absolute length, in bytes, of the new memory stream.
     * @return A new {@link MemoryStream} instance, having only the specified portion of bytes.
     * @throws ArgumentException {@code length} is greater than {@link #GetLength()}.
     * @throws ArgumentOutOfRangeException {@code length} is a negative value.
     * @since 1.0.36
     */
    @NotNull
    public MemoryStream Slice(long length) throws ArgumentOutOfRangeException, ArgumentException { return Slice(0L, length); }

    /**
     * Returns a new instance of the {@link MemoryStream} class that
     * references the same data, but with an independent seek pointer.
     * @return A new, cloned instance of the current {@link MemoryStream} object.
     * @since 1.0.36
     */
    @NotNull
    public MemoryStream Clone()
    {
        ByteBuffer[] buffers = new ByteBuffer[this.buffers.length];
        for (int I = 0; I < this.buffers.length; I++)
        {
            buffers[I] = this.buffers[I].asReadOnlyBuffer().rewind();
        }
        return new MemoryStream(buffers, this.length_absolute);
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
