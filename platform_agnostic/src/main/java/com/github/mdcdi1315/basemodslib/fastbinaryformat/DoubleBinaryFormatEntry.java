package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DoubleBinaryFormatEntry
    implements BinaryFormatEntry
{
    private double value;

    public DoubleBinaryFormatEntry(double value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.DOUBLE; }

    @Override
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.DOUBLE.WriteTo(stream);
        long g = Double.doubleToRawLongBits(value);
        stream.write(new byte[] {
                (byte)(g & 0xFF),
                (byte)((g >> 8) & 0xFF),
                (byte)((g >> 16) & 0xFF),
                (byte)((g >> 24) & 0xFF),
                (byte)((g >> 32) & 0xFF),
                (byte)((g >> 40) & 0xFF),
                (byte)((g >> 48) & 0xFF),
                (byte)((g >> 56) & 0xFF),
        });
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.DOUBLE) {
            throw new IOException("Expected DOUBLE");
        } else {
            long t_value = 0L;
            byte[] data = FastBinaryFormatUtils.ReadBytes(stream, 8);
            for (int I = 0; I < data.length; I++) { t_value |= (long) data[I] << (I * 8L); }
            this.value = Double.longBitsToDouble(t_value);
        }
    }

    public double GetValue() { return value; }

    public void SetValue(double value) { this.value = value; }
}
