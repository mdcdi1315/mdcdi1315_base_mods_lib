package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class LongFixedArrayBinaryFormatEntry
        extends BaseFixedArrayBinaryFormatEntry
{
    private long[] array;

    public LongFixedArrayBinaryFormatEntry() { array = new long[0]; }

    @Override
    public long[] GetData() { return array; }

    @Override
    public int GetSize() { return array.length; }

    @Override
    protected void CreateArrayImpl(int n_elements) { array = new long[n_elements]; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.FIXED_ARRAY_LONG; }

    @Override
    protected void WriteArrayData(WrappedOutputStream stream)
            throws IOException
    {
        for (long s : array) { stream.WriteLongLE(s); }
    }

    @Override
    protected void ReadArrayData(WrappedInputStream stream, int length)
            throws IOException
    {
        CreateArrayImpl(length);
        for (int I = 0; I < length; I++) {
            array[I] = stream.ReadLongLE();
        }
    }
}
