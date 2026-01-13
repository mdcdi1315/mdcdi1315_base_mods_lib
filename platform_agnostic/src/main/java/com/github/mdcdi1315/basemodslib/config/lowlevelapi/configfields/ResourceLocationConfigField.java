package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

import net.minecraft.resources.ResourceLocation;

/**
 * Provides an implementation of the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} interface for Minecraft's resource location fields.
 * @since 1.0.15
 */
public final class ResourceLocationConfigField
    extends BaseConfigField<ResourceLocation>
{
    private final ResourceLocation value;

    /**
     * Initializes a new instance of the {@link ResourceLocationConfigField} class.
     * @param name The name of the newly created field.
     * @param value The resource location value of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    public ResourceLocationConfigField(String name, @AllowNull String comment, ResourceLocation value, Iterable<IConfigFieldConstraint<ResourceLocation>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
        ArgumentNullException.ThrowIfNull(value, "value");
        this.value = value;
    }

    @Override
    public ResourceLocation GetValue() { return value; }
}
