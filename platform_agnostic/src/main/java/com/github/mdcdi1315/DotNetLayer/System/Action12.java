package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Encapsulates a method that has 12 parameters and does not return a value.
 * @param <T1> The type of the first parameter of the method that this delegate encapsulates.
 * @param <T2> The type of the second parameter of the method that this delegate encapsulates.
 * @param <T3> The type of the third parameter of the method that this delegate encapsulates.
 * @param <T4> The type of the fourth parameter of the method that this delegate encapsulates.
 * @param <T5> The type of the fifth parameter of the method that this delegate encapsulates.
 * @param <T6> The type of the sixth parameter of the method that this delegate encapsulates.
 * @param <T7> The type of the seventh parameter of the method that this delegate encapsulates.
 * @param <T8> The type of the eighth parameter of the method that this delegate encapsulates.
 * @param <T9> The type of the ninth parameter of the method that this delegate encapsulates.
 * @param <T10> The type of the tenth parameter of the method that this delegate encapsulates.
 * @param <T11> The type of the eleventh parameter of the method that this delegate encapsulates.
 * @param <T12> The type of the twelfth parameter of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Action")
public interface Action12<
        @DotNetByRefParameter(ByRefParameterType.IN) T1,
        @DotNetByRefParameter(ByRefParameterType.IN) T2,
        @DotNetByRefParameter(ByRefParameterType.IN) T3,
        @DotNetByRefParameter(ByRefParameterType.IN) T4,
        @DotNetByRefParameter(ByRefParameterType.IN) T5,
        @DotNetByRefParameter(ByRefParameterType.IN) T6,
        @DotNetByRefParameter(ByRefParameterType.IN) T7,
        @DotNetByRefParameter(ByRefParameterType.IN) T8,
        @DotNetByRefParameter(ByRefParameterType.IN) T9,
        @DotNetByRefParameter(ByRefParameterType.IN) T10,
        @DotNetByRefParameter(ByRefParameterType.IN) T11,
        @DotNetByRefParameter(ByRefParameterType.IN) T12
        >
{
    /**
     * @param arg1 The first parameter of the method that this delegate encapsulates.
     * @param arg2 The second parameter of the method that this delegate encapsulates.
     * @param arg3 The third parameter of the method that this delegate encapsulates.
     * @param arg4 The fourth parameter of the method that this delegate encapsulates.
     * @param arg5 The fifth parameter of the method that this delegate encapsulates.
     * @param arg6 The sixth parameter of the method that this delegate encapsulates.
     * @param arg7 The seventh parameter of the method that this delegate encapsulates.
     * @param arg8 The eighth parameter of the method that this delegate encapsulates.
     * @param arg9 The ninth parameter of the method that this delegate encapsulates.
     * @param arg10 The tenth parameter of the method that this delegate encapsulates.
     * @param arg11 The eleventh parameter of the method that this delegate encapsulates.
     * @param arg12 The twelfth parameter of the method that this delegate encapsulates.
     */
    void action(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8, T9 arg9, T10 arg10, T11 arg11, T12 arg12);
}
