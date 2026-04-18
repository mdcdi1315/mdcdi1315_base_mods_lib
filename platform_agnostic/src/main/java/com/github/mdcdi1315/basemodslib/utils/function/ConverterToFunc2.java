package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Converter;

record ConverterToFunc2<TI, TO>(Converter<TI, TO> converter)
        implements Func2<TI, TO>
{
    @Override
    public TO apply(TI input) { return converter.convert(input); }

    @Override
    public TO function(TI input) { return converter.convert(input); }
}