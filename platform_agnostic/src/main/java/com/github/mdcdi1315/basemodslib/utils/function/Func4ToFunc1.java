package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func4;

record Func4ToFunc1<T1, T2, T3, TR>(Func4<T1, T2, T3, TR> func, T1 input_1, T2 input_2, T3 input_3)
    implements Func1<TR>
{
    @Override
    public TR get() { return func.function(input_1, input_2, input_3); }

    @Override
    public TR function() { return func.function(input_1, input_2, input_3); }
}
