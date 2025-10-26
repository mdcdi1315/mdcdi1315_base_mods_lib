package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;

import net.minecraft.resources.ResourceLocation;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public final class ConfigValueCodec
    implements Codec<Object>
{
    public static final ConfigValueCodec INSTANCE = new ConfigValueCodec();

    private ConfigValueCodec() {}

    private <T> DataResult<Pair<Object, T>> DecodeStream(DynamicOps<T> ops, T input, Stream<T> str)
    {
        ArrayValue.Builder builder = new ArrayValue.Builder();
        Iterator<T> itr = str.iterator();
        DataResult<Pair<Object, T>> drt;
        while (itr.hasNext()) {
            drt = DecodeSimple(ops, itr.next());
            if (drt.result().isEmpty()) {
                return DataResult.error(new StringSupplier("Cannot decode the element in the array due to an error:\n" + drt.error().get().message()));
            }
            builder.Add(drt.result().get().getFirst());
        }
        return DataResult.success(Pair.of(builder.Build(), input));
    }

    private <T> DataResult<Pair<Object, T>> DecodeSimple(DynamicOps<T> ops, T input)
    {
        Optional<Boolean> dr1 = ops.getBooleanValue(input).result();

        if (dr1.isPresent()) {
            return DataResult.success(Pair.of(dr1.get(), input));
        } else {
            Optional<String> dr2 = ops.getStringValue(input).result();
            if (dr2.isPresent()) {
                return DataResult.success(Pair.of(dr2.get(), input));
            } else {
                Optional<Number> dr3 = ops.getNumberValue(input).result();
                if (dr3.isPresent()) {
                    return DataResult.success(Pair.of(dr3.get(), input));
                } else {
                    Optional<Pair<ResourceLocation, T>> dr4 = ResourceLocation.CODEC.decode(ops, input).result();
                    if (dr4.isPresent()) {
                        return DataResult.success(Pair.of(dr4.get(), input));
                    } else {
                        Optional<Stream<T>> dr5 = ops.getStream(input).result();
                        if (dr5.isPresent()) {
                            return DecodeStream(ops, input, dr5.get());
                        } else {
                            Optional<Pair<SerializedField , T>> dr6 = SerializedField.GetCodec().decode(ops, input).result();
                            if (dr6.isPresent()) {
                                return DataResult.success(Pair.of(dr6.get(), input));
                            }
                        }
                    }
                }
            }
        }
        return DataResult.error(new StringSupplier("Cannot find the appropriate type of value to decode!"));
    }

    private <T> DataResult<T> EncodeArray(ArrayValue input, DynamicOps<T> ops, T prefix)
    {
        ListBuilder<T> lt = ops.listBuilder();

        for (Object value : input) {
            lt.add(EncodeSimple(value, ops, ops.empty()));
        }

        return lt.build(prefix);
    }

    private <T> DataResult<T> EncodeSimple(Object input, DynamicOps<T> ops, T prefix)
    {
        if (input instanceof Number n) {
            return DataResult.success(ops.createNumeric(n));
        } else if (input instanceof String s) {
            return DataResult.success(ops.createString(s));
        } else if (input instanceof Boolean b) {
            return DataResult.success(ops.createBoolean(b));
        } else if (input instanceof ResourceLocation rl) {
            return ResourceLocation.CODEC.encode(rl, ops, prefix);
        } else if (input instanceof ArrayValue av) {
            return EncodeArray(av, ops, prefix);
        } else {
            return DataResult.error(StringSupplier.FromFormatted("Cannot encode the value of type %s!!!" , input.getClass().getName()));
        }
    }

    @Override
    public <T> DataResult<Pair<Object, T>> decode(DynamicOps<T> ops, T input) { return DecodeSimple(ops, input); }

    @Override
    public <T> DataResult<T> encode(Object input, DynamicOps<T> ops, T prefix) { return EncodeSimple(input, ops, prefix); }
}
