package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func4;
import com.github.mdcdi1315.DotNetLayer.System.Action1;

record ThenConsume_Func4ToAction1<T1, T2, T3, TR>(Func4<T1, T2, T3, TR> f, Action1<TR> consumer)
    implements Func4<T1, T2, T3, TR>
{
    @Override
    public TR function(T1 input_1, T2 input_2, T3 input_3)
    {
        TR result = f.function(input_1, input_2, input_3);
        consumer.action(result);
        return result;
    }
}
