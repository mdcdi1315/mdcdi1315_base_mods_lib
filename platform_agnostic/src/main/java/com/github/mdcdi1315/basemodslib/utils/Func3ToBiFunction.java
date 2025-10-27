package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.util.function.BiFunction;

public record Func3ToBiFunction<T1, T2, TResult>(Func3<T1, T2, TResult> function)
    implements BiFunction<T1, T2, TResult>
{
    public Func3ToBiFunction {
        ArgumentNullException.ThrowIfNull(function, "function");
    }

    @Override
    public TResult apply(T1 t1, T2 t2) {
        return function.function(t1, t2);
    }
}
