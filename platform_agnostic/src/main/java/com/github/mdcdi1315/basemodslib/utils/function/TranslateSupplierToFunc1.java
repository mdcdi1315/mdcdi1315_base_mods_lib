package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func1;

import java.util.function.Supplier;

record TranslateSupplierToFunc1<T>(Supplier<T> supplier)
    implements Func1<T>
{
    @Override
    public T get() { return supplier.get(); }

    @Override
    public T function() { return supplier.get(); }
}
