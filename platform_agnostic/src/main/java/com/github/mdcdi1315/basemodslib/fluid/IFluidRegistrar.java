package com.github.mdcdi1315.basemodslib.fluid;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

/**
 * Provides a way for registering new fluids to Minecraft.
 * @since 1.0.5
 */
public interface IFluidRegistrar
{
    /**
     * Registers a new fluid to the Minecraft instance.
     * @param name The name of the fluid to be registered.
     * @param info The fluid registration information to use for registering the fluid.
     * @throws ArgumentNullException {@code name} and/or {@code info} are {@code null}.
     */
    void Register(@ConstantExpected String name, FluidRegistrationInformation info) throws ArgumentNullException;
}
