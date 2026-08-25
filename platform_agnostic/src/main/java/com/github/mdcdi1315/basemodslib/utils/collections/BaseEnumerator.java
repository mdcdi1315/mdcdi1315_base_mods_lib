package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ObjectDisposedException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides the base layout and enumeration services for all the {@link IEnumerator} implementations provided in this package.
 * @param <T> The type of the elements to be enumerated.
 */
public abstract class BaseEnumerator<T>
        implements IEnumerator<T>
{
    private volatile boolean not_disposed, not_reset;

    /**
     * Initializes an instance of the {@link BaseEnumerator} class.
     */
    @Pure
    protected BaseEnumerator()
    {
        not_reset = true;
        not_disposed = true;
    }

    @MaybeNull
    public abstract T getCurrent();

    /**
     * Disposes this {@link BaseEnumerator} instance. <br />
     * Implementers overriding this MUST also call this method as well.
     */
    @Pure
    public void Dispose() { not_disposed = false; }

    /**
     * Advances the enumerator to the next element of the collection.
     * @return {@code true} if the enumerator was successfully advanced to the next element; {@code false} if the enumerator has passed the end of the collection.
     * @exception InvalidOperationException The collection was modified after the enumerator was created.
     */
    public final boolean MoveNext()
            throws InvalidOperationException
    {
        if (not_disposed && not_reset) {
            if (MoveNextImpl()) {
                return true;
            } else {
                not_reset = false;
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Sets the enumerator to its initial position, which is before the first element in the collection.
     * @throws ObjectDisposedException The current enumerator instance is now disposed.
     * @throws InvalidOperationException The collection was modified after the enumerator was created.
     */
    public final void Reset()
            throws ObjectDisposedException, InvalidOperationException
    {
        ObjectDisposedException.ThrowIf(!not_disposed , this);
        ResetImpl();
        not_reset = true;
    }

    /**
     * Defines the actual implementation of the {@link #Reset()} method.
     * @throws InvalidOperationException The collection was modified after the enumerator was created.
     */
    protected abstract void ResetImpl() throws InvalidOperationException;

    /**
     * Defines the actual implementation of the {@link #MoveNext()} method.
     * @return A value whether the enumerator moved successfully to the next element.
     * @throws InvalidOperationException The collection was modified after the enumerator was created.
     */
    protected abstract boolean MoveNextImpl() throws InvalidOperationException;
}
