package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a {@link Codec} implementation that does always decode into the specified value or the supplier returning the value. <br />
 * Encoding for this codec does nothing; that means that encoding with this will always return an empty map. <br />
 * If the supplier constructor alternative is used, the supplier will only be invoked once and the value returned by the supplier will be stored to a field.
 * @param <T> The type of the object to always return.
 */
public final class LazyUnitCodec<T>
    implements Codec<T>
{
    private T value;
    private Func1<T> value_getter;

    /**
     * Creates a new instance of the {@link LazyUnitCodec} class from the specified supplying function that will be called only at the first time a decoding request is acknowledged.
     * @param getter The supplying function providing the type {@link T}.
     * @throws ArgumentNullException {@code getter} is {@code null}.
     */
    public LazyUnitCodec(Func1<T> getter)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(getter , "getter");
        value = null;
        value_getter = getter;
    }

    /**
     * Creates a new instance of the {@link LazyUnitCodec} class directly from the specified value.
     * @param direct_value The value to initialize this codec class instance from.
     * @throws ArgumentNullException {@code direct_value} is {@code null}.
     */
    public LazyUnitCodec(T direct_value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(direct_value, "direct_value");
        value = direct_value;
        value_getter = null;
    }

    private synchronized void EnsureLoaded()
    {
        if (value == null)
        {
            value = value_getter.function();
            value_getter = null;
        }
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        EnsureLoaded();
        return DataResult.success(Pair.of(value , input));
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return ops.mapBuilder().build(prefix); // Create an empty map instead.
    }
}
