package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;

/**
 * Provides the class for building {@link MemoryStream} instances. <br />
 * This class does also extend from the {@link OutputStream} class.
 * @since 1.0.35
 */
public final class MemoryStreamBuilder
    extends OutputStream
{
    private final SingleLinkedList<ByteBuffer> buffers;

    /**
     * Provides the constant value that is the minimum allowed buffer
     * size for creating buffers from a given {@link InputStream}.
     */
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 4096;

    /**
     * Initializes a new and empty instance of the {@link MemoryStreamBuilder} class.
     */
    public MemoryStreamBuilder() { buffers = new SingleLinkedList<>(); }

    @Override
    public void write(int b) throws IOException { buffers.Add(ByteBuffer.allocate(1).put((byte)b).rewind().asReadOnlyBuffer()); }

    // Prevent the data to be changed, by allocating a buffer, then
    // copying the current data to the allocated byte buffer.
    @Override
    public void write(@NotNull byte[] b) throws IOException { buffers.Add(ByteBuffer.allocate(b.length).put(b).rewind().asReadOnlyBuffer()); }

    // Prevent the data to be changed, by allocating a buffer, then
    // copying the current data to the allocated byte buffer.
    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException { buffers.Add(ByteBuffer.allocate(b.length).put(b, off, len).rewind().asReadOnlyBuffer()); }

    /**
     * Writes all the data from the specified input stream.
     * @param stream The data stream to copy data to this builder.
     * @param buffer_copy_window The maximum size of each buffer that will be created.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @throws IOException {@link InputStream#read(byte[])} had an I/O error.
     * @throws ArgumentOutOfRangeException {@code buffer_copy_window} is less than 4096.
     */
    public void WriteFromStream(InputStream stream, int buffer_copy_window)
        throws ArgumentNullException, ArgumentOutOfRangeException, IOException
    {
        ArgumentNullException.ThrowIfNull(stream, "stream");
        if (buffer_copy_window < DEFAULT_STREAM_BUFFER_SIZE) {
            throw new ArgumentOutOfRangeException("buffer_copy_window", "Buffer copy window cannot be less than 4096.");
        } else {
            int read;
            byte[] data = new byte[buffer_copy_window];
            while ((read = stream.read(data)) > -1)
            {
                buffers.Add(ByteBuffer.allocate(read).put(data, 0, read).asReadOnlyBuffer());
            }
        }
    }

    /**
     * Writes all the data from the specified input stream. <br />
     * Each constructed buffer will be of size
     * @param stream The data stream to copy data to this builder.
     * @throws ArgumentNullException {@code stream} is {@code null}.
     * @throws IOException {@link InputStream#read(byte[])} had an I/O error.
     */
    public void WriteFromStream(InputStream stream) throws ArgumentNullException, IOException { WriteFromStream(stream, DEFAULT_STREAM_BUFFER_SIZE); }

    /**
     * Constructs a copy of the specified buffer and adds it to the builder.
     * @param buffer The {@link ByteBuffer} to copy it's remaining data to the builder.
     * @throws ArgumentNullException {@code builder} is {@code null}.
     */
    public void WriteBuffer(ByteBuffer buffer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        int previous_position = buffer.position();
        try {
            buffers.Add(ByteBuffer.allocate(buffer.remaining()).put(buffer).rewind());
        } finally {
            buffer.position(previous_position);
        }
    }

    /**
     * Creates a buffer from the specified array, creates a {@link ByteBuffer}
     * for it, and adds the created buffer to the builder.
     * @param array The byte array to copy data from.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public void WriteArray(byte[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        buffers.Add(ByteBuffer.allocate(array.length).put(array).rewind());
    }

    /**
     * Creates a buffer from the specified portion of the array, creates a {@link ByteBuffer}
     * for it, and adds the created buffer to the builder.
     * @param array The byte array to copy data from.
     * @param offset The offset to begin copying data from.
     * @param count The number of elements to copy into the buffer.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code offset + count} is out of bounds of the array.
     * @throws ArgumentOutOfRangeException {@code offset} and/or {@code count} is less than 0.
     */
    public void WriteArray(byte[] array, int offset, int count)
            throws ArgumentException, ArgumentNullException, ArgumentOutOfRangeException
    {
        if (offset < 0) {
            throw new ArgumentOutOfRangeException("offset", "offset cannot be less than 0");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "count cannot be less than 0");
        } else {
            try {
                buffers.Add(ByteBuffer.allocate(count).put(array, offset, count).rewind());
            } catch (IndexOutOfBoundsException e) {
                throw new ArgumentException("The current combination of count + offset parameters is out of bounds of the input array.");
            }
        }
    }

    /**
     * Removes all the added buffers added so far.
     * @apiNote After this method returns, the builder will not have any data.
     */
    public void DropBuffers() { buffers.Clear(); }

    /**
     * Builds the {@link MemoryStream} from the currently specified data.
     * @return The built memory stream.
     */
    @NotNull
    public MemoryStream Build() { return new MemoryStream((ITraversableCollection<ByteBuffer>) buffers); }

    @NotNull
    @Override
    public String toString() { return String.format("MemoryStreamBuilder { NumberOfBuffers = %d }", buffers.GetCount()); }
}
