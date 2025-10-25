package com.github.mdcdi1315.basemodslib.block;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * Defines methods for making known new Minecraft blocks to the mod loader.
 */
public interface IBlockRegistrar
{
    /**
     * Makes known a block with the specified block information that can create the in question block.
     * @param name The name of the block. It's resource location will be constructed by your mod's name as the namespace and this value as it's path.
     * @param info The block registration information to use.
     * @throws ArgumentNullException {@code name} or {@code creator} were {@code null}.
     */
    void Register(String name, BlockRegistrationInformation info)
            throws ArgumentNullException;
}
