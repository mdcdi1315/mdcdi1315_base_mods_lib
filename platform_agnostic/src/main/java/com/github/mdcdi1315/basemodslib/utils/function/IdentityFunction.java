package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Func2;

import java.util.function.Function;

record IdentityFunction<T>()
    implements Func2<T, T>
{
    @Override
    public T apply(T t) { return t; } // Also override this for faster path

    @Override
    public T function(T input) { return input; }

    @NotNull
    @Override
    public <V> Function<T, V> andThen(@NotNull Function<? super T, ? extends V> after) { return (Function<T, V>) after; }

    @NotNull
    @Override
    public <V> Function<V, T> compose(@NotNull Function<? super V, ? extends T> before) { return (Function<V, T>) before; }
}
