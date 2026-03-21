package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.io.SevenBitEncodedInt;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.IOException;

public abstract class BaseFixedArrayBinaryFormatEntry
    implements BinaryFormatEntry
{
    public BaseFixedArrayBinaryFormatEntry() {  }

    @Override
    public abstract BinaryFormatEntryType GetType();

    /**
     * Gets the number of elements contained in the array.
     * @return The number of elements contained in the array.
     */
    public int GetSize() { return java.lang.reflect.Array.getLength(GetData()); }

    /**
     * Gets the array data.
     * @return The array data.
     */
    @NotNull
    public abstract Object GetData();

    /**
     * Creates a new array of the specified number of elements. <br />
     * The older one is discarded.
     * @param n_elements The number of elements that the current array entry will now contain.
     */
    public final void CreateArray(int n_elements)
        throws ArgumentOutOfRangeException
    {
        if (n_elements < 0) {
            throw new ArgumentOutOfRangeException("n_elements", "Number of elements must be greater than or equal 0.");
        } else {
            CreateArrayImpl(n_elements);
        }
    }

    protected abstract void CreateArrayImpl(int n_elements);

    protected abstract void WriteArrayData(WrappedOutputStream stream) throws IOException;

    protected abstract void ReadArrayData(WrappedInputStream stream, int length) throws IOException;

    @Override
    public final void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        GetType().WriteTo(stream);
        // Number of elements, as a 7-bit encoded integer.
        SevenBitEncodedInt.Write(stream, GetSize());
        // Iterate through all the elements, write them all
        WriteArrayData(stream);
    }

    @Override
    public final void ReadFrom(PushbackWrappedInputStream stream)
            throws IOException
    {
        BinaryFormatEntryType read = BinaryFormatEntryType.ReadFrom(stream);
        if (read.equals(GetType())) {
            int length = SevenBitEncodedInt.Read(stream);
            ReadArrayData(stream, length);
        } else {
            throw new IOException("Expected FIXED_ARRAY");
        }
    }
}
