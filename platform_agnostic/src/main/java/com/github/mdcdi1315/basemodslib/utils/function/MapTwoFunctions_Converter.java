package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Converter;

record MapTwoFunctions_Converter<TI, TM, TO>(Converter<TI, TM> c1, Converter<TM, TO> c2)
    implements Converter<TI, TO>
{
    @Override
    public TO convert(TI input) { return c2.convert(c1.convert(input)); }
}
