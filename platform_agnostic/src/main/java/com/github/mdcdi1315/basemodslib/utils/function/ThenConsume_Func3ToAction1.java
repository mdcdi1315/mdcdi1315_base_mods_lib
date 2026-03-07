package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.Action1;

record ThenConsume_Func3ToAction1<T1, T2, TR>(Func3<T1, T2, TR> f, Action1<TR> consumer)
    implements Func3<T1, T2, TR>
{
    @Override
    public TR function(T1 input_1, T2 input_2)
    {
        TR result = f.function(input_1, input_2);
        consumer.action(result);
        return result;
    }

    @Override // Also overridden for faster path
    public TR apply(T1 t1, T2 t2)
    {
        TR result = f.function(t1, t2);
        consumer.action(result);
        return result;
    }
}
