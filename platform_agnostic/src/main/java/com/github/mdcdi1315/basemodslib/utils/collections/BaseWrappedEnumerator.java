package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides an abstract implementation of the {@link BaseEnumerator} class by defining
 * an enumerator that is being backed by another existing enumerator.
 * @param <T> The type of the elements to enumerate.
 * @since 1.0.26
 */
public abstract class BaseWrappedEnumerator<T>
    extends BaseEnumerator<T>
{
    private IEnumerator<T> wrapped;

    /**
     * Initializes a new instance of the {@link BaseWrappedEnumerator} class.
     * @param enumerator_to_wrap The {@link IEnumerator} instance to wrap.
     * @throws ArgumentNullException {@code enumerator_to_wrap} is {@code null}.
     */
    public BaseWrappedEnumerator(IEnumerator<T> enumerator_to_wrap)
            throws ArgumentNullException
    {
        super();
        ArgumentNullException.ThrowIfNull(wrapped = enumerator_to_wrap, "enumerator_to_wrap");
    }

    @Override
    public abstract T getCurrent();

    @Override
    protected abstract void ResetImpl();

    @Override
    protected abstract boolean MoveNextImpl();

    /**
     * Provides the signature for disposing additional fields. <br />
     * This is provided because the {@link #Dispose()} method is final and cannot be overridden.
     */
    protected void DisposeAdditional() {}

    /**
     * Provides the wrapped enumerator implementation given through the {@link #BaseWrappedEnumerator(IEnumerator)} constructor.
     * @return The stored {@link IEnumerator} instance.
     */
    @MaybeNull
    protected final IEnumerator<T> GetWrapped() { return wrapped; }

    @Override
    public final void Dispose()
    {
        synchronized (this)
        {
            try {
                super.Dispose();
                if (wrapped != null) { wrapped.Dispose(); }
            } finally {
                wrapped = null;
                DisposeAdditional();
            }
        }
    }
}
