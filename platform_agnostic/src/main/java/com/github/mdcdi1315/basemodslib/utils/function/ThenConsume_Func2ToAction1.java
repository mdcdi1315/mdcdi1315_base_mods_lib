package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action1;

record ThenConsume_Func2ToAction1<TS, TR>(Func2<TS, TR> f, Action1<TR> consumer)
    implements Func2<TS, TR>
{
    @Override
    public TR function(TS input)
    {
        TR result = f.function(input);
        consumer.action(result);
        return result;
    }

    @Override
    public TR apply(TS input) // Also override this for faster path
    {
        TR result = f.function(input);
        consumer.action(result);
        return result;
    }
}
