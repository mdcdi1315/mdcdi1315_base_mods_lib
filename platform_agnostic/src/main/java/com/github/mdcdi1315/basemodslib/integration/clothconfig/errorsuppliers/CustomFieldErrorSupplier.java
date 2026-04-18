package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;
import com.github.mdcdi1315.basemodslib.config.gui.IClothConfigTransformableValue;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public final class CustomFieldErrorSupplier<T>
    extends BaseErrorSupplier<String>
{
    private final IClothConfigTransformableValue<T> handler;

    public CustomFieldErrorSupplier(ReflectedConfigFieldData d, IClothConfigTransformableValue<T> handler) {
        super(d);
        this.handler = handler;
    }

    @Override
    public Optional<Component> function(String input)
    {
        T parsed;
        try {
            parsed = handler.Parse(input);
        } catch (Exception e) {
            return Optional.of(
                    Component.literal(String.format("Could not parse string contents: \"%s\"\nDue to: %s", input, e))
            );
        }
        return VirtualizedFunctionImpl(parsed);
    }
}
