package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func1;

/**
 * Defines the base functional interface for providing primitive numeric values as return values.
 * @param <T> The return type of the 'provider' function.
 * @since 1.0.37
 */
@FunctionalInterface
public interface ProvidesNumberFunction<T extends Number> extends Func1<T> { }
