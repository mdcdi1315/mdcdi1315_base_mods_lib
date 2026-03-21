package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class DoubleFixedArrayBinaryFormatEntry
        extends BaseFixedArrayBinaryFormatEntry
{
    private double[] array;

    public DoubleFixedArrayBinaryFormatEntry() { array = new double[0]; }

    @Override
    public double[] GetData() { return array; }

    @Override
    public int GetSize() { return array.length; }

    @Override
    protected void CreateArrayImpl(int n_elements) { array = new double[n_elements]; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.FIXED_ARRAY_DOUBLE; }

    @Override
    protected void WriteArrayData(WrappedOutputStream stream)
            throws IOException
    {
        for (double s : array) { stream.WriteDoubleLE(s); }
    }

    @Override
    protected void ReadArrayData(WrappedInputStream stream, int length)
            throws IOException
    {
        CreateArrayImpl(length);
        for (int I = 0; I < length; I++) {
            array[I] = stream.ReadDoubleLE();
        }
    }
}