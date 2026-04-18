package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Converter;

record Func2ToConverter<TI, TO>(Func2<TI, TO> function)
    implements Converter<TI, TO>
{
    @Override
    public TO convert(TI input) { return function.function(input); }
}
