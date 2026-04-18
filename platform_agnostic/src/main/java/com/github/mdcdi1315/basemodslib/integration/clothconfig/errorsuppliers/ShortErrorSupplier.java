package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ShortErrorSupplier
    extends BaseErrorSupplier<Integer>
{
    public ShortErrorSupplier(ReflectedConfigFieldData d) { super(d); }

    @Override
    public Optional<Component> function(Integer input)
    {
        if (input > Short.MAX_VALUE || input < Short.MIN_VALUE) {
            return Optional.of(
                    Component.literal("Short value out of range; Allowed values are from [-32768..32767].")
            );
        } else {
            return VirtualizedFunctionImpl(input.shortValue());
        }
    }
}
