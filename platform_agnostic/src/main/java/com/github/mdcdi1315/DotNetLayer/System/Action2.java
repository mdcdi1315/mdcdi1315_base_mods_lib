package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Encapsulates a method that has two parameters and does not return a value.
 * @param <T1> The type of the first parameter of the method that this delegate encapsulates.
 * @param <T2> The type of the second parameter of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Action")
public interface Action2<
        @DotNetByRefParameter(ByRefParameterType.IN) T1,
        @DotNetByRefParameter(ByRefParameterType.IN) T2
        >
{
    /**
     * @param obj1 The first parameter of the method that this delegate encapsulates.
     * @param obj2 The second parameter of the method that this delegate encapsulates.
     */
    void action(T1 obj1 , T2 obj2);
}