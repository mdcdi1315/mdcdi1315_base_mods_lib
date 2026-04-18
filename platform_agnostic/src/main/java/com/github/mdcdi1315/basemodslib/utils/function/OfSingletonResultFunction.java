package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;

record OfSingletonResultFunction<TI, TR>(Func1<TR> function)
    implements Func2<TI, TR>
{
    @Override
    public TR apply(TI input) { return function.function(); }

    @Override
    public TR function(TI input) { return function.function(); }
}
