package com.github.mdcdi1315.basemodslib.utils.function;

import java.util.function.LongConsumer;

/**
 * Provides an action that accepts a non-null {@link Long} value as input.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveLongAction
    extends PrimitiveNumericAction<Long>, LongConsumer
{
    void action(long value);

    @Override
    default void accept(long value) { action(value); }

    @Override
    default void action(Long obj) { action(obj.longValue()); }

    @Override
    default void accept(Long aLong) { action(aLong.longValue()); }
}
