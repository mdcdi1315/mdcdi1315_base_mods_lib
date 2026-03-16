package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class SevenBitEncodedIntBinaryFormatEntry
    implements BinaryFormatEntry
{
    private int value;

    public SevenBitEncodedIntBinaryFormatEntry(int value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT; }

    @Override
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT.WriteTo(stream);
        FastBinaryFormatUtils.Write7BitEncodedInt(stream, value);
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) !=  BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT) {
            throw new IOException("Expected SEVEN_BIT_ENCODED_INT");
        } else {
            value = FastBinaryFormatUtils.Read7BitEncodedInt(stream);
        }
    }

    public int GetValue() { return value; }

    public void SetValue(int value) { this.value = value; }
}
