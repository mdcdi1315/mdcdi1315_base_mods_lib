package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action1;

/**
 * Provides an action that accepts a non-null {@link Double} value as input.
 * @since 1.0.29
 */
@FunctionalInterface
public interface PrimitiveDoubleAction
    extends Action1<Double>
{
    void action(double input);

    @Override
    default void action(Double input) { action(input.doubleValue()); }

    @Override
    default void accept(Double input) { action(input.doubleValue()); }
}
