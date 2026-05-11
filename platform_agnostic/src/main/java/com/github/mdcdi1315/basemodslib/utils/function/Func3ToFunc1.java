package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func3;

record Func3ToFunc1<T1, T2, TR>(Func3<T1, T2, TR> func, T1 input_1, T2 input_2)
    implements Func1<TR>
{
    @Override
    public TR get() { return func.function(input_1, input_2); }

    @Override
    public TR function() { return func.function(input_1, input_2); }
}
