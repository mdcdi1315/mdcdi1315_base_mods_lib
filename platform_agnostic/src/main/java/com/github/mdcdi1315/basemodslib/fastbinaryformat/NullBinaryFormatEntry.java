package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

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
    public void ReadFrom(PushbackWrappedInputStream stream) throws IOException {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.NULL) { throw new IOException("Expected NULL"); }
    }
}
