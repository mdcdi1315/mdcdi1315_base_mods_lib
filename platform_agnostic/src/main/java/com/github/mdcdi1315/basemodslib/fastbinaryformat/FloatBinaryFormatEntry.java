package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FloatBinaryFormatEntry
    implements BinaryFormatEntry
{
    private float value;

    public FloatBinaryFormatEntry(float value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.FLOAT; }

    @Override
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.FLOAT.WriteTo(stream);
        int g = Float.floatToRawIntBits(value);
        stream.write(new byte[] {
                (byte)(g & 0xFF),
                (byte)((g >> 8) & 0xFF),
                (byte)((g >> 16) & 0xFF),
                (byte)((g >> 24) & 0xFF)
        });
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.FLOAT) {
            throw new IOException("Expected FLOAT");
        } else {
            int t_value = 0;
            byte[] data = FastBinaryFormatUtils.ReadBytes(stream, 4);
            for (int I = 0; I < data.length; I++) { t_value |= data[I] << (I * 8); }
            value = Float.intBitsToFloat(t_value);
        }
    }

    public float GetValue() { return value; }

    public void SetValue(float value) { this.value = value; }
}
