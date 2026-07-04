package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class BooleanBinaryFormatEntry
    implements BinaryFormatEntry
{
    private final boolean value;

    public static final BooleanBinaryFormatEntry
            TRUE = new BooleanBinaryFormatEntry(true),
            FALSE = new BooleanBinaryFormatEntry(false);

    @Deprecated(since = "1.0.35", forRemoval = true) // Is going to be private in a future release.
    public BooleanBinaryFormatEntry(boolean value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return value ? BinaryFormatEntryType.BOOLEAN_TRUE : BinaryFormatEntryType.BOOLEAN_FALSE; }

    @Override
    public void WriteTo(WrappedOutputStream stream) throws IOException { GetType().WriteTo(stream); }

    @Override
    public void ReadFrom(WrappedInputStream stream, BinaryFormatEntryType type)
            throws IOException
    {
        if (!GetType().equals(type))
        {
            if (type.GetEntryCode() != BinaryFormatEntryType.BOOLEAN_ENTRY_CODE) {
                throw new IOException("Expected BOOLEAN");
            } else {
                throw new IOException(String.format("Expected %s, while found %s.", Boolean.toString(type.GetEntryData() != 0).toUpperCase(), Boolean.toString(value).toUpperCase()));
            }
        }
    }

    public boolean GetValue() { return value; }

    @Deprecated(since = "1.0.35", forRemoval = true)
    public void SetValue(boolean value) {  }
}
