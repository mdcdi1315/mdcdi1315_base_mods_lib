package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.io.*;

import java.io.*;
import java.nio.charset.Charset;

public abstract class BaseStringBinaryFormatEntry
    implements BinaryFormatEntry
{
    private String value;

    protected BaseStringBinaryFormatEntry(String value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.value = value, "value");
    }

    @NotNull
    protected abstract Charset GetCharset();

    @Override
    public abstract BinaryFormatEntryType GetType();

    @Override
    public final void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        GetType().WriteTo(stream);
        FastBinaryFormatUtils.WriteString7BitEncodedLength(stream, value, GetCharset());
    }

    @Override
    public final void ReadFrom(WrappedInputStream stream, BinaryFormatEntryType type)
            throws IOException
    {
        if (type.GetEntryCode() != GetType().GetEntryCode()) {
            throw new IOException("Expected STRING");
        } else if (type.GetStringEncoding() != GetType().GetStringEncoding()) {
            throw new IOException(StringUtils.Format("Not a {0} string entry", GetType().GetStringEncoding()));
        } else {
            int bytes = SevenBitEncodedInt.Read(stream);
            value = StringIO.ReadString(stream, GetCharset().newDecoder(), bytes);
        }
    }

    public final String GetValue() { return value; }

    public final void SetValue(String value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        this.value = value;
    }
}
