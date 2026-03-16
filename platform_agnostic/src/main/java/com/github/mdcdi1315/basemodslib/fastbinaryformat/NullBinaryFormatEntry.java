package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class NullBinaryFormatEntry
    implements BinaryFormatEntry
{
    private NullBinaryFormatEntry() {}

    public static final NullBinaryFormatEntry INSTANCE = new NullBinaryFormatEntry();

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.NULL; }

    @Override
    public void WriteTo(OutputStream stream) throws IOException { BinaryFormatEntryType.NULL.WriteTo(stream); }

    @Override
    public void ReadFrom(InputStream stream) throws IOException {
        if (BinaryFormatEntryType.ReadFrom(stream) != BinaryFormatEntryType.NULL) { throw new IOException("Expected NULL"); }
    }
}
