package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class FloatBinaryFormatEntry
    implements BinaryFormatEntry
{
    private float value;

    public FloatBinaryFormatEntry(float value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.FLOAT; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.FLOAT.WriteTo(stream);
        stream.WriteFloatLE(value);
    }

    @Override
    public void ReadFrom(WrappedInputStream stream, BinaryFormatEntryType type)
            throws IOException
    {
        if (type != BinaryFormatEntryType.FLOAT) {
            throw new IOException("Expected FLOAT");
        } else {
            value = stream.ReadFloatLE();
        }
    }

    public float GetValue() { return value; }

    public void SetValue(float value) { this.value = value; }
}
