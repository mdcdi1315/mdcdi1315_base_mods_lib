package com.github.mdcdi1315.basemodslib.entity.attributes;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * Provides the way for registering attributes to Minecraft.
 * @param attribute_getter A function that, when invoked, provides the attribute to register.
 */
public record AttributeRegistrationInfo(
       @NotNull Func1<Attribute> attribute_getter
) {
    /**
     * Creates a new instance of the {@link AttributeRegistrationInfo} class.
     * @param attribute_getter The function that when invoked, it will provide the attribute to register.
     */
    public AttributeRegistrationInfo {
        ArgumentNullException.ThrowIfNull(attribute_getter, "attribute_getter");
    }
}
