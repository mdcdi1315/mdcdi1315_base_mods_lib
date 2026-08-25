package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.IDisposable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides a way to safely wrap and access a thread-unsafe object as a thread-safe object.
 * @param <T> The type of the object to wrap.
 * @since 1.0.37
 */
public final class ThreadSafeObject<T>
    implements ISynchronized
{
    private T value;
    private final ReentrantLock lock;

    /**
     * Initializes a new instance of the {@link ThreadSafeObject} class.
     * The wrapped object value will be {@code null}.
     */
    public ThreadSafeObject()
    {
        value = null;
        lock = new ReentrantLock();
    }

    /**
     * Initializes a new instance of the {@link ThreadSafeObject} class,
     * with an initial value of the wrapped object.
     * @param value The initial value. Can be {@code null}.
     */
    public ThreadSafeObject(@AllowNull T value)
    {
        this.value = value;
        lock = new ReentrantLock();
    }

    /**
     * A 'handle' class providing thread-safe access to an object for modification. <br />
     * Returned by the {@link ThreadSafeObject#Acquire()} method.
     * @param <T> The type of the object to modify.
     */
    public static final class LockContext<T>
        implements IDisposable
    {
        private boolean disposed;
        private final T last_value;
        private final ReentrantLock lock;

        private LockContext(ReentrantLock lock, T value)
        {
            disposed = false;
            this.lock = lock;
            this.last_value = value;
        }

        /**
         * Gets the value of the object. It can be {@code null}.
         * @return The currently acquired value of the object.
         */
        @Pure
        @MaybeNull
        public T GetValue() { return last_value; }

        /**
         * Releases the context, so that other threads can access the current object.
         */
        @Override
        public void Dispose() { if (disposed) return; lock.unlock(); disposed = true; }
    }

    /**
     * Acquires a {@link LockContext} that provides the value. <br />
     * It will acquire access first before returning the lock context to
     * access the value.
     * @return A {@link LockContext} that provides the acquired value at the time it was retrieved.
     */
    @NotNull
    public LockContext<T> Acquire()
    {
        lock.lock();
        try {
            return new LockContext<>(lock, value);
        } catch (Throwable e) {
            // Unlock the object if the creation of the context fails
            lock.unlock();
            throw e;
        }
    }

    /**
     * Assigns a new object value to the internally kept field.
     * @param value The new value to assign. This particular assignment is thread-safe.
     */
    public void SetValue(@AllowNull T value)
    {
        lock.lock();
        try {
            this.value = value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks whether the internally field does not have a valid value. <br />
     * This check does not acquire the lock.
     * @return {@code true} if the internal field is {@code null}.
     */
    public boolean IsNull() { return value == null; }
}
