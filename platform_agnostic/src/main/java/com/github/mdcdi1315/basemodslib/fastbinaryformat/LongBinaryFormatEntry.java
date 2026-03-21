package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.IOException;

public final class LongBinaryFormatEntry
    implements BinaryFormatEntry
{
    private long value;

    public LongBinaryFormatEntry(long value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.LONG; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.LONG.WriteTo(stream);
        stream.WriteLongLE(value);
    }

    @Override
    public void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.LONG) {
            throw new IOException("Expected LONG");
        } else {
            value = stream.ReadLongLE();
        }
    }

    public long GetValue() { return value; }

    public void SetValue(long value) { this.value = value; }
}
