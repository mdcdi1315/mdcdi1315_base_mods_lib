package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

record MapTwoFunctions_Func2<TS, TM, TR>(Func2<TS, TM> f_1, Func2<TM, TR> f_2)
    implements Func2<TS, TR>
{
    @Override
    public TR apply(TS ts) { return f_2.function(f_1.function(ts)); }

    @Override
    public TR function(TS input) { return f_2.function(f_1.function(input)); }
}
