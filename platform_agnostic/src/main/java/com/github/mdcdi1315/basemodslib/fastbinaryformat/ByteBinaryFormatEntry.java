package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public void WriteTo(OutputStream stream) throws IOException {
        BinaryFormatEntryType.BYTE.WriteTo(stream);
        stream.write(value);
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.BYTE) {
            throw new IOException("Expected BYTE");
        } else {
            int i = stream.read();
            if (i == -1) {
                throw new IOException("End of stream");
            } else {
                value = (byte) i;
            }
        }
    }

    public byte GetValue() { return value; }

    public void SetValue(byte value) { this.value = value; }
}
