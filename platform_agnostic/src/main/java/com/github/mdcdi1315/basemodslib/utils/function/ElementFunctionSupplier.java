package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;

/**
 * Provides an extendant of the {@link ElementSupplier} class, in that this class
 * does also implement the {@link Func2} functional interface, that does
 * always ignore the value of it's input parameter and just returns
 * the element's value.
 * @param <TInput> The type of the input parameter that is by default ignored.
 * @param <TItem> The type of the item that is returned.
 * @since 1.0.26
 */
public class ElementFunctionSupplier<TInput, TItem>
    extends ElementSupplier<TItem>
    implements Func2<TInput, TItem>
{
    public ElementFunctionSupplier(@AllowNull TItem item) { super(item); }

    @Override
    public TItem apply(TInput ignored) { return function(); }

    @Override
    public TItem function(TInput ignored) { return function(); }
}
