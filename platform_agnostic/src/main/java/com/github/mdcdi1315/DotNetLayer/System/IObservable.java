package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

/**
 * Defines a provider for push-based notification.
 * @param <T> The object that provides notification information.
 */
public interface IObservable<@DotNetByRefParameter(ByRefParameterType.OUT) T>
{
    /**
     * Notifies the provider that an observer is to receive notifications.
     * @param observer The object that is to receive notifications.
     * @return A reference to an interface that allows observers to stop receiving notifications before the provider has finished sending them.
     */
    <T2 extends T> IDisposable Subscribe(IObserver<T2> observer);
}
