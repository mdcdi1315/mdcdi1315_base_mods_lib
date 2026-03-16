package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility methods used around the Fast Binary Format subsystem.
 */
final class FastBinaryFormatUtils
{
    private FastBinaryFormatUtils() {}

    public static void Write7BitEncodedInt(OutputStream stream, int value)
            throws IOException
    {
        long num;
        for (num = (value & 0xFFFFFFFFL); num >= 0x7FL; num >>= 7L) {
            stream.write((int)((num | 0x80L) & 0xFFL));
        }
        stream.write((int) num);
    }

    public static int Read7BitEncodedInt(InputStream stream)
            throws IOException, FormatException
    {
        int value = 0, bits = 0, g;
        byte b;
        do {
            if (bits == 35) {
                throw new FormatException("Too many bytes of what should have been a 7-bit encoded Integer.");
            }
            g = stream.read();
            if (g == -1) { throw new IOException("End of stream reached"); } else { b = (byte)g; }
            value |= (b & 0x7F) << bits;
            bits += 7;
        } while ((b & 0x80) != 0);
        return value;
    }

    private static int ComputeBufferSize(int consumed, int total, int buffer_size) { return ((consumed + buffer_size) < total) ? buffer_size : (total - consumed); }

    @NotNull
    public static String ReadString(InputStream stream, CharsetDecoder decoder, int bytes)
            throws IOException
    {
        int read, total_read = 0;
        byte[] temp = new byte[1024];
        CoderResult cr;
        // Allocate necessary buffers
        StringBuilder string_builder = new StringBuilder();
        CharBuffer buffer = CharBuffer.allocate(Extensions.Ceiling(decoder.maxCharsPerByte() * temp.length));
        // Read loop
        while (total_read < bytes)
        {
            // Read bytes...
            read = stream.read(temp, 0, ComputeBufferSize(total_read, bytes, temp.length));
            if (read > -1) { total_read += read; } else { break; }
            // Then wrap them into a buffer...
            ByteBuffer bb = ByteBuffer.wrap(temp, 0, read);
            // Decode...
            do {
                buffer.position(0);
                buffer.limit(buffer.capacity());
                cr = decoder.decode(bb, buffer, total_read >= bytes);
                buffer.limit(buffer.position());
                buffer.position(0);
                string_builder.append(buffer);
            } while (cr.isOverflow());
            // Throw exception if we have an error.
            if (cr.isError()) { cr.throwException(); }
        }
        // Final flush as instructed by Java API
        do {
            buffer.position(0);
            buffer.limit(buffer.capacity());
            cr = decoder.flush(buffer);
            buffer.limit(buffer.position());
            buffer.position(0);
            string_builder.append(buffer);
        } while (cr.isOverflow());
        // Get value, and we are done.
        return string_builder.toString();
    }

    public static void WriteString7BitEncodedLength(OutputStream stream, String string, Charset set)
            throws IOException
    {
        ByteBuffer bb = set.encode(string);
        bb.position(0);
        Write7BitEncodedInt(stream, bb.remaining());
        byte[] temp = new byte[1024];
        int rem;
        while ((rem = bb.remaining()) > 1024) {
            bb.get(temp);
            stream.write(temp);
        }
        if (rem > 0) {
            temp = new byte[rem];
            bb.get(temp);
            stream.write(temp, 0, rem);
        }
    }

    public static void WriteFieldNameString(OutputStream stream, String string)
            throws IOException
    {
        ByteBuffer bb = StandardCharsets.US_ASCII.encode(string);
        bb.position(0);
        if (bb.remaining() > 255) {
            throw new IOException("Too many bytes of what should have been a field name. Maximum allowed value is 255.");
        } else {
            stream.write(bb.remaining());
            byte[] temp = new byte[1024];
            int rem;
            while ((rem = bb.remaining()) > 1024) {
                bb.get(temp);
                stream.write(temp);
            }
            if (rem > 0) {
                temp = new byte[rem];
                bb.get(temp);
                stream.write(temp, 0, rem);
            }
        }
    }

    @NotNull
    public static byte[] ReadBytes(InputStream stream, int n_bytes_to_read)
            throws IOException
    {
        byte[] bytes = new byte[n_bytes_to_read];
        int read = 0, r;
        do {
            r = stream.read(bytes, read, n_bytes_to_read - read);
            if (r == -1) {
                throw new IOException("Unexpected end of stream");
            } else {
                read += r;
            }
        } while (read < n_bytes_to_read);
        return bytes;
    }
}
