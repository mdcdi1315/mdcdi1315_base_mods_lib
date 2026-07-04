package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.DotNetLayer.System.Threading.LazyThreadSafetyMode;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides support for lazy initialization.
 * @param <T> The type of object that is being lazily initialized.
 */
public class Lazy1<T>
{
    // _state, a volatile reference, is set to null after _value has been set
    private final AtomicReference<LazyHelper> _state;

    // we ensure that _factory when finished is set to null to allow garbage collector to clean up
    // any referenced items
    @AllowNull
    private Func1<T> _factory;

    // _value eventually stores the lazily created value. It is valid when _state = null.
    @AllowNull
    private T _value;

    /**
     * Initializes a new instance of the {@link Lazy1} class that uses a pre-initialized specified value.
     * @param value The preinitialized value to be used.
     */
    public Lazy1(@AllowNull T value)
    {
        _state = new AtomicReference<>();
        _value = value;
    }

    /**
     * Initializes a new instance of the {@link Lazy1} class. When lazy initialization occurs, the specified initialization function is used.
     * @param valueFactory The delegate that is invoked to produce the lazily initialized value when it is needed.
     * @throws ArgumentNullException {@code valueFactory} is {@code null}.
     */
    public Lazy1(Func1<T> valueFactory)
            throws ArgumentNullException
    {
        this(valueFactory, LazyThreadSafetyMode.ExecutionAndPublication);
    }

    /**
     * Initializes a new instance of the {@link Lazy1} class.
     * When lazy initialization occurs, the specified initialization function and initialization mode are used.
     * @param valueFactory The delegate that is invoked to produce the lazily initialized value when it is needed.
     * @param isThreadSafe {@code true} to make this instance usable concurrently by multiple threads; {@code false} to make this instance usable by only one thread at a time.
     * @throws ArgumentNullException {@code valueFactory} is {@code null}.
     */
    public Lazy1(Func1<T> valueFactory, boolean isThreadSafe)
        throws ArgumentNullException
    {
        this(valueFactory, LazyHelper.GetModeFromIsThreadSafe(isThreadSafe));
    }

    /**
     * Initializes a new instance of the {@link Lazy1} class that uses the specified initialization function and thread-safety mode.
     * @param valueFactory The delegate that is invoked to produce the lazily initialized value when it is needed.
     * @param mode One of the enumeration values that specifies the thread safety mode.
     * @throws ArgumentNullException {@code valueFactory} is {@code null}.
     */
    public Lazy1(Func1<T> valueFactory, LazyThreadSafetyMode mode)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(valueFactory, "valueFactory");

        _factory = valueFactory;
        _state = new AtomicReference<>();
        _state.set(LazyHelper.Create(mode));
    }

    private void ViaFactory(LazyThreadSafetyMode mode)
    {
        try
        {
            if (_factory == null) {
                throw new InvalidOperationException("Factory is null");
            } else {
                Func1<T> factory = _factory;
                _factory = null;

                _value = factory.function();
                _state.set(null); // volatile write, must occur after setting _value
            }
        }
        catch (Exception exception)
        {
            _state.set(new LazyHelper(mode, exception));
            throw exception;
        }
    }

    private void ExecutionAndPublication(LazyHelper executionAndPublication)
    {
        synchronized (executionAndPublication)
        {
            // it's possible for multiple calls to have piled up behind the lock, so we need to check
            // to see if the ExecutionAndPublication object is still the current implementation.
            if (_state.get() == executionAndPublication)
            {
                ViaFactory(LazyThreadSafetyMode.ExecutionAndPublication);
            }
        }
    }

    private void PublicationOnly(LazyHelper publicationOnly, T possibleValue)
    {
        LazyHelper previous = _state.compareAndExchange(publicationOnly, LazyHelper.PublicationOnlyWaitForOtherThreadToPublish);
        if (previous == publicationOnly)
        {
            _factory = null;
            _value = possibleValue;
            _state.set(null); // volatile write, must occur after setting _value
        }
    }

    private void PublicationOnlyViaFactory(LazyHelper initializer)
    {
        Func1<T> factory = _factory;
        if (factory == null)
        {
            PublicationOnlyWaitForOtherThreadToPublish();
        }
        else
        {
            PublicationOnly(initializer, factory.function());
        }
    }

    private void PublicationOnlyWaitForOtherThreadToPublish()
    {
        while (_state.get() != null)
        {
            // We get here when PublicationOnly temporarily sets _state to LazyHelper.PublicationOnlyWaitForOtherThreadToPublish.
            // This temporary state should be quickly followed by _state being set to null.
            Thread.onSpinWait();
        }
    }

    private T CreateValue()
    {
        // we have to create a copy of state here, and use the copy exclusively from here on in
        // so as to ensure thread safety.
        LazyHelper state = _state.get();
        if (state != null)
        {
            switch (state.GetState())
            {
                case LazyState.NoneViaFactory:
                    ViaFactory(LazyThreadSafetyMode.None);
                    break;

                case LazyState.PublicationOnlyViaFactory:
                    PublicationOnlyViaFactory(state);
                    break;

                case LazyState.PublicationOnlyWait:
                    PublicationOnlyWaitForOtherThreadToPublish();
                    break;

                case LazyState.ExecutionAndPublicationViaFactory:
                    ExecutionAndPublication(state);
                    break;

                default:
                    state.ThrowException();
                    break;
            }
        }
        return _value;
    }

    /**
     * Gets a value that indicates whether a value has been created for this {@link Lazy1} instance.
     * @return {@code true} if a value has been created for this {@link Lazy1} instance; otherwise, {@code false}.
     */
    public boolean IsValueCreated() { return _state.get() == null; }

    /**
     * Creates and returns a string representation of this instance.
     * @return The result of calling {@link T#toString()} on {@link #GetValue()}.
     * @throws NullPointerException {@link #GetValue()} is {@code null}.
     */
    @NotNull
    public String toString()
    {
        return IsValueCreated() ?
                _value.toString() : // Throws NullReferenceException as if caller called ToString on the value itself
                "Value is not created yet.";
    }

    /**
     * Gets the lazily initialized value of the current {@link Lazy1} instance.
     * @return The lazily initialized value of the current {@link Lazy1} instance.
     * @throws InvalidOperationException The initialization function tries to access {@link #GetValue()} on this instance.
     */
    @MaybeNull
    public T GetValue()
            throws InvalidOperationException
    {
        return _state.get() == null ? _value : CreateValue();
    }
}
