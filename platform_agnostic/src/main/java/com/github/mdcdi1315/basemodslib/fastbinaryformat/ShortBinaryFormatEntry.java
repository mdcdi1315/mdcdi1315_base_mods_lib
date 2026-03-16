package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.SHORT.WriteTo(stream);
        stream.write(new byte[] { (byte)(value & 0xFF), (byte)((value >> 8) & 0xFF) });
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.SHORT) {
            throw new IOException("Expected SHORT");
        } else {
            int t = stream.read();
            if (t == -1) {
                throw new IOException("End of stream");
            } else {
                value = (short) t;
                t = stream.read();
                if (t == -1) {
                    throw new IOException("End of stream");
                } else {
                    value |= (short) (t << 8);
                }
            }
        }
    }

    public short GetValue() { return value; }

    public void SetValue(short value) { this.value = value; }
}
