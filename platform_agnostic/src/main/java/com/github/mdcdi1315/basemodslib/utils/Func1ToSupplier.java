package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.util.function.Supplier;

public record Func1ToSupplier<T>(Func1<T> function)
    implements Supplier<T>
{
    public Func1ToSupplier {
        ArgumentNullException.ThrowIfNull(function, "function");
    }

    @Override
    public T get() {
        return function.function();
    }
}
