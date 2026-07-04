package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Converter;

/**
 * Provides the base functional interface for functions that accept a numeric argument and return a value of type {@link TR}.
 * @param <TARG> The numeric argument type to specify. Must extend the {@link Number} type.
 * @param <TR> The type of the result.
 * @since 1.0.35
 * @apiNote This function is used as a linking aggregate to many already-defined primitive functional interface declarations in this package. <br />
 * Older consumers depending on those preexisting functional interfaces will still be functional. <br />
 * Also, this interface also implements the {@link Converter} functional interface.
 */
@FunctionalInterface
public interface PrimitiveNumericFunction<TARG extends Number, TR>
        extends Func2<TARG, TR>, Converter<TARG, TR>
{
    @Override
    default TR apply(TARG input) { return function(input); }

    @Override
    default TR convert(TARG input) { return function(input); }
}