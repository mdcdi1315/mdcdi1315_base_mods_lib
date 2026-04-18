package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;

/**
 * Provides the base predicate interface for numeric types.
 * @param <T> The exact type of the number that it is matched against.
 * @since 1.0.26
 */
@FunctionalInterface
public interface NumericPredicate<T extends Number> extends Predicate<T> { }