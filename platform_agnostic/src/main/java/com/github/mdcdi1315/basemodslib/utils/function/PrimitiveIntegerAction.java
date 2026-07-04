package com.github.mdcdi1315.basemodslib.utils.function;

import java.util.function.IntConsumer;

/**
 * Provides an action that accepts a non-null {@link Integer} value as input.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveIntegerAction
    extends PrimitiveNumericAction<Integer>, IntConsumer
{
    /**
     * Provides the method that should be implemented by all derivants of this interface.
     * @param value The value to pass as input to the action.
     */
    void action(int value);

    @Override
    default void accept(int value) { action(value); }

    @Override
    default void action(Integer obj) { action(obj.intValue()); }

    @Override
    default void accept(Integer integer) { action(integer.intValue()); }
}
