package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class ShortFixedArrayBinaryFormatEntry
    extends BaseFixedArrayBinaryFormatEntry
{
    private short[] array;

    public ShortFixedArrayBinaryFormatEntry() { array = new short[0]; }

    @Override
    public short[] GetData() { return array; }

    @Override
    public int GetSize() { return array.length; }

    @Override
    protected void CreateArrayImpl(int n_elements) { array = new short[n_elements]; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.FIXED_ARRAY_SHORT; }

    @Override
    protected void WriteArrayData(WrappedOutputStream stream)
            throws IOException
    {
        for (short s : array) { stream.WriteShortLE(s); }
    }

    @Override
    protected void ReadArrayData(WrappedInputStream stream, int length)
            throws IOException
    {
        CreateArrayImpl(length);
        for (int I = 0; I < length; I++) {
            array[I] = stream.ReadShortLE();
        }
    }
}
