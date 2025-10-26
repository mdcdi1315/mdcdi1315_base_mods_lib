package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import java.util.function.Function;

public record Func2ToFunction<T, TR>(Func2<T , TR> function)
    implements Function<T , TR>
{
    @Override
    public TR apply(T t) {
        return function.function(t);
    }
}
