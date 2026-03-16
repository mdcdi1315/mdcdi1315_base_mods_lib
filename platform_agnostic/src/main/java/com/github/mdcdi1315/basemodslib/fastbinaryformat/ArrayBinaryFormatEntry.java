package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Spliterator;
import java.io.PushbackInputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class ArrayBinaryFormatEntry
    implements BinaryFormatEntry, Iterable<BinaryFormatEntry>
{
    private List<BinaryFormatEntry> entries;

    public ArrayBinaryFormatEntry() { entries = ImmutableList.of(); }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.LARGE_ARRAY; }

    @Override
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        var header = BinaryFormatEntryType.ConstructArray(entries.size());
        header.GetItem1().WriteTo(stream);
        if (header.GetItem2()) { FastBinaryFormatUtils.Write7BitEncodedInt(stream, entries.size()); }
        for (BinaryFormatEntry entry : entries) { entry.WriteTo(stream); }
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        var e = BinaryFormatEntryType.ReadFrom(stream);
        if (e.GetEntryCode() != BinaryFormatEntryType.ARRAY_ENTRY_CODE) {
            throw new IOException("Expected ARRAY");
        } else {
            BinaryFormatEntry entry;
            int elements = e.GetEntryData();
            if (elements > 14) { elements = FastBinaryFormatUtils.Read7BitEncodedInt(stream); }
            PushbackInputStream pis = new PushbackInputStream(stream, 1);
            ImmutableList.Builder<BinaryFormatEntry> builder = ImmutableList.builderWithExpectedSize(elements);
            for (; elements > 0; elements--)
            {
                e = BinaryFormatEntryType.ReadFrom(pis);
                entry = switch (e.GetEntryCode())
                {
                    case BinaryFormatEntryType.NULL_ENTRY_CODE -> NullBinaryFormatEntry.INSTANCE;
                    case BinaryFormatEntryType.OBJECT_ENTRY_CODE -> new ObjectBinaryFormatEntry();
                    case BinaryFormatEntryType.ARRAY_ENTRY_CODE -> new ArrayBinaryFormatEntry();
                    case BinaryFormatEntryType.BYTE_ENTRY_CODE -> new ByteBinaryFormatEntry(0);
                    case BinaryFormatEntryType.SHORT_ENTRY_CODE -> new ShortBinaryFormatEntry(0);
                    case BinaryFormatEntryType.INT_ENTRY_CODE -> new IntBinaryFormatEntry(0);
                    case BinaryFormatEntryType.LONG_ENTRY_CODE -> new LongBinaryFormatEntry(0L);
                    case BinaryFormatEntryType.FLOAT_ENTRY_CODE -> new FloatBinaryFormatEntry(0F);
                    case BinaryFormatEntryType.DOUBLE_ENTRY_CODE -> new DoubleBinaryFormatEntry(0D);
                    case BinaryFormatEntryType.BOOLEAN_ENTRY_CODE -> new BooleanBinaryFormatEntry(false);
                    case BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT_ENTRY_CODE -> new SevenBitEncodedIntBinaryFormatEntry(0);
                    case BinaryFormatEntryType.STRING_ENTRY_CODE -> switch (e.GetStringEncoding()) {
                        case UTF16_LE -> new UTF16LEStringBinaryFormatEntry(StringUtils.Empty);
                        case UTF16_BE -> new UTF16BEStringBinaryFormatEntry(StringUtils.Empty);
                        case ASCII -> new ASCIIStringBinaryFormatEntry(StringUtils.Empty);
                        default -> throw new FormatException("Unexpected encoding " + e.GetStringEncoding());
                    };
                    default -> throw new IOException("Do not know how to decode type " + e.GetEntryCode());
                };
                pis.unread(e.GetEncodedValue());
                entry.ReadFrom(pis);
                builder.add(entry);
            }
            entries = builder.build();
        }
    }

    public Stream<BinaryFormatEntry> AsStream() { return entries.stream(); }

    @Override
    public Spliterator<BinaryFormatEntry> spliterator() { return entries.spliterator(); }

    @Override
    public @NotNull Iterator<BinaryFormatEntry> iterator() { return entries.iterator(); }

    @Override
    public void forEach(Consumer<? super BinaryFormatEntry> action) { entries.forEach(action); }

    public int GetCount() { return entries.size(); }

    public BinaryFormatEntry GetAt(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0 || index >= entries.size()) {
            throw new ArgumentOutOfRangeException("index", "Index is out of range of the array's bounds.");
        } else {
            return entries.get(index);
        }
    }

    public boolean Optimize()
    {
        if (entries instanceof ArrayList<BinaryFormatEntry> l) {
            l.trimToSize();
            return true;
        } else {
            return false;
        }
    }

    public void ModifyAt(int index, BinaryFormatEntry entry)
            throws ArgumentOutOfRangeException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(entry, "entry");
        if (index < 0 || index >= entries.size()) {
            throw new ArgumentOutOfRangeException("index", "Index is out of range of the array's bounds.");
        } else {
            if (entries instanceof ImmutableList<BinaryFormatEntry>) { entries = new ArrayList<>(entries); }
            entries.set(index, entry);
        }
    }

    public BinaryFormatEntry RemoveAt(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0 || index >= entries.size()) {
            throw new ArgumentOutOfRangeException("index", "Index is out of range of the array's bounds.");
        } else {
            if (entries instanceof ImmutableList<BinaryFormatEntry>) { entries = new ArrayList<>(entries); }
            return entries.remove(index);
        }
    }

    public void Add(BinaryFormatEntry entry)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(entry, "entry");
        if (entries instanceof ImmutableList<BinaryFormatEntry>) { entries = new ArrayList<>(entries); }
        entries.add(entry);
    }
}
