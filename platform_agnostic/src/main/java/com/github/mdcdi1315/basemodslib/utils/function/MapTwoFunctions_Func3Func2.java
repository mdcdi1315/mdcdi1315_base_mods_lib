package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;

record MapTwoFunctions_Func3Func2<T1, T2, TM, TR>(Func3<T1, T2, TM> f, Func2<TM, TR> mapper)
    implements Func3<T1, T2, TR>
{
    @Override
    public TR apply(T1 t1, T2 t2) { return mapper.function(f.function(t1, t2)); }

    @Override
    public TR function(T1 input_1, T2 input_2) { return mapper.function(f.function(input_1, input_2)); }
}
