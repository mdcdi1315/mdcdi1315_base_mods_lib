package com.github.mdcdi1315.basemodslib.fluid;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.Contract;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Provides a way for registering new fluids to Minecraft.
 * @since 1.0.5
 */
@Contract
public interface IFluidRegistrar
{
    /**
     * Registers a new fluid to the Minecraft instance.
     * @param name The name of the fluid to be registered.
     * @param info The fluid registration information to use for registering the fluid.
     * @throws ArgumentNullException {@code name} and/or {@code info} are {@code null}.
     */
    void Register(@ConstantExpected String name, FluidRegistrationInformation info) throws ArgumentNullException;

    /**
     * Gets any fluid by the specified location.
     * @param location The resource location that specifies the fluid to get.
     * @return The requested fluid, if that was found.
     * @param <T> The exact type of the fluid to return.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException {@code location} was not found in the fluid registry.
     * @since 1.0.12
     */
    public static <T extends Fluid> T GetFluid(ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return (T) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.FLUID , location);
    }
}
