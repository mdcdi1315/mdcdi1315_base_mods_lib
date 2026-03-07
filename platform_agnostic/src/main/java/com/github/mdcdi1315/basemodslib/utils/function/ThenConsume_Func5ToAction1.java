package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Func5;

record ThenConsume_Func5ToAction1<T1, T2, T3, T4, TR>(Func5<T1, T2, T3, T4, TR> f, Action1<TR> consumer)
    implements Func5<T1, T2, T3, T4, TR>
{
    @Override
    public TR function(T1 input_1, T2 input_2, T3 input_3, T4 input_4)
    {
        TR result = f.function(input_1, input_2, input_3, input_4);
        consumer.action(result);
        return result;
    }
}
