package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public final class DelayLoadedCodec<T>
    implements Codec<T>
{
    private Codec<T> actual;
    private Func1<Codec<T>> getter;

    public DelayLoadedCodec(Func1<Codec<T>> supplier)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(supplier , "supplier");
        getter = supplier;
        actual = null;
    }

    private void EnsureLoaded()
    {
        if (actual == null)
        {
            actual = getter.function();
            getter = null;
        }
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        EnsureLoaded();
        return actual.decode(ops , input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        EnsureLoaded();
        return actual.encode(input , ops , prefix);
    }
}
