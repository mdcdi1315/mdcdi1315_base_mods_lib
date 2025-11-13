package com.github.mdcdi1315.basemodslib.fluid;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

/**
 * Provides information for registering new fluids to Minecraft.
 * @param fluid_getter A function for registering the fluid. Notice that the function does also return the constructed resource location under which the fluid will be registered as.
 * @since 1.0.5
 */
public record FluidRegistrationInformation(
        @NotNull Func2<ResourceLocation , Fluid> fluid_getter
) {
    /**
     * Constructs a new instance of the {@link FluidRegistrationInformation} class.
     * @param fluid_getter The function responsible for registering the fluid.
     * @throws ArgumentNullException {@code fluid_getter} is {@code null}.
     */
    public FluidRegistrationInformation {
        ArgumentNullException.ThrowIfNull(fluid_getter, "fluid_getter");
    }
}
