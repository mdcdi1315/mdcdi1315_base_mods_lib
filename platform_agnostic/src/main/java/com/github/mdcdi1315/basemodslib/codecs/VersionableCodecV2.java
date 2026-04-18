package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a codec implementation for de/encoding data based on versioning details. <br />
 * The names of the version and data fields are configurable as well. <br />
 * Encoding is always done as the latest version of the codec, which it will be the last declared codec.
 * @param <T> The type of the data to de/encode. Codecs provided through the constructor should provide derived types from {@link T}.
 * @since 1.0.19
 */
public final class VersionableCodecV2<T>
    implements Codec<T>
{
    private final Codec<? extends T>[] codecs;
    private final String version_field_name, data_field_name;

    /**
     * Initializes a new instance of the {@link VersionableCodecV2} class.
     * @param version_field_name The name of the version field.
     * @param data_field_name The name of the data field, the field that holds the information to provide to the versioned decoder.
     * @param codecs The decoders that are acting as versioned decoders. The first decoder always gets a version of 0, the next one the version 1, and goes on...
     * @throws ArgumentException The {@code version_field_name} and {@code data_field_name} parameters do contain the same values. -or- the {@code codecs} array is empty.
     * @throws ArgumentNullException {@code version_field_name} and/or {@code data_field_name} and/or {@code codecs} are {@code null}.
     */
    @SafeVarargs
    public VersionableCodecV2(String version_field_name, String data_field_name, Codec<? extends T>... codecs)
            throws ArgumentException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.codecs = codecs, "codecs");
        ArgumentNullException.ThrowIfNull(this.data_field_name = data_field_name, "data_field_name");
        ArgumentNullException.ThrowIfNull(this.version_field_name = version_field_name, "version_field_name");

        if (data_field_name.equals(version_field_name)) {
            throw new ArgumentException("Data name field cannot share the same name as the version field", "data_name_field");
        } else if (codecs.length == 0) {
            throw new ArgumentException("Cannot specify 0 codecs.", "codecs");
        }
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input)
    {
        var values_dt = ops.getMap(input);

        if (values_dt.isError()) {
            return DataResult.error(values_dt.error().get().messageSupplier());
        } else {
            MapLike<T1> values = values_dt.result().get();

            T1 temp = values.get(version_field_name);

            if (temp == null) {
                return CodecUtils.CreateDotNetFormattedErrorDataResult("There is not a field named as {0} in the map!" , version_field_name);
            } else {
                DataResult<Number> version_dt = ops.getNumberValue(temp);
                if (version_dt.result().isEmpty()) {
                    return CodecUtils.CreateDotNetFormattedErrorDataResult("Not a number: {0}", temp);
                } else {
                    int version = version_dt.result().get().intValue();
                    if (version >= codecs.length) {
                        return CodecUtils.CreateDotNetFormattedErrorDataResult("The specified version value ({0}) is outside of the permitted values of this codec: [0..{1}]", version, codecs.length-1);
                    } else {
                        temp = values.get(data_field_name);
                        if (temp == null) {
                            return CodecUtils.CreateDotNetFormattedErrorDataResult("There is not a field named as {0} in the map!" , data_field_name);
                        } else {
                            var dr = codecs[version].decode(ops, temp);
                            return dr.isSuccess() ?
                                    DataResult.success(Pair.of(dr.result().get().getFirst(), input)) :
                                    DataResult.error(dr.error().get().messageSupplier());
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return EncodeInternal(codecs[codecs.length - 1], input, ops, prefix);
    }

    private <TSI, TI extends TSI, TP> DataResult<TP> EncodeInternal(Codec<TI> last, TSI input, DynamicOps<TP> ops, TP prefix)
    {
        try {
            return last.encode((TI)input, ops, prefix);
        } catch (ClassCastException e) {
            return CodecUtils.CreateErrorDataResult("Failed to properly cast to latest version of the data structure. Possibly the data structure passed is not of the latest version.");
        }
    }
}
