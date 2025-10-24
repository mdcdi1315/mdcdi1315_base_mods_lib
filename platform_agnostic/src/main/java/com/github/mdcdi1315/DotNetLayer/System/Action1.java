package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Encapsulates a method that has a single parameter and does not return a value.
 * @param <T> The type of the parameter of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Action")
public interface Action1<@DotNetByRefParameter(ByRefParameterType.IN) T>
{
    /**
     * @param obj The parameter of the method that this delegate encapsulates.
     */
    void action(T obj);
}
