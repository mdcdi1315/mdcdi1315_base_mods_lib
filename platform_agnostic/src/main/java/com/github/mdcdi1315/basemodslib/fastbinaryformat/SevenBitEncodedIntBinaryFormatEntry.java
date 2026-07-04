package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.SevenBitEncodedInt;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class SevenBitEncodedIntBinaryFormatEntry
    implements BinaryFormatEntry
{
    private int value;

    public SevenBitEncodedIntBinaryFormatEntry(int value) { this.value = value; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT.WriteTo(stream);
        SevenBitEncodedInt.Write(stream, value);
    }

    @Override
    public void ReadFrom(WrappedInputStream stream, BinaryFormatEntryType type)
            throws IOException
    {
        if (type != BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT) {
            throw new IOException("Expected SEVEN_BIT_ENCODED_INT");
        } else {
            value = SevenBitEncodedInt.Read(stream);
        }
    }

    public int GetValue() { return value; }

    public void SetValue(int value) { this.value = value; }
}
