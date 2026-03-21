package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.*;

public final class IntBinaryFormatEntry
    implements BinaryFormatEntry
{
    private int value;

    public IntBinaryFormatEntry(int value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.INT; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.INT.WriteTo(stream);
        stream.WriteIntegerLE(value);
    }

    @Override
    public void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.INT) {
            throw new IOException("Expected INT");
        } else {
            value = stream.ReadIntegerLE();
        }
    }

    public int GetValue() { return value; }

    public void SetValue(int value) { this.value = value; }
}
