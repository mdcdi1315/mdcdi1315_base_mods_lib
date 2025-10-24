package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.DotNetDelegateInterface;

/**
 * Represents a method that converts an object from one type to another type.
 * @param <TInput> The type of object that is to be converted. <br />
 * This type parameter is contravariant. <br />
 * That is, you can use either the type you specified or any type that is less derived. <br />
 * For more information about covariance and contravariance, see Covariance and Contravariance in Generics.
 * @param <TOutput> The type the input object is to be converted to. <br />
 * This type parameter is covariant. <br />
 * That is, you can use either the type you specified or any type that is more derived. <br />
 * For more information about covariance and contravariance, see Covariance and Contravariance in Generics.
 */
@FunctionalInterface
@DotNetDelegateInterface
public interface Converter<@DotNetByRefParameter(ByRefParameterType.IN) TInput, @DotNetByRefParameter(ByRefParameterType.OUT) TOutput>
{
    /**
     * @param input The object to convert.
     * @return The {@link TOutput} that represents the converted {@link TInput}.
     */
    TOutput convert(TInput input);
}
