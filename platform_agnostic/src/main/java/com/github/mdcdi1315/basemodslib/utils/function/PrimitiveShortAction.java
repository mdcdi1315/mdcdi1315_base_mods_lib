package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action1;

/**
 * Provides an action that accepts a non-null {@link Short} value as input.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveShortAction
    extends Action1<Short>
{
    void action(short input);

    @Override
    default void action(Short obj) { action(obj.shortValue()); }

    @Override
    default void accept(Short aShort) { action(aShort.shortValue()); }
}
