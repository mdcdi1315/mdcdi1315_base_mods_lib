package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides a way for register color and block color handlers to Minecraft client.
 */
public interface IColorHandlersRegistrar
{
    /**
     * Registers an item color handler to the Minecraft client.
     * @param info The item color handler registration information.
     */
    void Register(ItemColorHandlerRegistrationInfo info) throws ArgumentNullException;

    /**
     * Registers a block color handler to the Minecraft client.
     * @param info The item color handler registration information.
     */
    void Register(BlockColorHandlerRegistrationInfo info) throws ArgumentNullException;
}
