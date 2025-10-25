package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Encapsulates a method that has one parameter and returns a value of the type specified by the TResult parameter.
 * @param <T> The type of the parameter of the method that this delegate encapsulates.
 * @param <TResult> The type of the return value of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Func")
public interface Func2<@DotNetByRefParameter(ByRefParameterType.IN) T , @DotNetByRefParameter(ByRefParameterType.OUT) TResult>
{
    /**
     * @param input The parameter of the method that this delegate encapsulates.
     * @return The return value of the method that this delegate encapsulates.
     */
    TResult function(T input);
}
