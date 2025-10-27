package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines a mechanism for retrieving a service object; that is, an object that provides custom support to other objects.
 */
public interface IServiceProvider
{
    /**
     * Gets the service object of the specified type.
     * @param serviceType An object that specifies the type of service object to get.
     * @return A service object of type {@code serviceType}.
     */
    @MaybeNull
    Object GetService(Class<?> serviceType);
}
