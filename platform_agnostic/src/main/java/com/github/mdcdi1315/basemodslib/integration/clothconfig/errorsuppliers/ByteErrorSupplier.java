package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ByteErrorSupplier
    extends BaseErrorSupplier<Integer>
{
    public ByteErrorSupplier(ReflectedConfigFieldData d) { super(d); }

    @Override
    public Optional<Component> function(Integer input)
    {
        if (input > Byte.MAX_VALUE || input < Byte.MIN_VALUE) {
            return Optional.of(
                    Component.literal("Byte value out of range; Allowed values are from [-128..127].")
            );
        } else {
            return VirtualizedFunctionImpl(input.byteValue());
        }
    }
}
