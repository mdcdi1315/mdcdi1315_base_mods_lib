package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IntBinaryFormatEntry
    implements BinaryFormatEntry
{
    private int value;

    public IntBinaryFormatEntry(int value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.INT; }

    @Override
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.INT.WriteTo(stream);
        stream.write(new byte[] {
                (byte)(value & 0xFF),
                (byte)((value >> 8) & 0xFF),
                (byte)((value >> 16) & 0xFF),
                (byte)((value >> 24) & 0xFF)
        });
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.INT) {
            throw new IOException("Expected INT");
        } else {
            value = 0;
            byte[] data = FastBinaryFormatUtils.ReadBytes(stream, 4);
            for (int I = 0; I < data.length; I++) { value |= data[I] << (I * 8); }
        }
    }

    public int GetValue() { return value; }

    public void SetValue(int value) { this.value = value; }
}
