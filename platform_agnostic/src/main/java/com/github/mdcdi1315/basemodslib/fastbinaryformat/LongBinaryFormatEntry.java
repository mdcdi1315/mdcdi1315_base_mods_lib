package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class LongBinaryFormatEntry
    implements BinaryFormatEntry
{
    private long value;

    public LongBinaryFormatEntry(long value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.LONG; }

    @Override
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.LONG.WriteTo(stream);
        stream.write(new byte[] {
                (byte)(value & 0xFF),
                (byte)((value >> 8) & 0xFF),
                (byte)((value >> 16) & 0xFF),
                (byte)((value >> 24) & 0xFF),
                (byte)((value >> 32) & 0xFF),
                (byte)((value >> 40) & 0xFF),
                (byte)((value >> 48) & 0xFF),
                (byte)((value >> 56) & 0xFF),
        });
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.LONG) {
            throw new IOException("Expected LONG");
        } else {
            value = 0L;
            byte[] data = FastBinaryFormatUtils.ReadBytes(stream, 8);
            for (int I = 0; I < data.length; I++) { value |= (long) data[I] << (I * 8L); }
        }
    }

    public long GetValue() { return value; }

    public void SetValue(long value) { this.value = value; }
}
