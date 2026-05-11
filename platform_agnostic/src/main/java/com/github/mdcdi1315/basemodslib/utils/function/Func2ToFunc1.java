package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;

record Func2ToFunc1<T, TR>(Func2<T, TR> fc, T input)
    implements Func1<TR>
{
    @Override
    public TR get() { return fc.function(input); }

    @Override
    public TR function() { return fc.function(input); }
}
