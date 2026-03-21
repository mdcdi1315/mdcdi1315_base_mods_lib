package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.IOException;

public final class DoubleBinaryFormatEntry
    implements BinaryFormatEntry
{
    private double value;

    public DoubleBinaryFormatEntry(double value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.DOUBLE; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.DOUBLE.WriteTo(stream);
        stream.WriteDoubleLE(value);
    }

    @Override
    public void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.DOUBLE) {
            throw new IOException("Expected DOUBLE");
        } else {
            value = stream.ReadDoubleLE();
        }
    }

    public double GetValue() { return value; }

    public void SetValue(double value) { this.value = value; }
}
