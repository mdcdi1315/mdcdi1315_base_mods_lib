package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.function.Supplier;

/**
 * Provides a way for creating {@link Supplier} objects that their return values are already known from the context they are called into. <br />
 * This does provide a performance optimization since that value is just retrieved through an internal field that this class holds. <br />
 * Can also be further extended for providing other functional interfaces too, or for special cases.
 * @param <T> The type of the element to supply.
 */
public class ElementSupplier<T>
    implements Supplier<T>, Func1<T>
{
    @AllowNull
    private final T value;

    public ElementSupplier(@MaybeNull T item) {
        value = item;
    }

    @Override
    @MaybeNull
    public T function() { return value; }

    @Override
    @MaybeNull
    public T get() { return value; }
}
