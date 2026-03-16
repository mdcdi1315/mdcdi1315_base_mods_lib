package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class UTF16BEStringBinaryFormatEntry
    extends BaseStringBinaryFormatEntry
{
    public UTF16BEStringBinaryFormatEntry(String string) throws ArgumentNullException { super(string); }

    @Override
    protected Charset GetCharset() { return StandardCharsets.UTF_16BE; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.BIG_ENDIAN_UNICODE_STRING; }
}
