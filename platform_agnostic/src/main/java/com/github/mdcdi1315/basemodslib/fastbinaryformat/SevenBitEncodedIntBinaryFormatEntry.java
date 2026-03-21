package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.SevenBitEncodedInt;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

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
    public void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        if (BinaryFormatEntryType.ReadFrom(stream) !=  BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT) {
            throw new IOException("Expected SEVEN_BIT_ENCODED_INT");
        } else {
            value = SevenBitEncodedInt.Read(stream);
        }
    }

    public int GetValue() { return value; }

    public void SetValue(int value) { this.value = value; }
}
