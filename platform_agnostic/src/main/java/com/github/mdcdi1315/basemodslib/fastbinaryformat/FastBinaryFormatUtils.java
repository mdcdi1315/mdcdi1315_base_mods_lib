package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.io.SevenBitEncodedInt;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utility methods used around the Fast Binary Format subsystem.
 */
final class FastBinaryFormatUtils
{
    private FastBinaryFormatUtils() {}

    public static void ThrowEOF() throws IOException { throw new EOFException("Unexpected end of stream"); }

    public static void ThrowEOFIf(boolean condition) throws IOException { if (condition) { ThrowEOF(); } }

    public static void WriteString7BitEncodedLength(OutputStream stream, String string, Charset set)
            throws IOException
    {
        ByteBuffer bb = set.encode(string);
        bb.position(0);
        SevenBitEncodedInt.Write(stream, bb.remaining());
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
    public static ByteBuffer ReadBytes(InputStream stream, int n_bytes_to_read)
            throws IOException
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[n_bytes_to_read]);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int read = 0, r;
        do {
            ThrowEOFIf((r = stream.read(bb.array(), read, n_bytes_to_read - read)) == -1);
            read += r;
        } while (read < n_bytes_to_read);
        bb.rewind();
        return bb;
    }

    @NotNull
    public static BinaryFormatEntry ConstructEntryFromType(BinaryFormatEntryType type)
            throws IOException
    {
        return switch (type.GetEntryCode()) {
            case BinaryFormatEntryType.NULL_ENTRY_CODE -> NullBinaryFormatEntry.INSTANCE;
            case BinaryFormatEntryType.OBJECT_ENTRY_CODE -> new ObjectBinaryFormatEntry();
            case BinaryFormatEntryType.ARRAY_ENTRY_CODE -> new ArrayBinaryFormatEntry();
            case BinaryFormatEntryType.BYTE_ENTRY_CODE -> new ByteBinaryFormatEntry(0);
            case BinaryFormatEntryType.SHORT_ENTRY_CODE -> new ShortBinaryFormatEntry(0);
            case BinaryFormatEntryType.INT_ENTRY_CODE -> new IntBinaryFormatEntry(0);
            case BinaryFormatEntryType.LONG_ENTRY_CODE -> new LongBinaryFormatEntry(0L);
            case BinaryFormatEntryType.FLOAT_ENTRY_CODE -> new FloatBinaryFormatEntry(0F);
            case BinaryFormatEntryType.DOUBLE_ENTRY_CODE -> new DoubleBinaryFormatEntry(0D);
            case BinaryFormatEntryType.BOOLEAN_ENTRY_CODE -> new BooleanBinaryFormatEntry(false);
            case BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT_ENTRY_CODE -> new SevenBitEncodedIntBinaryFormatEntry(0);
            case BinaryFormatEntryType.STRING_ENTRY_CODE -> CreateStringEntry(type.GetStringEncoding());
            case BinaryFormatEntryType.FIXED_ARRAY_ENTRY_CODE -> CreateFixedArrayEntry(type.GetEntryData());
            default -> throw new IOException("Do not know how to decode type " + type.GetEntryCode());
        };
    }

    @NotNull
    private static BaseStringBinaryFormatEntry CreateStringEntry(StringEncoding encoding)
    {
        if (encoding == StringEncoding.ASCII) {
            return new ASCIIStringBinaryFormatEntry(StringUtils.Empty);
        } else if (encoding == StringEncoding.UTF16_LE) {
            return new UTF16LEStringBinaryFormatEntry(StringUtils.Empty);
        } else if (encoding == StringEncoding.UTF16_BE) {
            return new UTF16BEStringBinaryFormatEntry(StringUtils.Empty);
        } else {
            throw new FormatException("Unexpected encoding " + encoding);
        }
    }

    @NotNull
    private static BaseFixedArrayBinaryFormatEntry CreateFixedArrayEntry(int element_code)
    {
        if (element_code == BinaryFormatEntryType.BYTE_ENTRY_CODE) {
            return new ByteFixedArrayBinaryFormatEntry();
        } else if (element_code == BinaryFormatEntryType.SHORT_ENTRY_CODE) {
            return new ShortFixedArrayBinaryFormatEntry();
        } else if (element_code == BinaryFormatEntryType.INT_ENTRY_CODE) {
            return new IntFixedArrayBinaryFormatEntry();
        } else if (element_code == BinaryFormatEntryType.LONG_ENTRY_CODE) {
            return new LongFixedArrayBinaryFormatEntry();
        } else if (element_code == BinaryFormatEntryType.FLOAT_ENTRY_CODE) {
            return new FloatFixedArrayBinaryFormatEntry();
        } else if (element_code == BinaryFormatEntryType.DOUBLE_ENTRY_CODE) {
            return new DoubleFixedArrayBinaryFormatEntry();
        } else {
            throw new InvalidOperationException("Unknown fixed array element code " + element_code);
        }
    }


}
