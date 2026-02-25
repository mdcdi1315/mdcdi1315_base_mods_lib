package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Encapsulates a method that has 13 parameters and returns a value of the type specified by the {@link TResult} parameter.
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
 * @param <T13> The type of the thirteenth parameter of the method that this delegate encapsulates.
 * @param <TResult> The type of the return value of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Func")
public interface Func14<
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
        @DotNetByRefParameter(ByRefParameterType.IN) T12,
        @DotNetByRefParameter(ByRefParameterType.IN) T13,
        @DotNetByRefParameter(ByRefParameterType.OUT) TResult
        >
{
    /**
     * @param input_1 The first parameter of the method that this delegate encapsulates.
     * @param input_2 The second parameter of the method that this delegate encapsulates.
     * @param input_3 The third parameter of the method that this delegate encapsulates.
     * @param input_4 The fourth parameter of the method that this delegate encapsulates.
     * @param input_5 The fifth parameter of the method that this delegate encapsulates.
     * @param input_6 The sixth parameter of the method that this delegate encapsulates.
     * @param input_7 The seventh parameter of the method that this delegate encapsulates.
     * @param input_8 The eighth parameter of the method that this delegate encapsulates.
     * @param input_9 The ninth parameter of the method that this delegate encapsulates.
     * @param input_10 The tenth parameter of the method that this delegate encapsulates.
     * @param input_11 The eleventh parameter of the method that this delegate encapsulates.
     * @param input_12 The twelfth parameter of the method that this delegate encapsulates.
     * @param input_13 The thirteenth parameter of the method that this delegate encapsulates.
     * @return The return value of the method that this delegate encapsulates.
     */
    TResult function(T1 input_1, T2 input_2, T3 input_3, T4 input_4, T5 input_5, T6 input_6, T7 input_7, T8 input_8, T9 input_9, T10 input_10, T11 input_11, T12 input_12, T13 input_13);
}