package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func3;

import java.util.function.BiFunction;

record TranslateBiFunctionToFunc3<T1, T2, TR>(BiFunction<T1, T2, TR> biFunction)
    implements Func3<T1, T2, TR>
{
    @Override
    public TR apply(T1 t1, T2 t2) { return biFunction.apply(t1, t2); }

    @Override
    public TR function(T1 input_1, T2 input_2) { return biFunction.apply(input_1, input_2); }
}
