package com.github.mdcdi1315.DotNetLayer.System.Threading;

import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Represents a method to be called when a message is to be dispatched to a synchronization context.
 */
@FunctionalInterface
@DotNetDelegateInterface
public interface SendOrPostCallback
{
    /**
     * @param state The object passed to the delegate.
     */
    void callback(@AllowNull Object state);
}
