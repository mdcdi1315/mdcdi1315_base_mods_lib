package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides a way for creating {@link java.util.function.Supplier} objects that their return values are already known from the context they are called into. <br />
 * This does provide a performance optimization since that value is just retrieved through an internal field that this class holds. <br />
 * Can also be further extended for providing other functional interfaces too, or for special cases. <br />
 * Note also that this class implements the {@link Func1} functional interface as well.
 * @param <T> The type of the element to supply.
 */
public class ElementSupplier<T>
    implements Func1<T>
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
