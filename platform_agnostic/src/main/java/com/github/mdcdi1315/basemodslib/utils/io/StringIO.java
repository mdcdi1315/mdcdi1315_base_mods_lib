package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.io.OutputStream;
import java.nio.charset.CoderResult;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;

/**
 * Provides a way for reading and writing strings from/to data streams. <br />
 * String reading and writing is done using buffered methods, so that
 * to reduce the memory footprint during these operations. <br />
 * However, any encoding having both a {@link CharsetEncoder} and a {@link CharsetDecoder}
 * can be directly written to a stream by using the provided methods.
 */
public final class StringIO
{
    private StringIO() {}

    private static int ComputeBufferSize(long consumed, long total, int buffer_size) { return ((consumed + buffer_size) < total) ? buffer_size : (int)(total - consumed); }

    /**
     * Writes the specified character sequence to the specified stream and returns the number of bytes that the sequence occupies in the data stream space.
     * @param stream The data stream to write the string to.
     * @param encoder The {@link CharsetEncoder} object to use for transforming the character sequence into bytes.
     * @param string The character sequence to encode.
     * @return The number of bytes written to {@code stream}.
     * @throws IOException An I/O exception occurred.
     */
    public static long WriteString(OutputStream stream, CharsetEncoder encoder, CharSequence string)
            throws IOException
    {
        int buf_written;
        long written = 0L;
        CoderResult cr;
        CharBuffer cb = CharBuffer.wrap(string);
        ByteBuffer bb = ByteBuffer.wrap(new byte[1024]);
        do {
            bb.rewind();
            cr = encoder.encode(cb, bb, true);
            stream.write(bb.array(), 0, buf_written = bb.position());
            written += buf_written;
        } while (cr.isOverflow());
        if (cr.isError()) { cr.throwException(); }

        do {
            bb.rewind();
            cr = encoder.flush(bb);
            stream.write(bb.array(), 0, buf_written = bb.position());
            written += buf_written;
        } while (cr.isOverflow());
        if (cr.isError()) { cr.throwException(); }
        return written;
    }

    /**
     * Reads the previously encoded character sequence as a {@link String}.
     * @param stream The data stream to read the string from.
     * @param decoder The {@link CharsetDecoder} object to use for transforming the stream's bytes into a string.
     * @param bytes The number of bytes of the string, returned from the {@link #WriteString(OutputStream, CharsetEncoder, CharSequence)} return value.
     * @return The decoded string contents contained in {@code stream}.
     * @throws IOException An I/O exception occurred.
     */
    @NotNull
    public static String ReadString(InputStream stream, CharsetDecoder decoder, long bytes)
            throws IOException
    {
        int read;
        long total_read = 0;

        byte[] temp = new byte[1024];
        CoderResult cr;

        // Allocate necessary buffers
        CharBuffer buffer = CharBuffer.allocate(Extensions.Ceiling(decoder.maxCharsPerByte() * temp.length));
        StringBuilder string_builder = new StringBuilder(Extensions.Floor(decoder.averageCharsPerByte() * bytes));

        // Read loop
        while (total_read < bytes)
        {
            // Read bytes...
            read = stream.read(temp, 0, ComputeBufferSize(total_read, bytes, temp.length));
            if (read > -1) { total_read += read; }
            // Then wrap them into a buffer...
            ByteBuffer bb = ByteBuffer.wrap(temp, 0, read);
            // Decode...
            do {
                buffer.rewind();
                buffer.limit(buffer.capacity());
                cr = decoder.decode(bb, buffer, total_read >= bytes);
                buffer.limit(buffer.position());
                buffer.rewind();
                string_builder.append(buffer);
            } while (cr.isOverflow());
            // Throw exception if we have an error.
            if (cr.isError()) { cr.throwException(); }
            if (read == -1) { break; }
        }
        // Final flush as instructed by Java API
        do {
            buffer.rewind();
            buffer.limit(buffer.capacity());
            cr = decoder.flush(buffer);
            buffer.limit(buffer.position());
            buffer.rewind();
            string_builder.append(buffer);
        } while (cr.isOverflow());
        // Get value, and we are done.
        return string_builder.toString();
    }
}
