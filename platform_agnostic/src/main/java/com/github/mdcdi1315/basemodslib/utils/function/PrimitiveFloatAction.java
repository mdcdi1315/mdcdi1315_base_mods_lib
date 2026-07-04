package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides an action that accepts a non-null {@link Float} value as input.
 * @since 1.0.29
 */
@FunctionalInterface
public interface PrimitiveFloatAction
    extends PrimitiveNumericAction<Float>
{
    void action(float input);

    @Override
    default void action(Float obj) { action(obj.floatValue()); }

    @Override
    default void accept(Float obj) { action(obj.floatValue()); }
}
