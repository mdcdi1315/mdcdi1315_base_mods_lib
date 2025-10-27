package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Encapsulates a method that has five parameters and does not return a value.
 * @param <T1> The type of the first parameter of the method that this delegate encapsulates.
 * @param <T2> The type of the second parameter of the method that this delegate encapsulates.
 * @param <T3> The type of the third parameter of the method that this delegate encapsulates.
 * @param <T4> The type of the fourth parameter of the method that this delegate encapsulates.
 * @param <T5> The type of the fifth parameter of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Action")
public interface Action5<
        @DotNetByRefParameter(ByRefParameterType.IN) T1,
        @DotNetByRefParameter(ByRefParameterType.IN) T2,
        @DotNetByRefParameter(ByRefParameterType.IN) T3,
        @DotNetByRefParameter(ByRefParameterType.IN) T4,
        @DotNetByRefParameter(ByRefParameterType.IN) T5
>
{
    /**
     * @param arg1 The first parameter of the method that this delegate encapsulates.
     * @param arg2 The second parameter of the method that this delegate encapsulates.
     * @param arg3 The third parameter of the method that this delegate encapsulates.
     * @param arg4 The fourth parameter of the method that this delegate encapsulates.
     * @param arg5 The fifth parameter of the method that this delegate encapsulates.
     */
    void action(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5);
}
