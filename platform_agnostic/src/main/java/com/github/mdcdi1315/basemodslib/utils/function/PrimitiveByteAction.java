package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action1;

/**
 * Provides an action that accepts a non-null {@link Byte} value as input.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveByteAction
    extends Action1<Byte>
{
    void action(byte value);

    @Override
    default void action(Byte obj) { action(obj.byteValue()); }

    @Override
    default void accept(Byte aByte) { action(aByte.byteValue()); }
}
