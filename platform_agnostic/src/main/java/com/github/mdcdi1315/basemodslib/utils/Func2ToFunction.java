package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.util.function.Function;

public record Func2ToFunction<T, TR>(Func2<T , TR> function)
    implements Function<T , TR>
{
    public Func2ToFunction {
        ArgumentNullException.ThrowIfNull(function, "function");
    }

    @Override
    public TR apply(T t) {
        return function.function(t);
    }
}
