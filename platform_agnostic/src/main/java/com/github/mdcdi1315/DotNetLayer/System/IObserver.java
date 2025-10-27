package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

/**
 * Provides a mechanism for receiving push-based notifications.
 * @param <T> The object that provides notification information.
 */
public interface IObserver<@DotNetByRefParameter(ByRefParameterType.IN) T>
{
    /**
     * Notifies the observer that the provider has finished sending push-based notifications.
     */
    void OnCompleted();

    /**
     * Notifies the observer that the provider has experienced an error condition.
     * @param error An object that provides additional information about the error.
     */
    void OnError(Exception error);

    /**
     * Provides the observer with new data.
     * @param value The current notification information.
     */
    void OnNext(T value);
}
