package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.DisposableIteratorImplFromEnumerator;

import java.util.Iterator;

/**
 * Provides a way for surfacing disposable iterator objects to Java.
 * @param <T> The type of the elements that the iterator does return.
 * @since 1.0.31
 */
public interface DisposableIterator<T>
    extends Iterator<T>, AutoCloseable
{
    /**
     * Disposes this {@link DisposableIterator} instance, freeing any native resources used by the iterator.
     */
    @Override
    void close();

    /**
     * Provides a default {@link DisposableIterator} implementation for an existing {@link IEnumerator} instance.
     * @param enumerator The {@link IEnumerator} instance to wrap.
     * @return The wrapped {@link IEnumerator} instance as a {@link DisposableIterator} instance.
     * @param <T> The type of the elements that the wrapping enumerator returns.
     * @throws ArgumentNullException {@code enumerator} is {@code null}.
     */
    @NotNull
    static <T> DisposableIterator<T> FromEnumerator(IEnumerator<T> enumerator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerator, "enumerator");
        return new DisposableIteratorImplFromEnumerator<>(enumerator);
    }
}
