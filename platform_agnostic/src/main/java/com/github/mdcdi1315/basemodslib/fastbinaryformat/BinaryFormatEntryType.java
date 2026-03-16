package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Tuple2;

import java.io.IOException;

public final class BinaryFormatEntryType
{
    public static final int BYTE_ENTRY_CODE = 0x00;
    public static final int SHORT_ENTRY_CODE = 0x01;
    public static final int INT_ENTRY_CODE = 0x02;
    public static final int SEVEN_BIT_ENCODED_INT_ENTRY_CODE = 0x03;
    public static final int LONG_ENTRY_CODE = 0x04;
    public static final int FLOAT_ENTRY_CODE = 0x05;
    public static final int DOUBLE_ENTRY_CODE = 0x06;
    public static final int BOOLEAN_ENTRY_CODE = 0x07;
    public static final int STRING_ENTRY_CODE = 0x08;
    public static final int OBJECT_ENTRY_CODE = 0x09;
    public static final int ARRAY_ENTRY_CODE = 0x0A;
    public static final int NULL_ENTRY_CODE = 0x0B;

    public static final BinaryFormatEntryType BYTE = new BinaryFormatEntryType(BYTE_ENTRY_CODE | (0x01 << 4));

    public static final BinaryFormatEntryType SHORT = new BinaryFormatEntryType(SHORT_ENTRY_CODE | (0x02 << 4));

    public static final BinaryFormatEntryType INT = new BinaryFormatEntryType(INT_ENTRY_CODE | (0x04 << 4));

    public static final BinaryFormatEntryType SEVEN_BIT_ENCODED_INT = new BinaryFormatEntryType(SEVEN_BIT_ENCODED_INT_ENTRY_CODE);

    public static final BinaryFormatEntryType LONG = new BinaryFormatEntryType(LONG_ENTRY_CODE | (0x08 << 4));

    public static final BinaryFormatEntryType FLOAT = new BinaryFormatEntryType(FLOAT_ENTRY_CODE | (0x04 << 4));

    public static final BinaryFormatEntryType DOUBLE = new BinaryFormatEntryType(DOUBLE_ENTRY_CODE | (0x08 << 4));

    public static final BinaryFormatEntryType BOOLEAN_TRUE = new BinaryFormatEntryType(BOOLEAN_ENTRY_CODE | (1 << 4));

    public static final BinaryFormatEntryType BOOLEAN_FALSE = new BinaryFormatEntryType(BOOLEAN_ENTRY_CODE | (0 << 4));

    public static final BinaryFormatEntryType ASCII_STRING = new BinaryFormatEntryType(STRING_ENTRY_CODE | (StringEncoding.ASCII.encoded_value << 4));

    public static final BinaryFormatEntryType UNICODE_STRING = new BinaryFormatEntryType(STRING_ENTRY_CODE | (StringEncoding.UTF16_LE.encoded_value << 4));

    public static final BinaryFormatEntryType BIG_ENDIAN_UNICODE_STRING = new BinaryFormatEntryType(STRING_ENTRY_CODE | (StringEncoding.UTF16_BE.encoded_value << 4));

    public static final BinaryFormatEntryType LARGE_OBJECT = new BinaryFormatEntryType(OBJECT_ENTRY_CODE | (15 << 4));

    public static final BinaryFormatEntryType LARGE_ARRAY = new BinaryFormatEntryType(ARRAY_ENTRY_CODE | (15 << 4));

    public static final BinaryFormatEntryType NULL = new BinaryFormatEntryType(NULL_ENTRY_CODE);

    private final byte packed_data;

    private BinaryFormatEntryType(int data) { packed_data = (byte) data; }

    /**
     * Gets the type of the entry data that follows.
     * @return The type of the entry data, encoded into an integer. Only it's 16 values are actually encoded.
     */
    public int GetEntryCode() { return packed_data & 0b00001111; }

    /**
     * Gets additional data that describe the entry.
     * @return Additional data. On simple types, it is the size of the entry. <br />
     * On booleans, this directly stores the boolean data. If this is different from zero, it represents the {@code true} value; otherwise, the value {@code false} is represented. <br />
     * On strings, this stores the encoding under which the string is stored as. To decode the value properly, use the dedicated {@link #GetStringEncoding()} method instead. <br />
     * On arrays, if the number of entries is less than 15, this value stores this. Otherwise, it is 15 and a 7-bit encoded integer follows the type, indicating the actual number of entries stored.
     */
    public int GetEntryData() { return packed_data >> 4; }

    /**
     * Returns the value as it is stored in a Fast Binary Format file.
     * @return The encoded value.
     */
    public byte GetEncodedValue() { return packed_data; }

    /**
     * If the current entry type is a string, this translates the value of the {@link #GetEntryData()} method into a value from the {@link StringEncoding} enumeration.
     * @return A member of the {@link StringEncoding} enumeration.
     * @throws InvalidOperationException The current type is not a string type.
     */
    public StringEncoding GetStringEncoding()
            throws InvalidOperationException
    {
        if (GetEntryCode() != STRING_ENTRY_CODE) {
            throw new InvalidOperationException("Not a string type.");
        } else {
            return StringEncoding.values()[GetEntryData()];
        }
    }

    /**
     * Gets a value whether the current entry type instance is an array of the specified type.
     * @param type Type that the array is presumed to encompass.
     * @return A value whether the current entry type is an array of the specified type.
     */
    public boolean IsArrayOf(BinaryFormatEntryType type) { return type != null && this.GetEntryCode() == ARRAY_ENTRY_CODE && type.GetEntryCode() == this.GetEntryData(); }

    public static Tuple2<BinaryFormatEntryType, Boolean> ConstructArray(int n_elements)
    {
        if (n_elements > 14) {
            return new Tuple2<>(BinaryFormatEntryType.LARGE_ARRAY, true);
        } else {
            return new Tuple2<>(new BinaryFormatEntryType(ARRAY_ENTRY_CODE | (n_elements << 4)), false);
        }
    }

    public static Tuple2<BinaryFormatEntryType, Boolean> ConstructObject(int n_fields)
    {
        if (n_fields > 14) {
            return new Tuple2<>(BinaryFormatEntryType.LARGE_OBJECT, true);
        } else {
            return new Tuple2<>(new BinaryFormatEntryType(OBJECT_ENTRY_CODE | (n_fields << 4)), false);
        }
    }

    public void WriteTo(java.io.OutputStream os) throws IOException { os.write(packed_data); }

    public static BinaryFormatEntryType ReadFrom(java.io.InputStream is)
            throws IOException, ArgumentException
    {
        int i = is.read();
        return switch (i & 0x0F) {
            case INT_ENTRY_CODE -> INT;
            case NULL_ENTRY_CODE -> NULL;
            case BYTE_ENTRY_CODE -> BYTE;
            case LONG_ENTRY_CODE -> LONG;
            case SHORT_ENTRY_CODE -> SHORT;
            case FLOAT_ENTRY_CODE -> FLOAT;
            case DOUBLE_ENTRY_CODE -> DOUBLE;
            case SEVEN_BIT_ENCODED_INT_ENTRY_CODE -> SEVEN_BIT_ENCODED_INT;
            case BOOLEAN_ENTRY_CODE -> ((i >> 4) == 0) ? BOOLEAN_FALSE : BOOLEAN_TRUE;
            case ARRAY_ENTRY_CODE, OBJECT_ENTRY_CODE, STRING_ENTRY_CODE -> new BinaryFormatEntryType(i);
            default -> throw new ArgumentException("Not a validly encoded type: " + i);
        };
    }
}
