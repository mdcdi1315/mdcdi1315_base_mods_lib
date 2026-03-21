package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class FloatFixedArrayBinaryFormatEntry
        extends BaseFixedArrayBinaryFormatEntry
{
    private float[] array;

    public FloatFixedArrayBinaryFormatEntry() { array = new float[0]; }

    @Override
    public float[] GetData() { return array; }

    @Override
    public int GetSize() { return array.length; }

    @Override
    protected void CreateArrayImpl(int n_elements) { array = new float[n_elements]; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.FIXED_ARRAY_FLOAT; }

    @Override
    protected void WriteArrayData(WrappedOutputStream stream)
            throws IOException
    {
        for (float s : array) { stream.WriteFloatLE(s); }
    }

    @Override
    protected void ReadArrayData(WrappedInputStream stream, int length)
            throws IOException
    {
        CreateArrayImpl(length);
        for (int I = 0; I < length; I++) {
            array[I] = stream.ReadFloatLE();
        }
    }
}