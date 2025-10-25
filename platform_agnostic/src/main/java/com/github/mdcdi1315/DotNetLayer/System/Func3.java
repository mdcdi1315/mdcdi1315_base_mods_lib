package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Encapsulates a method that has two parameters and returns a value of the type specified by the TResult parameter.
 * @param <T1> The type of the first parameter of the method that this delegate encapsulates.
 * @param <T2> The type of the second parameter of the method that this delegate encapsulates.
 * @param <TResult> The type of the return value of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Func")
public interface Func3<
        @DotNetByRefParameter(ByRefParameterType.IN) T1,
        @DotNetByRefParameter(ByRefParameterType.IN) T2,
        @DotNetByRefParameter(ByRefParameterType.OUT) TResult
        >
{
    /**
     * @param input_1 The first parameter of the method that this delegate encapsulates.
     * @param input_2 The second parameter of the method that this delegate encapsulates.
     * @return The return value of the method that this delegate encapsulates.
     */
    TResult function(T1 input_1, T2 input_2);
}
