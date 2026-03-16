package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class UTF16LEStringBinaryFormatEntry
    extends BaseStringBinaryFormatEntry
{
    public UTF16LEStringBinaryFormatEntry(String value) throws ArgumentNullException { super(value); }

    @Override
    protected Charset GetCharset() { return StandardCharsets.UTF_16LE; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.UNICODE_STRING; }
}
