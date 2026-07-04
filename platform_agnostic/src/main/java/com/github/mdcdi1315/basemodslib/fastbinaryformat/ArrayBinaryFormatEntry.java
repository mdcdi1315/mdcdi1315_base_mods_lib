package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.io.SevenBitEncodedInt;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedInputStream;
import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.function.Consumer;

public final class ArrayBinaryFormatEntry
    implements BinaryFormatEntry, Iterable<BinaryFormatEntry>
{
    private List<BinaryFormatEntry> entries;

    public ArrayBinaryFormatEntry() { entries = ImmutableList.of(); }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.LARGE_ARRAY; }

    @Override
    public void WriteTo(WrappedOutputStream stream)
            throws IOException
    {
        var header = BinaryFormatEntryType.ConstructArray(entries.size());
        header.GetItem1().WriteTo(stream);
        if (header.GetItem2()) { SevenBitEncodedInt.Write(stream, entries.size()); }
        for (BinaryFormatEntry entry : entries) { entry.WriteTo(stream); }
    }

    @Override
    public void ReadFrom(WrappedInputStream stream, BinaryFormatEntryType type)
            throws IOException
    {
        if (type.GetEntryCode() != BinaryFormatEntryType.ARRAY_ENTRY_CODE) {
            throw new IOException("Expected ARRAY");
        } else {
            BinaryFormatEntry entry;
            int elements = type.GetEntryData();
            if (elements > 14) { elements = SevenBitEncodedInt.Read(stream); }
            ImmutableList.Builder<BinaryFormatEntry> builder = ImmutableList.builderWithExpectedSize(elements);
            for (; elements > 0; elements--)
            {
                var e = BinaryFormatEntryType.ReadFrom(stream);
                entry = FastBinaryFormatUtils.ConstructEntryFromType(e);
                entry.ReadFrom(stream, e);
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

    public void AddFrom(ArrayBinaryFormatEntry entry)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(entry, "entry");
        if (entries instanceof ImmutableList<BinaryFormatEntry>) { entries = new ArrayList<>(entries); }
        this.entries.addAll(entry.entries);
    }
}
