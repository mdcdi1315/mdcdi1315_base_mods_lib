package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import java.io.IOException;

public final class NullBinaryFormatEntry
    implements BinaryFormatEntry
{
    private NullBinaryFormatEntry() {}

    public static final NullBinaryFormatEntry INSTANCE = new NullBinaryFormatEntry();

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.NULL; }

    @Override
    public void WriteTo(WrappedOutputStream stream) throws IOException { BinaryFormatEntryType.NULL.WriteTo(stream); }

    @Override
    public void ReadFrom(WrappedInputStream stream, BinaryFormatEntryType type) throws IOException {
        if (type != BinaryFormatEntryType.NULL) { throw new IOException("Expected NULL"); }
    }
}
