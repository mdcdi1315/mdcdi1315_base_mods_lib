package com.github.mdcdi1315.DotNetLayer.System.Threading;

import com.github.mdcdi1315.DotNetLayer.System.Lazy1;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

/**
 * Specifies how a {@link Lazy1} instance should synchronize access among multiple threads.
 */
public enum LazyThreadSafetyMode
{
    /**
     * The {@link Lazy1} instance is not thread safe; if the instance is accessed from multiple threads, its behavior is undefined.
     * Use this mode only when high performance is crucial and the {@link Lazy1} instance is guaranteed never to be initialized from more than one thread.
     * If you use a {@link Lazy1} constructor that specifies an initialization method ({@code valueFactory} parameter), and if that initialization method throws an exception (or fails to handle an exception) the first time you call the {@link Lazy1#GetValue()} method,
     * then the exception is cached and thrown again on subsequent calls to the {@link Lazy1#GetValue()} method.
     * If you use a {@link Lazy1} constructor that does not specify an initialization method, exceptions that are thrown by the parameterless constructor for T are not cached.
     * In that case, a subsequent call to the {@link Lazy1#GetValue()} method might successfully initialize the {@link Lazy1} instance.
     * If the initialization method recursively accesses the {@link Lazy1#GetValue()} method of the {@link Lazy1} instance, an {@link InvalidOperationException} is thrown.
     */
    None,

    /**
     * When multiple threads try to initialize a {@link Lazy1} instance simultaneously, all threads are allowed to run the initialization method (or the parameterless constructor, if there is no initialization method).
     * The first thread to complete initialization sets the value of the {@link Lazy1} instance.
     * This is referred to as Publication in the field names.
     * That value is returned to any other threads that were simultaneously running the initialization method, unless the initialization method throws exceptions on those threads.
     * Any instances of T that were created by the competing threads are discarded.
     * Effectively, the publication of the initialized value is thread-safe in the sense that only one of the initialized values can be published and used by all threads.
     * If the initialization method throws an exception on any thread, the exception is propagated out of the {@link Lazy1#GetValue()} method on that thread.
     * The exception is not cached. The value of the IsValueCreated property remains false, and subsequent calls to the {@link Lazy1#GetValue()} method, either by the thread where the exception was thrown or by other threads, cause the initialization method to run again.
     * If the initialization method recursively accesses the {@link Lazy1#GetValue()} method of the {@link Lazy1} instance, no exception is thrown.
     */
    PublicationOnly,

    /**
     * Locks are used to ensure that only a single thread can initialize a {@link Lazy1} instance in a thread-safe manner. 
     * Effectively, the initialization method is executed in a thread-safe manner (referred to as Execution in the field name). 
     * Publication of the initialized value is also thread-safe in the sense that only one value may be published and used by all threads. 
     * If the initialization method (or the parameterless constructor, if there is no initialization method) uses locks internally, deadlocks can occur.
     * If you use a {@link Lazy1} constructor that specifies an initialization method ({@code valueFactory} parameter), and if that initialization method throws an exception (or fails to handle an exception) the first time you call the {@link Lazy1#GetValue()} method, then the exception is cached and thrown again on subsequent calls to the {@link Lazy1#GetValue()} method. 
     * If you use a {@link Lazy1} constructor that does not specify an initialization method, exceptions that are thrown by the parameterless constructor for T are not cached. 
     * In that case, a subsequent call to the {@link Lazy1#GetValue()} method might successfully initialize the {@link Lazy1} instance. 
     * If the initialization method recursively accesses the {@link Lazy1#GetValue()} method of the {@link Lazy1} instance, an {@link InvalidOperationException} is thrown.
     */
    ExecutionAndPublication
}
