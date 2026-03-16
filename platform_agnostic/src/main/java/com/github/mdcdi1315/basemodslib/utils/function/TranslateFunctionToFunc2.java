package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import java.util.function.Function;

record TranslateFunctionToFunc2<T, TR>(Function<T, TR> function)
    implements Func2<T, TR>
{
    @Override
    public TR apply(T t) { return function.apply(t); }

    @Override
    public TR function(T input) { return function.apply(input); }
}
