package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapLike;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.stream.Stream;
import java.util.function.Consumer;

public final class ObjectBinaryFormatEntry
    implements BinaryFormatEntry, MapLike<BinaryFormatEntry>, Iterable<Map.Entry<String, BinaryFormatEntry>>
{
    private final HashMap<String, BinaryFormatEntry> fields;

    public ObjectBinaryFormatEntry() { fields = new HashMap<>(); }

    private ObjectBinaryFormatEntry(HashMap<String, BinaryFormatEntry> fields) { this.fields = fields; }

    @Override
    public BinaryFormatEntryType GetType() { return BinaryFormatEntryType.LARGE_OBJECT; }

    @Override
    public void WriteTo(OutputStream stream)
            throws IOException
    {
        var header = BinaryFormatEntryType.ConstructObject(fields.size());
        header.GetItem1().WriteTo(stream);
        if (header.GetItem2()) { FastBinaryFormatUtils.Write7BitEncodedInt(stream, fields.size()); }
        for (Map.Entry<String, BinaryFormatEntry> map_entry : fields.entrySet()) {
            FastBinaryFormatUtils.WriteFieldNameString(stream, map_entry.getKey());
            map_entry.getValue().WriteTo(stream);
        }
    }

    @Override
    public void ReadFrom(InputStream stream)
            throws IOException
    {
        var e = BinaryFormatEntryType.ReadFrom(stream);
        if (e.GetEntryCode() != BinaryFormatEntryType.OBJECT_ENTRY_CODE) {
            throw new IOException("Expected OBJECT");
        } else {
            BinaryFormatEntry entry;
            int elements = e.GetEntryData();
            if (elements > 14) { elements = FastBinaryFormatUtils.Read7BitEncodedInt(stream); }
            PushbackInputStream pis = new PushbackInputStream(stream, 1);
            fields.clear();
            int field_name_size;
            String field_name;
            for (; elements > 0; elements--)
            {
                field_name_size = pis.read();
                if (field_name_size == -1) { throw new IOException("Unexpected end of stream"); }
                field_name = FastBinaryFormatUtils.ReadString(pis, StandardCharsets.US_ASCII.newDecoder(), field_name_size);
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
                fields.put(field_name, entry);
            }
        }
    }

    @MaybeNull
    public BinaryFormatEntry GetField(@AllowNull @ConstantExpected String field_name) { return field_name == null ? null : fields.get(field_name); }

    public Optional<Byte> GetByteField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType() != BinaryFormatEntryType.BYTE) {
                return Optional.empty();
            } else {
                return Optional.of(((ByteBinaryFormatEntry)e).GetValue());
            }
        }
    }

    public Optional<Short> GetShortField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType() != BinaryFormatEntryType.SHORT) {
                return Optional.empty();
            } else {
                return Optional.of(((ShortBinaryFormatEntry)e).GetValue());
            }
        }
    }

    public Optional<Integer> GetIntField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null) {
                return Optional.empty();
            } else if (e.GetType() == BinaryFormatEntryType.INT) {
                return Optional.of(((IntBinaryFormatEntry)e).GetValue());
            } else if (e.GetType() == BinaryFormatEntryType.SEVEN_BIT_ENCODED_INT) {
                return Optional.of(((SevenBitEncodedIntBinaryFormatEntry)e).GetValue());
            } else {
                return Optional.empty();
            }
        }
    }

    public Optional<Long> GetLongField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType() != BinaryFormatEntryType.LONG) {
                return Optional.empty();
            } else {
                return Optional.of(((LongBinaryFormatEntry)e).GetValue());
            }
        }
    }

    public Optional<Float> GetFloatField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType() != BinaryFormatEntryType.FLOAT) {
                return Optional.empty();
            } else {
                return Optional.of(((FloatBinaryFormatEntry)e).GetValue());
            }
        }
    }

    public Optional<Double> GetDoubleField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType() != BinaryFormatEntryType.DOUBLE) {
                return Optional.empty();
            } else {
                return Optional.of(((DoubleBinaryFormatEntry)e).GetValue());
            }
        }
    }

    public Optional<Boolean> GetBooleanField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType().GetEntryCode() != BinaryFormatEntryType.BOOLEAN_ENTRY_CODE) {
                return Optional.empty();
            } else {
                return Optional.of(((BooleanBinaryFormatEntry)e).GetValue());
            }
        }
    }

    public Optional<String> GetStringField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType().GetEntryCode() != BinaryFormatEntryType.STRING_ENTRY_CODE) {
                return Optional.empty();
            } else {
                return Optional.of(((BaseStringBinaryFormatEntry)e).GetValue());
            }
        }
    }

    public Optional<ArrayBinaryFormatEntry> GetArrayField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType().GetEntryCode() != BinaryFormatEntryType.ARRAY_ENTRY_CODE) {
                return Optional.empty();
            } else {
                return Optional.of((ArrayBinaryFormatEntry)e);
            }
        }
    }

    public Optional<ObjectBinaryFormatEntry> GetObjectField(@AllowNull @ConstantExpected String field_name)
    {
        if (field_name == null) {
            return Optional.empty();
        } else {
            BinaryFormatEntry e = fields.get(field_name);
            if (e == null || e.GetType().GetEntryCode() != BinaryFormatEntryType.OBJECT_ENTRY_CODE) {
                return Optional.empty();
            } else {
                return Optional.of((ObjectBinaryFormatEntry) e);
            }
        }
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, BinaryFormatEntry value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, value);
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, boolean value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new BooleanBinaryFormatEntry(value));
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, byte value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new ByteBinaryFormatEntry(value));
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, short value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new ShortBinaryFormatEntry(value));
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, int value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new IntBinaryFormatEntry(value));
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, long value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new LongBinaryFormatEntry(value));
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, float value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new FloatBinaryFormatEntry(value));
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, double value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new DoubleBinaryFormatEntry(value));
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, ObjectBinaryFormatEntry value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, value);
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, ArrayBinaryFormatEntry value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, value);
    }

    @MaybeNull
    public BinaryFormatEntry UpdateField(@ConstantExpected String field_name, String value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        ArgumentNullException.ThrowIfNull(field_name, "field_name");
        return fields.put(field_name, new UTF16LEStringBinaryFormatEntry(value));
    }

    public boolean RemoveField(@AllowNull @ConstantExpected String field_name) { return field_name != null && fields.remove(field_name) != null; }

    public int GetFieldCount() { return fields.size(); }

    public ObjectBinaryFormatEntry clone() { return new ObjectBinaryFormatEntry(new HashMap<>(fields)); }

    public Stream<Map.Entry<String, BinaryFormatEntry>> AsStream() { return fields.entrySet().stream(); }

    @Override
    public Spliterator<Map.Entry<String, BinaryFormatEntry>> spliterator() { return fields.entrySet().spliterator(); }

    @Override
    public @NotNull Iterator<Map.Entry<String, BinaryFormatEntry>> iterator() { return fields.entrySet().iterator(); }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, BinaryFormatEntry>> action) { fields.entrySet().forEach(action); }

    @Override
    @MaybeNull
    public BinaryFormatEntry get(BinaryFormatEntry key) { return (key instanceof BaseStringBinaryFormatEntry e) ? fields.get(e.GetValue()) : null; }

    @Override
    @MaybeNull
    public BinaryFormatEntry get(String key) { return fields.get(key); }

    private record MapMapEntryToMapLikeEntryFunction()
        implements Func2<Map.Entry<String, BinaryFormatEntry>, Pair<BinaryFormatEntry, BinaryFormatEntry>>
    {
        @Override
        public Pair<BinaryFormatEntry, BinaryFormatEntry> function(Map.Entry<String, BinaryFormatEntry> input) {
            return new Pair<>(
                    new UTF16LEStringBinaryFormatEntry(input.getKey()),
                    input.getValue()
            );
        }
    }

    @Override
    public Stream<Pair<BinaryFormatEntry, BinaryFormatEntry>> entries() { return AsStream().map(new MapMapEntryToMapLikeEntryFunction()); }
}
