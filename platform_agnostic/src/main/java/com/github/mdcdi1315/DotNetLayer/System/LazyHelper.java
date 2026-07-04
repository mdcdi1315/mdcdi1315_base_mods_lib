package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DoesNotReturn;

import com.github.mdcdi1315.DotNetLayer.System.Threading.LazyThreadSafetyMode;

/// <summary>
/// LazyHelper serves multiples purposes
/// - minimizing code size of Lazy&lt;T&gt; by implementing as much of the code that is not generic
///   this reduces generic code bloat, making faster class initialization
/// - contains singleton objects that are used to handle threading primitives for PublicationOnly mode
/// - allows for instantiation for ExecutionAndPublication so as to create an object for locking on
/// - holds exception information.
/// </summary>
final class LazyHelper
{
    static final LazyHelper NoneViaFactory                = new LazyHelper(LazyState.NoneViaFactory);
    static final LazyHelper PublicationOnlyViaFactory     = new LazyHelper(LazyState.PublicationOnlyViaFactory);
    static final LazyHelper PublicationOnlyWaitForOtherThreadToPublish       = new LazyHelper(LazyState.PublicationOnlyWait);

    private LazyState state;

    private Exception exception;

    public LazyState GetState() { return state; }

    /// <summary>
    /// Constructor that defines the state
    /// </summary>
    LazyHelper(LazyState state)
    {
        this.state = state;
    }

    /// <summary>
    /// Constructor used for exceptions
    /// </summary>
    LazyHelper(LazyThreadSafetyMode mode, Exception exception)
    {
        switch (mode)
        {
            case LazyThreadSafetyMode.ExecutionAndPublication:
                state = LazyState.ExecutionAndPublicationException;
                break;

            case LazyThreadSafetyMode.None:
                state = LazyState.NoneException;
                break;

            case LazyThreadSafetyMode.PublicationOnly:
                state = LazyState.PublicationOnlyException;
                break;

            default:
                // Debug.Fail("internal constructor, this should never occur");
                break;
        }

        this.exception = exception;
    }

    @DoesNotReturn
    @StackTraceHidden
    void ThrowException()
    {
        // Debug.Assert(_exceptionDispatch != null, "execution path is invalid");
        throw exception;
    }

    private LazyThreadSafetyMode GetMode()
    {
        switch (state)
        {
            case LazyState.NoneViaConstructor:
            case LazyState.NoneViaFactory:
            case LazyState.NoneException:
                return LazyThreadSafetyMode.None;

            case LazyState.PublicationOnlyViaConstructor:
            case LazyState.PublicationOnlyViaFactory:
            case LazyState.PublicationOnlyWait:
            case LazyState.PublicationOnlyException:
                return LazyThreadSafetyMode.PublicationOnly;

            case LazyState.ExecutionAndPublicationViaConstructor:
            case LazyState.ExecutionAndPublicationViaFactory:
            case LazyState.ExecutionAndPublicationException:
                return LazyThreadSafetyMode.ExecutionAndPublication;

            default:
                // Debug.Fail("Invalid logic; State should always have a valid value");
                return LazyThreadSafetyMode.None;
        }
    }

    @MaybeNull
    static LazyThreadSafetyMode GetMode(@AllowNull LazyHelper state)
    {
        if (state == null)
            return null; // we don't know the mode anymore
        return state.GetMode();
    }

    static boolean GetIsValueFaulted(LazyHelper state) { return state.exception != null; }

    static LazyHelper Create(LazyThreadSafetyMode mode)
    {
        switch (mode)
        {
            case LazyThreadSafetyMode.None:
                return NoneViaFactory;

            case LazyThreadSafetyMode.PublicationOnly:
                return PublicationOnlyViaFactory;

            case LazyThreadSafetyMode.ExecutionAndPublication:
                // we need to create an object for ExecutionAndPublication because we use Monitor-based locking
                LazyState state = LazyState.ExecutionAndPublicationViaFactory;
                return new LazyHelper(state);

            default:
                throw new ArgumentOutOfRangeException("mode", "Mode is invalid");
        }
    }

    static LazyThreadSafetyMode GetModeFromIsThreadSafe(boolean isThreadSafe)
    {
        return isThreadSafe ? LazyThreadSafetyMode.ExecutionAndPublication : LazyThreadSafetyMode.None;
    }
}