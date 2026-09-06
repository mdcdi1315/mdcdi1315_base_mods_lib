package com.github.mdcdi1315.DotNetLayer.System.Threading;

import com.github.mdcdi1315.DotNetLayer.System.NullReferenceException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.concurrent.ForkJoinPool;

/**
 * Provides the basic functionality for propagating a synchronization context in various synchronization models.
 */
public class SynchronizationContext
{
    private static final ThreadLocal<SynchronizationContext> context_holder = new ThreadLocal<>();

    private boolean _requireWaitNotification;

    /**
     * Creates a new instance of the {@link SynchronizationContext} class.
     */
    public SynchronizationContext()
    {
        _requireWaitNotification = false;
    }

    private record ThreadRunnable(SendOrPostCallback callback, Object state)
            implements Runnable
    {
        @Override
        public void run() { callback.callback(state); }
    }

    /**
     * Gets the synchronization context for the current thread.
     * @return A {@link SynchronizationContext} object representing the current synchronization context.
     */
    @MaybeNull
    public static SynchronizationContext GetCurrent() { return context_holder.get(); }

    /**
     * Sets notification that wait notification is required and prepares the callback method so it can be called more reliably when a wait occurs.
     */
    protected final void SetWaitNotificationRequired() { _requireWaitNotification = true; }

    /**
     * Determines if wait notification is required.
     * @return {@code true} if wait notification is required; otherwise, {@code false}.
     */
    public boolean IsWaitNotificationRequired() { return _requireWaitNotification; }

    /**
     * When overridden in a derived class, dispatches a synchronous message to a synchronization context.
     * @param d The {@link SendOrPostCallback} delegate to call.
     * @param state The object passed to the delegate.
     */
    public void Send(SendOrPostCallback d, @AllowNull Object state)
    {
        if (d == null) {
            throw new NullReferenceException();
        } else {
            d.callback(state);
        }
    }

    /**
     * When overridden in a derived class, dispatches an asynchronous message to a synchronization context.
     * @param d The {@link SendOrPostCallback} delegate to call.
     * @param state The object passed to the delegate.
     */
    @SuppressWarnings("resource")
    public void Post(SendOrPostCallback d, @AllowNull Object state)
    {
        if (d == null) {
            throw new NullReferenceException();
        } else {
            ForkJoinPool.commonPool().execute(new ThreadRunnable(d, state));
        }
    }

    /**
     * Optional override for subclasses, for responding to notification that operation is starting.
     */
    public void OperationStarted() { }

    /**
     * Optional override for subclasses, for responding to notification that operation has completed.
     */
    public void OperationCompleted() { }

    /**
     * Sets the current synchronization context.
     * @param syncContext The {@link SynchronizationContext} object to be set.
     */
    public static void SetSynchronizationContext(@AllowNull SynchronizationContext syncContext) { context_holder.set(syncContext); }

    /**
     * When overridden in a derived class, creates a copy of the synchronization context.
     * @return A new {@link SynchronizationContext} object.
     */
    public SynchronizationContext CreateCopy() { return new SynchronizationContext(); }
}
