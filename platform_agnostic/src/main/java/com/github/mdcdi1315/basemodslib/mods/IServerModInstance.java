package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.basemodslib.entity.IEntityTypeRegistrar;
import com.github.mdcdi1315.basemodslib.item.IItemRegistrar;
import com.github.mdcdi1315.basemodslib.block.IBlockRegistrar;
import com.github.mdcdi1315.basemodslib.network.NetworkManager;
import com.github.mdcdi1315.basemodslib.world.IWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.commands.ICommandRegistrar;
import com.github.mdcdi1315.basemodslib.registries.IRegistryRegistrar;
import com.github.mdcdi1315.basemodslib.block.entity.IBlockEntityRegistrar;

/**
 * Defines the server-side mod instance. <br />
 * The mod instance is expected to be initialized and finally destroyed by using the {@link #Dispose()} method.
 */
public interface IServerModInstance
    extends IModInstance
{
    /**
     * Provides the logic for registering blocks.
     * @param registrar The object responsible for registering blocks to this instance.
     */
    default void RegisterBlocks(IBlockRegistrar registrar) {}

    /**
     * Provides the logic for registering block entities.
     * @param registrar The object responsible for registering block entities to this instance.
     */
    default void RegisterBlockEntities(IBlockEntityRegistrar registrar) {}

    /**
     * Provides the logic for registering items.
     * @param registrar The object responsible for registering items to this instance.
     */
    default void RegisterItems(IItemRegistrar registrar) {}

    /**
     * Registers other registry items far from blocks and items to Minecraft.
     * @param registrar The object responsible for registering other registry items.
     */
    default void RegisterRegistryItems(IRegistryRegistrar registrar) {}

    /**
     * Registers chat commands to Minecraft.
     * @param registrar The object responsible for registering chat commands to Minecraft.
     */
    default void RegisterCommands(ICommandRegistrar registrar) {}

    /**
     * Registers registry items related to world generation. <br />
     * Generally, it is a wrapper around the {@link IRegistryRegistrar} instance.
     * @param registrar The object responsible for registering registry items related to world generation.
     */
    default void RegisterWorldGenItems(IWorldGenRegistrar registrar) {}

    /**
     * Initializes networking services for this mod instance. <br />
     * Note: Mods wishing to provide networking services should retain somewhere this object! <br />
     * @param manager The networking manager to be used by the mod instance.
     */
    default void InitializeNetwork(NetworkManager manager) {}

    /**
     * Registers entity types and other stuff related to entities for this mod instance.
     * @param registrar The object responsible for registering entity types to Minecraft.
     */
    default void RegisterEntityTypes(IEntityTypeRegistrar registrar) {}
}
