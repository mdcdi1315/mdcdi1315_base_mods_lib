package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.OutputStream;

/**
 * Provides static methods for de/encoding 7-bit integers from data streams and network buffers.
 */
public final class SevenBitEncodedInt
{
    private SevenBitEncodedInt() {}

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
            if (bits == 35) {
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
                if (bits == 35) {
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
