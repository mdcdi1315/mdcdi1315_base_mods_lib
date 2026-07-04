package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.OutputStream;

/**
 * Provides utilities around the {@link ByteBuffer} class.
 * @since 1.0.26
 */
public final class ByteBufferUtils
{
    private ByteBufferUtils() {}

    /**
     * Provides a constant that is the default recommended buffer size for buffer copy operations.
     */
    public static final int DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE = 2048;

    /**
     * Writes a {@link ByteBuffer} to the specified {@link OutputStream}.
     * @param os The {@link OutputStream} to write the {@link ByteBuffer} data to.
     * @param buffer The {@link ByteBuffer} containing the data to write to the stream.
     * @return The number of bytes actually written to the stream.
     * @throws IOException An I/O exception was occurred.
     * @implNote This implementation respects the input byte buffer so it's original position
     * is restored when the method returns. Even on hard I/O failure the position is restored. <br />
     * Additionally, if the buffer has a backing array and is not a read-only buffer, it directly
     * accesses that array and performs the write operation on-the-fly.
     * @apiNote This API now forwards to {@link StreamUtils#WriteBuffer(OutputStream, ByteBuffer)}
     * and returns the value of the {@link ByteBuffer#remaining()} method upon return.
     */
    public static int WriteToStream(OutputStream os, ByteBuffer buffer)
            throws IOException
    {
        StreamUtils.WriteBuffer(os, buffer);
        return buffer.remaining();
    }

    /**
     * Reads data from the specified {@link InputStream} and returns them to a newly created {@link ByteBuffer}.
     * @param is The {@link InputStream} to read data from.
     * @param number_of_bytes Number of bytes to read from the {@link InputStream}.
     * @return The contents of reading {@code number_of_bytes} bytes. <br />
     *         Written contents may be less than {@code number_of_bytes}. <br />
     *         To find out how many bytes were actually read from the input stream,
     *         invoke the {@link ByteBuffer#limit()} method on the returned value.
     * @throws IOException An I/O exception was occurred.
     * @throws ArgumentOutOfRangeException {@code number_of_bytes} is a negative value.
     * @apiNote Since 1.0.35, it is more preferable to use the {@link StreamUtils#ReadAsBuffer(InputStream, int)}
     * method, as that exposes the stream ended case, which for it returns {@code null}.
     */
    @NotNull
    public static ByteBuffer ReadFromStream(InputStream is, int number_of_bytes)
            throws IOException, ArgumentOutOfRangeException
    {
        if (number_of_bytes < 0) {
            throw new ArgumentOutOfRangeException("number_of_bytes", "Number of bytes to read cannot be less than zero!");
        } else {
            byte[] buf = new byte[number_of_bytes];
            int read = is.read(buf, 0, number_of_bytes);
            return ByteBuffer.wrap(buf, 0, Extensions.Max(read, 0));
        }
    }

    /**
     * Safely slices the input {@code source} buffer by copying the data into a new {@link ByteBuffer}, and returning that back to the caller. <br />
     * This differs from the {@link ByteBuffer#slice(int, int)} method, which it does return a view {@link ByteBuffer} of the source one. <br />
     * Note also that the method does the slicing based on the {@linkplain ByteBuffer#position() current position of the byte buffer},
     * and not regardless of its value.
     * @param source The source {@linkplain ByteBuffer byte buffer} to perform the slice on.
     * @param start_index The starting index to additionally apply on the current position of the {@linkplain ByteBuffer byte buffer}.
     * @param length The number of elements to copy from the source {@linkplain ByteBuffer byte buffer}.
     * @return The result of slicing {@code source} by {@code start_index} and {@code length}.
     * @throws ArgumentNullException {@code source} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code start_index} and/or {@code length} are negative values. <br />
     * -or- <br />
     * {@code start_index} is greater than the buffer's length.
     * -or- <br />
     * {@code length} is greater than the buffer's length.
     */
    @NotNull
    public static ByteBuffer SafeSlice(ByteBuffer source, int start_index, int length)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(source, "source");
        if (start_index < 0) {
            throw new ArgumentOutOfRangeException("start_index", "Starting index cannot be a negative value");
        } else if (length < 0) {
            throw new ArgumentOutOfRangeException("length", "New buffer length cannot be a negative value");
        } else {
            int src_position = source.position();
            try {
                source.position(src_position + start_index);
                if (source.remaining() < length) {
                    throw new ArgumentOutOfRangeException("length", "Length must be less than or equal to the source buffer's length.");
                } else {
                    byte[] temp_buf = new byte[DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE];
                    ByteBuffer result = ByteBuffer.allocateDirect(length);

                    int copied = 0, cp;
                    while (copied < length)
                    {
                        cp = Extensions.ComputeStreamBufferSize(copied, length, temp_buf.length);
                        source.get(temp_buf, 0, cp);
                        result.put(temp_buf, 0, cp);
                        copied += cp;
                    }
                    // Result buffer: clear the buffer position and assign the limit.
                    return result.rewind().limit(copied);
                }
            } catch (IllegalArgumentException iae) {
                throw new ArgumentOutOfRangeException("start_index", "Start index must be less than the byte buffer's length.");
            } finally {
                // Restore position once the try block completes execution.
                source.position(src_position);
            }
        }
    }

    /**
     * Creates a copy of the {@code input} buffer, but doing any changes to the
     * returned buffer does not propagate those changes back to the {@code input} buffer.
     * @param input The input buffer to copy data from. The method respects the value of the {@link ByteBuffer#position()} method.
     * @return A new {@link ByteBuffer} that contains the data that the {@code input} buffer contains.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    @NotNull
    public static ByteBuffer DetachedClone(ByteBuffer input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        int length = input.remaining(), src_position = input.position(), copied = 0, cp;
        byte[] temp_buf = new byte[DEFAULT_RECOMMENDED_COPY_BUFFER_SIZE];
        ByteBuffer result = ByteBuffer.allocateDirect(length);

        try {
            while (copied < length)
            {
                cp = Extensions.ComputeStreamBufferSize(copied, length, temp_buf.length);
                input.get(temp_buf, 0, cp);
                result.put(temp_buf, 0, cp);
                copied += cp;
            }

            result.rewind();
            result.limit(copied);
            return result;
        } finally {
            input.position(src_position);
        }
    }
}
