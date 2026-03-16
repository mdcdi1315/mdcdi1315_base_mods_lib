package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class ASCIIStringBinaryFormatEntry
        extends BaseStringBinaryFormatEntry
{
    public ASCIIStringBinaryFormatEntry(String value) throws ArgumentNullException { super(value); }

    @Override
    protected Charset GetCharset() { return StandardCharsets.US_ASCII; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.ASCII_STRING; }
}
