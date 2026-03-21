package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.IOException;

public final class BooleanBinaryFormatEntry
    implements BinaryFormatEntry
{
    private boolean value;

    public BooleanBinaryFormatEntry(boolean value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return value ? BinaryFormatEntryType.BOOLEAN_TRUE : BinaryFormatEntryType.BOOLEAN_FALSE; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        GetType().WriteTo(stream);
    }

    @Override
    public void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        var t = BinaryFormatEntryType.ReadFrom(stream);
        if (t.GetEntryCode() != BinaryFormatEntryType.BOOLEAN_ENTRY_CODE) {
            throw new IOException("Expected BOOLEAN");
        } else {
            value = t.GetEntryData() != 0;
        }
    }

    public boolean GetValue() { return value; }

    public void SetValue(boolean value) { this.value = value; }
}
