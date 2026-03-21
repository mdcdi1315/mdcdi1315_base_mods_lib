package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.IOException;

public final class ShortBinaryFormatEntry
    implements BinaryFormatEntry
{
    private short value;

    public ShortBinaryFormatEntry(int value)
    {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new ArgumentOutOfRangeException("value", "Value is out of range of valid values: " + value);
        } else {
            this.value = (short) value;
        }
    }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.SHORT; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.SHORT.WriteTo(stream);
        stream.WriteShortLE(value);
    }

    @Override
    public void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.SHORT) {
            throw new IOException("Expected SHORT");
        } else {
            value = stream.ReadShortLE();
        }
    }

    public short GetValue() { return value; }

    public void SetValue(short value) { this.value = value; }
}
