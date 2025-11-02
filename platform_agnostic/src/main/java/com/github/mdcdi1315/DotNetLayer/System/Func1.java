package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

import java.util.function.Supplier;

/**
 * Encapsulates a method that has no parameters and returns a value of the type specified by the {@link TResult} parameter.
 * @param <TResult> The type of the return value of the method that this delegate encapsulates.
 */
@FunctionalInterface
@DotNetDelegateInterface(ActualTypeName = "Func")
public interface Func1<@DotNetByRefParameter(ByRefParameterType.OUT) TResult>
    extends Supplier<TResult>
{
    /**
     * @return The return value of the method that this delegate encapsulates.
     */
    TResult function();

    @Override
    default TResult get() {
        return function();
    }
}
