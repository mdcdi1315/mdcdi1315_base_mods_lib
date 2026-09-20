package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Converter;

/**
 * Provides the base functional interface that can convert a given input
 * argument of type {@link TARG} to a numeric value of type {@link T}.
 * @param <TARG> The input argument type.
 * @param <T> The resulting numeric type.
 * @since 1.0.38
 */
@FunctionalInterface
public interface ReturnsPrimitiveFunction<TARG, T extends Number>
    extends Func2<TARG, T>, Converter<TARG, T>
{
    @Override
    default T apply(TARG input) { return function(input); }

    @Override
    default T convert(TARG input) { return function(input); }
}
