package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class ByteFixedArrayBinaryFormatEntry
    extends BaseFixedArrayBinaryFormatEntry
{
    private byte[] bytes;

    public ByteFixedArrayBinaryFormatEntry() { bytes = new byte[0]; }

    @Override
    public byte[] GetData() { return bytes; }

    @Override
    public int GetSize() { return bytes.length; }

    @Override
    protected void CreateArrayImpl(int n_elements) { bytes = new byte[n_elements]; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.FIXED_ARRAY_BYTE; }

    @Override
    protected void WriteArrayData(WrappedOutputStream stream) throws IOException { stream.write(bytes); }

    @Override
    protected void ReadArrayData(WrappedInputStream stream, int length) throws IOException { bytes = stream.ReadExactly(length); }
}
