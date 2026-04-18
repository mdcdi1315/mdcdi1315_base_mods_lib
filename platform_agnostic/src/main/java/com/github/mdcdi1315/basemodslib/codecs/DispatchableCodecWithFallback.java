package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.*;

import java.util.function.Function;

/**
 * Provides a {@link Codec} that dispatches {@link MapCodec}s to concrete types. <br />
 * Types are deriving from the type parameter {@link T}. <br />
 * Note: This is a more flexible variant of the {@link Codec#dispatch(Function, Function)}, allowing full control over how the data themselves are serialized, and how special cases are treated.
 * @param <T> The type that objects of it are de/serialized.
 * @param <TYPE> The type that provides the key for identifying a value for the mapping {@link TYPE} to {@link T}.
 * @since 1.0.19
 */
public final class DispatchableCodecWithFallback<T, TYPE>
    implements Codec<T>
{
    @AllowNull
    private final TYPE default_type;
    @AllowNull
    private final Decoder<T> fallback;
    private final Codec<TYPE> key_codec;
    private final String type_field_name;
    private final Func2<? super T, ? extends TYPE> type_getter;
    private final Func2<? super TYPE, ? extends MapCodec<? extends T>> map_codec_getter;

    /**
     * Initializes a new instance of the {@link DispatchableCodecWithFallback} class.
     * @param key_codec The {@link Codec} that de/encodes keys for identifying the mapping between {@link TYPE} to {@link T} instances.
     * @param type_field_name The name of the key field. Its value is de/encoded using the {@code key_codec} parameter.
     * @param type_getter The function that provides the type instance of a given object.
     * @param map_codec_getter The function that provides a {@link MapCodec} from a given type instance and can de/encode specific and derived objects of type {@link T}.
     * @param fallback The {@link Decoder} that will decode an object of type {@link T} if the data do not represent a map. <br />
     *                 (Do not worry that the type of this parameter is {@link Decoder}, you can still pass {@link Codec} instances to this) <br />
     *                 Can be {@code null}, in which case an error will be instead returned.
     * @param default_type If the field named by the contents of the {@code type_field_name} parameter is absent,
     *                     this type determines the {@link MapCodec} to use as a default one. <br />
     *                     Can be {@code null}, in which case an error will be instead returned.
     * @implNote Note that the {@code default_type} parameter is used if and only if the field does not exist. <br />
     * If this codec has found the field but decoding its value has failed, that failure will be returned <br />
     * instead to avoid making assumptions on what the data themselves do represent.
     * @throws ArgumentException {@code type_field_name} parameter is the empty (&quot;&quot;) string, or it consists only of whitespace characters.
     * @throws ArgumentNullException {@code key_codec} and/or {@code type_field_name} and/or {@code type_getter} and/or {@code map_codec_getter} are {@code null}.
     */
    public DispatchableCodecWithFallback(
            Codec<TYPE> key_codec,
            String type_field_name,
            Func2<? super T, ? extends TYPE> type_getter,
            Func2<? super TYPE, ? extends MapCodec<? extends T>> map_codec_getter,
            @AllowNull Decoder<T> fallback,
            @AllowNull TYPE default_type
    ) throws ArgumentException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.key_codec = key_codec, "key_codec");
        ArgumentNullException.ThrowIfNull(this.type_getter = type_getter, "type_getter");
        ArgumentNullException.ThrowIfNull(this.map_codec_getter = map_codec_getter, "map_codec_getter");
        ArgumentException.ThrowIfNullOrEmpty(this.type_field_name = type_field_name, "type_field_name");

        this.fallback = fallback;
        this.default_type = default_type;
    }

    /**
     * Initializes a new instance of the {@link DispatchableCodecWithFallback} class.
     * @param key_codec The {@link Codec} that de/encodes keys for identifying the mapping between {@link TYPE} to {@link T} instances.
     * @param type_field_name The name of the key field. Its value is de/encoded using the {@code key_codec} parameter.
     * @param type_getter The function that provides the type instance of a given object.
     * @param map_codec_getter The function that provides a {@link MapCodec} from a given type instance and can de/encode specific and derived objects of type {@link T}.
     * @param fallback The {@link Decoder} that will decode an object of type {@link T} if the data do not represent a map. <br />
     *                 (Do not worry that the type of this parameter is {@link Decoder}, you can still pass {@link Codec} instances to this) <br />
     *                 Can be {@code null}, in which case an error will be instead returned.
     * @implNote This constructor alternative delegates to {@link #DispatchableCodecWithFallback(Codec, String, Func2, Func2, Decoder, TYPE)} <br />
     * constructor and passes as the {@code default_type} parameter the value {@code null}.
     * @throws ArgumentException {@code type_field_name} parameter is the empty (&quot;&quot;) string, or it consists only of whitespace characters.
     * @throws ArgumentNullException {@code key_codec} and/or {@code type_field_name} and/or {@code type_getter} and/or {@code map_codec_getter} are {@code null}.
     */
    public DispatchableCodecWithFallback(
            Codec<TYPE> key_codec,
            String type_field_name,
            Func2<? super T, ? extends TYPE> type_getter,
            Func2<? super TYPE, ? extends MapCodec<? extends T>> map_codec_getter,
            @AllowNull Decoder<T> fallback
    ) throws ArgumentException, ArgumentNullException
    { this(key_codec, type_field_name, type_getter, map_codec_getter, fallback, null); }

    /**
     * Initializes a new instance of the {@link DispatchableCodecWithFallback} class.
     * @param key_codec The {@link Codec} that de/encodes keys for identifying the mapping between {@link TYPE} to {@link T} instances.
     * @param type_field_name The name of the key field. Its value is de/encoded using the {@code key_codec} parameter.
     * @param type_getter The function that provides the type instance of a given object.
     * @param map_codec_getter The function that provides a {@link MapCodec} from a given type instance and can de/encode specific and derived objects of type {@link T}.
     * @implNote This constructor alternative delegates to {@link #DispatchableCodecWithFallback(Codec, String, Func2, Func2, Decoder)} <br />
     * constructor and passes as the {@code fallback} parameter the value {@code null}.
     * @throws ArgumentException {@code type_field_name} parameter is the empty (&quot;&quot;) string, or it consists only of whitespace characters.
     * @throws ArgumentNullException {@code key_codec} and/or {@code type_field_name} and/or {@code type_getter} and/or {@code map_codec_getter} are {@code null}.
     */
    public DispatchableCodecWithFallback(
            Codec<TYPE> key_codec,
            String type_field_name,
            Func2<? super T, ? extends TYPE> type_getter,
            Func2<? super TYPE, ? extends MapCodec<? extends T>> map_codec_getter
    ) throws ArgumentException, ArgumentNullException { this(key_codec, type_field_name, type_getter, map_codec_getter, null); }

    /**
     * Initializes a new instance of the {@link DispatchableCodecWithFallback} class.
     * @param key_codec The {@link Codec} that de/encodes keys for identifying the mapping between {@link TYPE} to {@link T} instances.
     * @param type_getter The function that provides the type instance of a given object.
     * @param map_codec_getter The function that provides a {@link MapCodec} from a given type instance and can de/encode specific and derived objects of type {@link T}.
     * @implNote This constructor alternative delegates to {@link #DispatchableCodecWithFallback(Codec, String, Func2, Func2)} <br />
     * constructor and passes as the {@code type_field_name} parameter the value {@code "type"}. <br />
     * This is also the most simple use case that behaves exactly as the returned {@link Codec} object from the {@link Codec#dispatch(Function, Function)} function. <br />
     * For more advanced use cases see the other constructors documentation.
     * @throws ArgumentNullException {@code key_codec} and/or {@code type_getter} and/or {@code map_codec_getter} are {@code null}.
     */
    public DispatchableCodecWithFallback(
            Codec<TYPE> key_codec,
            Func2<? super T, ? extends TYPE> type_getter,
            Func2<? super TYPE, ? extends MapCodec<? extends T>> map_codec_getter
    ) throws ArgumentNullException { this(key_codec, "type", type_getter, map_codec_getter); }

    private <TI> DataResult<Pair<T, TI>> DecodeInternal(DynamicOps<TI> ops, TI input, MapLike<TI> like, TYPE t)
    {
        DataResult<? extends T> d = map_codec_getter.function(t).decode(ops, like);
        return d.isSuccess() ?
                DataResult.success(Pair.of(d.result().get(), input)) :
                DataResult.error(d.error().get().messageSupplier());
    }

    private <TG, TM extends T> RecordBuilder<TG> OfMapCodecIndirection(MapCodec<TM> c, DynamicOps<TG> ops, T input) { return c.encode((TM)input, ops, ops.mapBuilder()); }

    @Override
    public <TI> DataResult<Pair<T, TI>> decode(DynamicOps<TI> ops, TI input)
    {
        DataResult<MapLike<TI>> dr_like = ops.getMap(input);

        if (dr_like.isError()) {
            return (fallback == null) ?
                    CodecUtils.CreateDotNetFormattedErrorDataResult("Not a map: {0}", input) :
                    fallback.decode(ops, input);
        } else {
            MapLike<TI> like = dr_like.result().get();

            TI key = like.get(type_field_name);

            if (key == null) {
                return (default_type == null) ?
                        CodecUtils.CreateDotNetFormattedErrorDataResult("No field named as '{0}' was present in map", type_field_name) :
                        DecodeInternal(ops, input, like, default_type);
            } else {
                DataResult<TYPE> t = key_codec.parse(ops, key);
                if (t.isError()) {
                    return DataResult.error(t.error().get().messageSupplier());
                } else {
                    return DecodeInternal(ops, input, like, t.result().get());
                }
            }
        }
    }

    @Override
    public <TI> DataResult<TI> encode(T input, DynamicOps<TI> ops, TI prefix)
    {
        TYPE t = type_getter.function(input);

        RecordBuilder<TI> builder = OfMapCodecIndirection(map_codec_getter.function(t), ops, input);

        builder.add(type_field_name, key_codec.encode(t, ops, ops.empty()));

        return builder.build(prefix);
    }
}
