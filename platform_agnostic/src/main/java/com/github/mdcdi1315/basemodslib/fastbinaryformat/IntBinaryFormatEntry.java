package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

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
    public void ReadFrom(WrappedInputStream stream, BinaryFormatEntryType type)
            throws IOException
    {
        if (type != BinaryFormatEntryType.INT) {
            throw new IOException("Expected INT");
        } else {
            value = stream.ReadIntegerLE();
        }
    }

    public int GetValue() { return value; }

    public void SetValue(int value) { this.value = value; }
}
