package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Provides static methods for de/encoding 7-bit integers from data streams and network buffers.
 */
public final class SevenBitEncodedInt
{
    private SevenBitEncodedInt() {}

    /**
     * Maximum number of numeric-only bits that a 7-bit encoded integer can occupy.
     * @since 1.0.37
     */
    public static final int MAX_SIZE_IN_BITS = 35;

    /**
     * Maximum number of bytes that a 7-bit encoded integer can occupy.
     * @since 1.0.37
     */
    public static final int MAX_SIZE_IN_BYTES = MAX_SIZE_IN_BITS / 7;

    /**
     * Writes a 7-bit encoded integer to the specified data stream.
     * @param stream The data stream to write the 7-bit encoded integer to.
     * @param value The integer value to write as a 7-bit encoded integer.
     * @throws IOException An I/O exception was occurred.
     */
    public static void Write(OutputStream stream, int value)
            throws IOException
    {
        long num;
        for (num = (value & 0xFFFFFFFFL); num >= 0x7FL; num >>= 7L) {
            stream.write((int)((num | 0x80L) & 0xFFL));
        }
        stream.write((int) num);
    }

    /**
     * Writes a 7-bit encoded integer to the specified data stream.
     * @param channel The data stream to write the 7-bit encoded integer to.
     * @param value The integer value to write as a 7-bit encoded integer.
     * @throws IOException An I/O exception was occurred.
     * @since 1.0.37
     */
    public static void Write(WritableByteChannel channel, int value)
            throws IOException
    {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_SIZE_IN_BYTES);
        long num;
        for (num = (value & 0xFFFFFFFFL); num >= 0x7FL; num >>= 7L) {
            buffer.put((byte)((num | 0x80L) & 0xFFL));
        }
        buffer.put((byte) num);
        StreamUtils.WriteBufferEnsured(channel, buffer.limit(buffer.position()).rewind());
    }

    /**
     * Writes a 7-bit encoded integer to the specified network byte buffer.
     * @param buffer The network byte buffer to write the 7-bit encoded integer to.
     * @param value The integer value to write as a 7-bit encoded integer.
     */
    public static void Write(ByteBuf buffer, int value)
    {
        long num;
        for (num = (value & 0xFFFFFFFFL); num >= 0x7FL; num >>= 7L) {
            buffer.writeByte((int)((num | 0x80L) & 0xFFL));
        }
        buffer.writeByte((int) num);
    }

    /**
     * Reads a previously written 7-bit encoded integer by using the {@link #Write(OutputStream, int)} method.
     * @param stream The data stream to read the previously stored encoded integer from.
     * @return The read 7-bit encoded integer value.
     * @throws IOException An I/O exception was occurred.
     * @throws FormatException Too many bytes of what a 7-bit encoded integer should be.
     */
    public static int Read(InputStream stream)
            throws IOException, FormatException
    {
        int value = 0, bits = 0, g;
        do {
            if (bits == MAX_SIZE_IN_BITS) {
                throw new FormatException("Too many bytes of what should have been a 7-bit encoded Integer.");
            } else if ((g = stream.read()) == -1) {
                throw new EOFException("Unexpected end of stream");
            } else {
                value |= (g & 0x7F) << bits;
                bits += 7;
            }
        } while ((g & 0x80) != 0);
        return value;
    }

    /**
     * Reads a previously written 7-bit encoded integer by using the {@link #Write(WritableByteChannel, int)} method.
     * @param channel The data stream to read the previously stored encoded integer from.
     * @return The read 7-bit encoded integer value.
     * @throws IOException An I/O exception was occurred.
     * @throws FormatException Too many bytes of what a 7-bit encoded integer should be.
     * @since 1.0.37
     */
    public static int Read(ReadableByteChannel channel)
        throws IOException, FormatException
    {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        int value = 0, bits = 0, g;
        do {
            if (bits == MAX_SIZE_IN_BITS) {
                throw new FormatException("Too many bytes of what should have been a 7-bit encoded Integer.");
            } else if (channel.read(buffer.rewind()) == -1) {
                throw new EOFException("Unexpected end of stream");
            } else {
                g = buffer.rewind().get();
                value |= (g & 0x7F) << bits;
                bits += 7;
            }
        } while ((g & 0x80) != 0);
        return value;
    }

    /**
     * Reads a 7-bit encoded integer from the specified byte buffer.
     * @param buffer The byte buffer where to read the stored 7-bit encoded integer from.
     * @return The read 7-bit encoded integer.
     * @throws DecoderException Detected unexpected end of the network buffer.
     * @throws FormatException Attempted to read more than 5 bytes from the {@link ByteBuf}.
     */
    public static int Read(ByteBuf buffer)
            throws DecoderException, FormatException
    {
        try {
            int value = 0, bits = 0, g;
            do {
                if (bits == MAX_SIZE_IN_BITS) {
                    throw new FormatException("Too many bytes of what should have been a 7-bit encoded Integer.");
                } else {
                    g = buffer.readByte();
                    value |= (g & 0x7F) << bits;
                    bits += 7;
                }
            } while ((g & 0x80) != 0);
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new DecoderException("Unexpected end of network buffer", e);
        }
    }
}
