package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.OutputStream;

/**
 * Provides static methods for de/encoding 7-bit {@code long} integers from data streams and network buffers.
 * @since 1.0.35
 */
public final class SevenBitEncodedLong
{
    private SevenBitEncodedLong() {}

    /**
     * Writes a 7-bit encoded {@code long} integer to the specified data stream.
     * @param stream The data stream to write the 7-bit encoded integer to.
     * @param value The integer value to write as a 7-bit encoded integer.
     * @throws IOException An I/O exception was occurred.
     */
    public static void Write(OutputStream stream, long value)
            throws IOException
    {
        // Based off the VarLong.write method.
        while ((value & -128) != 0)
        {
            stream.write((int)(value & 127 | 128));
            value >>>= 7;
        }

        stream.write((int)value);
    }

    /**
     * Writes a 7-bit encoded {@code long} integer to the specified network byte buffer.
     * @param buffer The network byte buffer to write the 7-bit encoded integer to.
     * @param value The integer value to write as a 7-bit encoded {@code long} integer.
     */
    public static void Write(ByteBuf buffer, long value)
    {
        // Based off the VarLong.write method.
        while ((value & -128) != 0)
        {
            buffer.writeByte((int)(value & 127 | 128));
            value >>>= 7;
        }

        buffer.writeByte((int)value);
    }

    /**
     * Reads a 7-bit encoded {@code long} integer from the specified byte buffer.
     * @param buffer The byte buffer where to read the stored 7-bit encoded integer from.
     * @return The read 7-bit encoded {@code long} integer.
     * @throws DecoderException Detected unexpected end of the network buffer.
     * @throws FormatException Attempted to read more than 5 bytes from the {@link ByteBuf}.
     */
    public static long Read(ByteBuf buffer)
            throws DecoderException, FormatException
    {
        try {
            long value = 0;
            int bits = 0, g;
            do {
                if (bits == 70) {
                    throw new FormatException("Too many bytes of what should have been a 7-bit encoded Long.");
                } else {
                    g = buffer.readByte();
                    value |= (g & 0x7FL) << bits;
                    bits += 7;
                }
            } while ((g & 0x80) != 0);
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new DecoderException("Unexpected end of network buffer", e);
        }
    }

    /**
     * Reads a previously written 7-bit encoded {@code long} integer by using the {@link #Write(OutputStream, long)} method.
     * @param stream The data stream to read the previously stored encoded integer from.
     * @return The read 7-bit encoded {@code long} integer value.
     * @throws IOException An I/O exception was occurred.
     * @throws FormatException Too many bytes of what a 7-bit encoded integer should be.
     */
    public static long Read(InputStream stream)
            throws IOException, FormatException
    {
        long value = 0;
        int bits = 0, g;
        do {
            if (bits == 70) {
                throw new FormatException("Too many bytes of what should have been a 7-bit encoded Long.");
            } else if ((g = stream.read()) == -1) {
                throw new EOFException("Unexpected end of stream");
            } else {
                value |= (g & 0x7FL) << bits;
                bits += 7;
            }
        } while ((g & 0x80) != 0);
        return value;
    }
}
