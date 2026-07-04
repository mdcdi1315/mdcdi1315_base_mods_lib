package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides an action that accepts a non-null {@link Double} value as input.
 * @since 1.0.29
 */
@FunctionalInterface
public interface PrimitiveDoubleAction
    extends PrimitiveNumericAction<Double>
{
    void action(double input);

    @Override
    default void action(Double input) { action(input.doubleValue()); }

    @Override
    default void accept(Double input) { action(input.doubleValue()); }
}
