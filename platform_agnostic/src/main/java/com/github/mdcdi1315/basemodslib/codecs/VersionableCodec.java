package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.*;

/**
 * Provides a codec implementation for decoding data based on versioning details. <br />
 * The names of the version and data fields are configurable as well. <br />
 * Although implementing the {@link Codec} interface, due to its nature it can only decode data; Encoding will always fail.
 * @param <T> The type of the data to decode. Decoders provided through the constructor should provide derived types from {@link T}.
 * @since 1.0.13
 */
public final class VersionableCodec<T>
    implements Codec<T>
{
    private final Decoder<? extends T>[] codecs;
    private final String version_field_name, data_field_name;

    /**
     * Initializes a new instance of the {@link VersionableCodec} class.
     * @param version_field_name The name of the version field.
     * @param data_field_name The name of the data field, the field that holds the information to provide to the versioned decoder.
     * @param codecs The decoders that are acting as versioned decoders. The first decoder always gets a version of 0, the next one the version 1, and goes on...
     * @throws ArgumentException The {@code version_field_name} and {@code data_field_name} parameters do contain the same values.
     * @throws ArgumentNullException {@code version_field_name} and/or {@code data_field_name} and/or {@code codecs} are {@code null}.
     */
    @SafeVarargs
    public VersionableCodec(String version_field_name, String data_field_name, Decoder<? extends T>... codecs)
            throws ArgumentException
    {
        ArgumentNullException.ThrowIfNull(codecs);
        ArgumentNullException.ThrowIfNull(data_field_name);
        ArgumentNullException.ThrowIfNull(version_field_name);

        if (data_field_name.equals(version_field_name)) {
            throw new ArgumentException("Data name field cannot share the same name as the version field", "data_name_field");
        }

        this.codecs = codecs;
        this.data_field_name = data_field_name;
        this.version_field_name = version_field_name;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input)
    {
        var values_dt = ops.getMap(input);

        if (values_dt.result().isEmpty()) {
            return DataResult.error(new StringSupplier(values_dt.error().get().message()));
        } else {
            MapLike<T1> values = values_dt.result().get();

            T1 temp = values.get(version_field_name);

            if (temp == null) {
                return DataResult.error(StringSupplier.FromDotNetFormatted("There is not a field named as {0} in the map!" , version_field_name));
            } else {
                DataResult<Number> version_dt = ops.getNumberValue(temp);
                if (version_dt.result().isEmpty()) {
                    return DataResult.error(new StringSupplier("Not a number: " + temp));
                } else {
                    int version = version_dt.result().get().intValue();
                    if (version >= codecs.length) {
                        return DataResult.error(StringSupplier.FromDotNetFormatted("The specified version value ({0}) is outside of the permitted values of this codec: [0..{1}]", version, codecs.length-1));
                    } else {
                        temp = values.get(data_field_name);
                        if (temp == null) {
                            return DataResult.error(StringSupplier.FromDotNetFormatted("There is not a field named as {0} in the map!" , data_field_name));
                        } else {
                            var dr = codecs[version].decode(ops, temp);
                            if (dr.result().isEmpty()) {
                                return DataResult.error(new StringSupplier(dr.error().get().message()));
                            } else {
                                return DataResult.success(Pair.of(dr.result().get().getFirst(), input));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return DataResult.error(new StringSupplier("Not supported for this kind of codec."));
    }
}
