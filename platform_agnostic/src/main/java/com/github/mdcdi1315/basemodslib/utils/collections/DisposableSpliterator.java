package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.DisposableSpliteratorImplFromEnumerator;

import java.util.Spliterator;

/**
 * Provides a way for surfacing disposable spliterator objects to Java.
 * @param <T> The type of the elements that the spliterator does return.
 * @since 1.0.31
 */
public interface DisposableSpliterator<T>
    extends Spliterator<T>, AutoCloseable
{
    /**
     * Disposes this {@link DisposableSpliterator} instance, freeing any native resources used by the iterator.
     */
    @Override
    void close();

    /**
     * Provides a default {@link DisposableSpliterator} implementation for an existing {@link IEnumerator} instance.
     * @param enumerator The {@link IEnumerator} instance to wrap.
     * @return The wrapped {@link IEnumerator} instance as a {@link DisposableSpliterator} instance.
     * @param <T> The type of the elements that the wrapping enumerator returns.
     * @throws ArgumentNullException {@code enumerator} is {@code null}.
     */
    static <T> DisposableSpliterator<T> FromEnumerator(IEnumerator<T> enumerator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerator, "enumerator");
        return new DisposableSpliteratorImplFromEnumerator<>(enumerator);
    }
}
