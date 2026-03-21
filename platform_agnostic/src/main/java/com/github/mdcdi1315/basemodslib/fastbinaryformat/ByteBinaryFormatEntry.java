package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.IOException;

public final class ByteBinaryFormatEntry
    implements BinaryFormatEntry
{
    private byte value;

    public ByteBinaryFormatEntry(int value)
            throws ArgumentOutOfRangeException
    {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArgumentOutOfRangeException("value", "Byte value out of range: " + value);
        } else {
            this.value = (byte)value;
        }
    }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.BYTE; }

    @Override
    public void WriteTo(WrappedOutputStream stream) throws IOException { BinaryFormatEntryType.BYTE.WriteTo(stream); stream.write(value); }

    @Override
    public void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.BYTE) {
            throw new IOException("Expected BYTE");
        } else {
            value = stream.ReadLiteralByte();
        }
    }

    public byte GetValue() { return value; }

    public void SetValue(byte value) { this.value = value; }
}
