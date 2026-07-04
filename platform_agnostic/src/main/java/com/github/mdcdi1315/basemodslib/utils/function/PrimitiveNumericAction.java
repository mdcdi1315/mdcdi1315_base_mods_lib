package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action1;

/**
 * Provides the base functional interface for functions that accept a numeric argument.
 * @param <TARG> The numeric argument type to specify. Must extend the {@link Number} type.
 * @since 1.0.35
 * @apiNote This function is used as a linking aggregate to many already-defined primitive functional interface declarations in this package.
 * Older consumers depending on those preexisting functional interfaces will still be functional.
 */
@FunctionalInterface
public interface PrimitiveNumericAction<TARG extends Number> extends Action1<TARG> { }