package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Provides an ops object for de/serializing Fast Binary Format objects using the Mojang's serialization library.
 */
public final class FastBinaryFormatOps
    implements DynamicOps<BinaryFormatEntry>
{
    /**
     * Gets the single and only instance of the {@link FastBinaryFormatOps} class.
     */
    public static final FastBinaryFormatOps INSTANCE = new FastBinaryFormatOps();

    private FastBinaryFormatOps() {}

    @Override
    public NullBinaryFormatEntry empty() { return NullBinaryFormatEntry.INSTANCE; }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, BinaryFormatEntry input) { return new ConvertAnyToTD_Mapper<>(outOps).function(input); }

    private record ConvertAnyToTD_Mapper<TD>(DynamicOps<TD> ops)
        implements Func2<BinaryFormatEntry, TD>
    {
        private record ConvertToConcreteValuesMapper<TD>(ConvertAnyToTD_Mapper<TD> mapper)
                implements Func2<Map.Entry<String, BinaryFormatEntry>, Pair<TD, TD>>
        {
            @Override
            public Pair<TD, TD> function(Map.Entry<String, BinaryFormatEntry> input) {
                return new Pair<>(
                        mapper.ops.createString(input.getKey()),
                        mapper.function(input.getValue())
                );
            }
        }

        @Override
        public TD function(BinaryFormatEntry input) {
            return switch (input) {
                case NullBinaryFormatEntry n -> ops.empty();
                case BooleanBinaryFormatEntry b -> ops.createBoolean(b.GetValue());
                case ByteBinaryFormatEntry b -> ops.createByte(b.GetValue());
                case ShortBinaryFormatEntry s -> ops.createShort(s.GetValue());
                case IntBinaryFormatEntry i -> ops.createInt(i.GetValue());
                case LongBinaryFormatEntry l -> ops.createLong(l.GetValue());
                case FloatBinaryFormatEntry f -> ops.createFloat(f.GetValue());
                case DoubleBinaryFormatEntry d -> ops.createDouble(d.GetValue());
                case SevenBitEncodedIntBinaryFormatEntry gi -> ops.createInt(gi.GetValue());
                case BaseStringBinaryFormatEntry s -> ops.createString(s.GetValue());
                case ArrayBinaryFormatEntry a -> ops.createList(a.AsStream().map(new ConvertAnyToTD_Mapper<>(ops)));
                case ObjectBinaryFormatEntry o -> ops.createMap(o.AsStream().map(new ConvertToConcreteValuesMapper<>(this)));
                case ByteFixedArrayBinaryFormatEntry b -> ops.createByteList(ByteBuffer.wrap(b.GetData()));
                case IntFixedArrayBinaryFormatEntry b -> ops.createIntList(Arrays.stream(b.GetData()));
                case LongFixedArrayBinaryFormatEntry b -> ops.createLongList(Arrays.stream(b.GetData()));
                default -> throw new RuntimeException("Unknown BinaryFormatEntry " + input);
            };
        }
    }

    @Override
    public DataResult<Number> getNumberValue(BinaryFormatEntry input)
    {
        return switch (input) {
            case ByteBinaryFormatEntry b -> DataResult.success(b.GetValue());
            case ShortBinaryFormatEntry s -> DataResult.success(s.GetValue());
            case IntBinaryFormatEntry i -> DataResult.success(i.GetValue());
            case LongBinaryFormatEntry l -> DataResult.success(l.GetValue());
            case FloatBinaryFormatEntry f -> DataResult.success(f.GetValue());
            case DoubleBinaryFormatEntry d -> DataResult.success(d.GetValue());
            case SevenBitEncodedIntBinaryFormatEntry si -> DataResult.success(si.GetValue());
            default -> CodecUtils.CreateDotNetFormattedErrorDataResult("Not a numeric type: {0}", input);
        };
    }

    @Override
    public DataResult<Boolean> getBooleanValue(BinaryFormatEntry input)
    {
        if (input instanceof BooleanBinaryFormatEntry b) {
            return DataResult.success(b.GetValue());
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Not a boolean: {0}", input);
        }
    }

    @Override
    public BinaryFormatEntry createNumeric(Number i)
    {
        return switch (i) {
            case Byte b -> new ByteBinaryFormatEntry(b);
            case Short sh -> new ShortBinaryFormatEntry(sh);
            case Integer g -> new IntBinaryFormatEntry(g);
            case Long l -> new LongBinaryFormatEntry(l);
            case Float f -> new FloatBinaryFormatEntry(f);
            case Double d -> new DoubleBinaryFormatEntry(d);
            default -> throw new IllegalStateException("Unexpected input: " + i);
        };
    }

    @Override
    public DataResult<String> getStringValue(BinaryFormatEntry input)
    {
        if (input instanceof BaseStringBinaryFormatEntry e) {
            return DataResult.success(e.GetValue());
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Not a string type: {0}", input);
        }
    }

    @Override
    public BinaryFormatEntry createString(String value) { return new UTF16LEStringBinaryFormatEntry(value); }

    @Override
    public DataResult<BinaryFormatEntry> mergeToList(BinaryFormatEntry list, BinaryFormatEntry value)
    {
        ArrayBinaryFormatEntry r = new ArrayBinaryFormatEntry();
        r.Add(value);
        if (list instanceof ArrayBinaryFormatEntry a) {
            r.AddFrom(a);
            return DataResult.success(r);
        } else if (list instanceof NullBinaryFormatEntry) {
            return DataResult.success(r);
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Merge To List was called on a non-list type: {0}", list);
        }
    }

    @Override
    public DataResult<BinaryFormatEntry> mergeToList(BinaryFormatEntry list, List<BinaryFormatEntry> values)
    {
        ArrayBinaryFormatEntry r = new ArrayBinaryFormatEntry();
        for (BinaryFormatEntry value : values) { r.Add(value); }
        return DataResult.success(r);
    }

    @Override
    public DataResult<BinaryFormatEntry> mergeToMap(BinaryFormatEntry map, BinaryFormatEntry key, BinaryFormatEntry value)
    {
        if (map instanceof ObjectBinaryFormatEntry o)  {
            return MergeToMap_Internal(o, key, value);
        } else if (map instanceof NullBinaryFormatEntry) {
            return MergeToMap_Internal(new ObjectBinaryFormatEntry(), key, value);
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Input not either empty or an object: {0}", map);
        }
    }

    private DataResult<BinaryFormatEntry> MergeToMap_Internal(ObjectBinaryFormatEntry map, BinaryFormatEntry key, BinaryFormatEntry value)
    {
        if (key instanceof BaseStringBinaryFormatEntry e) {
            ObjectBinaryFormatEntry r = map.clone();
            r.UpdateField(e.GetValue(), value);
            return DataResult.success(r);
        } else {
            return CodecUtils.CreateErrorDataResult("Key is not a string value.");
        }
    }

    private record GetMapValues_Mapper()
        implements Func2<Map.Entry<String, BinaryFormatEntry>, Pair<BinaryFormatEntry, BinaryFormatEntry>>
    {
        @Override
        public Pair<BinaryFormatEntry, BinaryFormatEntry> function(Map.Entry<String, BinaryFormatEntry> input)
        {
            return new Pair<>(
                    new UTF16LEStringBinaryFormatEntry(input.getKey()),
                    input.getValue()
            );
        }
    }

    @Override
    public DataResult<Stream<Pair<BinaryFormatEntry, BinaryFormatEntry>>> getMapValues(BinaryFormatEntry input)
    {
        if (input instanceof ObjectBinaryFormatEntry e) {
            return DataResult.success(e.AsStream().map(new GetMapValues_Mapper()));
        } else if (input instanceof NullBinaryFormatEntry) {
            return DataResult.success(Stream.empty());
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Input not either empty or an object: {0}", input);
        }
    }

    @Override
    public BinaryFormatEntry createMap(Stream<Pair<BinaryFormatEntry, BinaryFormatEntry>> map)
    {
        ObjectBinaryFormatEntry ret = new ObjectBinaryFormatEntry();
        Iterator<Pair<BinaryFormatEntry, BinaryFormatEntry>> iterator = map.iterator();
        BinaryFormatEntry key, value;
        while (iterator.hasNext())
        {
            Pair<BinaryFormatEntry, BinaryFormatEntry> pair = iterator.next();
            key = pair.getFirst();
            value = pair.getSecond();
            if (key instanceof BaseStringBinaryFormatEntry e) {
                ret.UpdateField(e.GetValue(), value);
            } else {
                throw new IllegalStateException("Key is not a string value.");
            }
        }
        return ret;
    }

    @Override
    public DataResult<Stream<BinaryFormatEntry>> getStream(BinaryFormatEntry input)
    {
        return switch (input) {
            case ArrayBinaryFormatEntry a -> DataResult.success(a.AsStream());
            case ByteFixedArrayBinaryFormatEntry b ->
                    DataResult.success(IntStream.range(0, b.GetSize()).mapToObj(new MapToObj_Byte(b.GetData())));
            case FloatFixedArrayBinaryFormatEntry b ->
                    DataResult.success(IntStream.range(0, b.GetSize()).mapToObj(new MapToObj_Float(b.GetData())));
            case ShortFixedArrayBinaryFormatEntry b ->
                    DataResult.success(IntStream.range(0, b.GetSize()).mapToObj(new MapToObj_Short(b.GetData())));
            case IntFixedArrayBinaryFormatEntry b ->
                    DataResult.success(Arrays.stream(b.GetData()).mapToObj(IntBinaryFormatEntry::new));
            case LongFixedArrayBinaryFormatEntry b ->
                    DataResult.success(Arrays.stream(b.GetData()).mapToObj(LongBinaryFormatEntry::new));
            case DoubleFixedArrayBinaryFormatEntry b ->
                    DataResult.success(Arrays.stream(b.GetData()).mapToObj(DoubleBinaryFormatEntry::new));
            default -> CodecUtils.CreateDotNetFormattedErrorDataResult("Not a list or array type: {0}", input);
        };
    }

    private record MapToObj_Byte(byte[] array)
        implements IntFunction<ByteBinaryFormatEntry>
    {
        @Override
        public ByteBinaryFormatEntry apply(int value) { return new ByteBinaryFormatEntry(array[value]); }
    }

    private record MapToObj_Float(float[] array)
        implements IntFunction<FloatBinaryFormatEntry>
    {
        @Override
        public FloatBinaryFormatEntry apply(int value) { return new FloatBinaryFormatEntry(array[value]); }
    }

    private record MapToObj_Short(short[] array)
        implements IntFunction<ShortBinaryFormatEntry>
    {
        @Override
        public ShortBinaryFormatEntry apply(int value) { return new ShortBinaryFormatEntry(array[value]); }
    }

    @Override
    public BinaryFormatEntry createList(Stream<BinaryFormatEntry> input)
    {
        ArrayBinaryFormatEntry array = new ArrayBinaryFormatEntry();
        Iterator<BinaryFormatEntry> iterator = input.iterator();
        while (iterator.hasNext()) { array.Add(iterator.next()); }
        array.Optimize();
        return array;
    }

    @Override
    public BinaryFormatEntry createByteList(ByteBuffer input)
    {
        ByteFixedArrayBinaryFormatEntry entry = new ByteFixedArrayBinaryFormatEntry();
        entry.CreateArray(input.remaining());
        input.get(entry.GetData());
        return entry;
    }

    @Override
    public DataResult<IntStream> getIntStream(BinaryFormatEntry input)
    {
        return switch (input) {
            case ArrayBinaryFormatEntry a -> {
                IntStream.Builder builder = IntStream.builder();
                for (BinaryFormatEntry i : a)
                {
                    if (i instanceof IntBinaryFormatEntry gi) { builder.add(gi.GetValue()); }
                }
                yield DataResult.success(builder.build());
            }
            case IntFixedArrayBinaryFormatEntry b -> DataResult.success(Arrays.stream(b.GetData()));
            default -> CodecUtils.CreateDotNetFormattedErrorDataResult("Not an integer fixed array or array type: {0}", input);
        };
    }

    @Override
    public DataResult<LongStream> getLongStream(BinaryFormatEntry input)
    {
        return switch (input) {
            case ArrayBinaryFormatEntry a -> {
                LongStream.Builder builder = LongStream.builder();
                for (BinaryFormatEntry i : a)
                {
                    if (i instanceof LongBinaryFormatEntry gi) { builder.add(gi.GetValue()); }
                }
                yield DataResult.success(builder.build());
            }
            case LongFixedArrayBinaryFormatEntry b -> DataResult.success(Arrays.stream(b.GetData()));
            default -> CodecUtils.CreateDotNetFormattedErrorDataResult("Not a long integer fixed array or array type: {0}", input);
        };
    }

    @Override
    public DataResult<ByteBuffer> getByteBuffer(BinaryFormatEntry input)
    {
        if (input instanceof ArrayBinaryFormatEntry a) {
            Iterator<BinaryFormatEntry> iterator = a.iterator();
            ByteBuffer bb = ByteBuffer.allocate(a.GetCount());
            BinaryFormatEntry v;
            while (iterator.hasNext())
            {
                v = iterator.next();
                if (v instanceof ByteBinaryFormatEntry b) {
                    bb.put(b.GetValue());
                } else {
                    return CodecUtils.CreateDotNetFormattedErrorDataResult("Not a byte: {0}", v);
                }
            }
            return DataResult.success(bb.rewind());
        } else if (input instanceof NullBinaryFormatEntry) {
            return DataResult.success(ByteBuffer.allocate(0));
        } else if (input instanceof ByteFixedArrayBinaryFormatEntry e) {
            return DataResult.success(ByteBuffer.wrap(e.GetData()));
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Not a list type: {0}", input);
        }
    }

    @Override
    public DataResult<MapLike<BinaryFormatEntry>> getMap(BinaryFormatEntry input)
    {
        if (input instanceof ObjectBinaryFormatEntry a) {
            return DataResult.success(a);
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Not an object: {0}", input);
        }
    }

    @Override
    public IntBinaryFormatEntry createInt(int value) { return new IntBinaryFormatEntry(value); }

    @Override
    public ByteBinaryFormatEntry createByte(byte value) { return new ByteBinaryFormatEntry(value); }

    @Override
    public LongBinaryFormatEntry createLong(long value) { return new LongBinaryFormatEntry(value); }

    @Override
    public FloatBinaryFormatEntry createFloat(float value) { return new FloatBinaryFormatEntry(value); }

    @Override
    public ShortBinaryFormatEntry createShort(short value) { return new ShortBinaryFormatEntry(value); }

    @Override
    public DoubleBinaryFormatEntry createDouble(double value) { return new DoubleBinaryFormatEntry(value); }

    @Override
    public BooleanBinaryFormatEntry createBoolean(boolean value) { return value ? BooleanBinaryFormatEntry.TRUE : BooleanBinaryFormatEntry.FALSE; }

    @Override
    public DataResult<BinaryFormatEntry> get(BinaryFormatEntry input, String key)
    {
        if (input instanceof ObjectBinaryFormatEntry o) {
            BinaryFormatEntry ret = o.GetField(key);
            if (ret == null) {
                return CodecUtils.CreateDotNetFormattedErrorDataResult("Field with name '{0}' was not found in the input object.", key);
            } else {
                return DataResult.success(ret);
            }
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Not a object: {0}", input);
        }
    }

    @Override
    public BinaryFormatEntry remove(BinaryFormatEntry input, String key)
    {
        if (input instanceof ObjectBinaryFormatEntry e) {
            ObjectBinaryFormatEntry g = e.clone();
            g.RemoveField(key);
            return g;
        } else {
            return input;
        }
    }

    @Override
    public BinaryFormatEntry set(BinaryFormatEntry input, String key, BinaryFormatEntry value)
    {
        ObjectBinaryFormatEntry ret = (input instanceof ObjectBinaryFormatEntry o) ? o.clone() : new ObjectBinaryFormatEntry();
        ret.UpdateField(key, value);
        return ret;
    }

    @Override
    public BinaryFormatEntry update(BinaryFormatEntry input, String key, Function<BinaryFormatEntry, BinaryFormatEntry> function)
    {
        if (input instanceof ObjectBinaryFormatEntry o) {
            BinaryFormatEntry entry = o.GetField(key);
            if (entry == null) {
                return input;
            } else {
                o.UpdateField(key, function.apply(entry));
                return o;
            }
        } else {
            return input;
        }
    }

    @Override
    public BinaryFormatEntry updateGeneric(BinaryFormatEntry input, BinaryFormatEntry key, Function<BinaryFormatEntry, BinaryFormatEntry> function)
    {
        if (input instanceof ObjectBinaryFormatEntry o) {
            if (key instanceof BaseStringBinaryFormatEntry e) {
                BinaryFormatEntry entry = o.GetField(e.GetValue());
                if (entry == null) {
                    return input;
                } else {
                    ObjectBinaryFormatEntry u = o.clone();
                    u.UpdateField(e.GetValue(), function.apply(entry));
                    return u;
                }
            } else {
                return input;
            }
        } else {
            return input;
        }
    }

    @Override
    public DataResult<BinaryFormatEntry> getGeneric(BinaryFormatEntry input, BinaryFormatEntry key)
    {
        if (key instanceof BaseStringBinaryFormatEntry e) {
            return this.get(input, e.GetValue());
        } else {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("Not a string key: {0}", key);
        }
    }

    @Override
    public Number getNumberValue(BinaryFormatEntry input, Number defaultValue) { return getNumberValue(input).result().orElse(defaultValue); }

    @Override
    public ArrayBinaryFormatEntry emptyList() { return new ArrayBinaryFormatEntry(); }

    @Override
    public ObjectBinaryFormatEntry emptyMap() { return new ObjectBinaryFormatEntry(); }

    @Override
    public String toString() { return "Fast Binary Format Ops"; }
}
