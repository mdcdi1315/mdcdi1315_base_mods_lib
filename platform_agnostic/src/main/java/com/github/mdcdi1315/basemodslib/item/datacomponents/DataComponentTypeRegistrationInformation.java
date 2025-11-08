package com.github.mdcdi1315.basemodslib.item.datacomponents;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.core.component.DataComponentType;

/**
 * Provides information for registering data component types to Minecraft.
 * @param component_type_provider A function providing the data component type to register.
 * @param <T> The type of the data component to hold.
 */
public record DataComponentTypeRegistrationInformation<T>(
        @NotNull Func1<DataComponentType<T>> component_type_provider
)
{
    public DataComponentTypeRegistrationInformation {
        ArgumentNullException.ThrowIfNull(component_type_provider, "component_type_provider");
    }
}
