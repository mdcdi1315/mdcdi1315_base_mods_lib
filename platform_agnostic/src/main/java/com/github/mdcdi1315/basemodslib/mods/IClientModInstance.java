package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.basemodslib.client.*;

import com.github.mdcdi1315.basemodslib.eventapi.EventManager;

/**
 * Defines the client-side mod instance. <br />
 * The mod instance is expected to be initialized and finally destroyed by using the {@link #Dispose()} method.
 */
public interface IClientModInstance
    extends IModInstance
{
    /**
     * Registers events to be listened on the current client mod instance.
     * @param manager The events manager object to use.
     */
    default void RegisterEvents(EventManager manager) {}

    /**
     * Provides the logic for registering color handlers for blocks and items.
     * @param registrar The object responsible for registering color handlers for blocks and items.
     */
    default void RegisterColorHandlers(IColorHandlersRegistrar registrar) {}

    /**
     * Provides the logic for registering entity renderers.
     * @param registrar The object responsible for registering entity renderers.
     */
    default void RegisterEntityRenderers(IEntityRendererRegistrar registrar) {}

    /**
     * Provides the logic for registering block entity renderers.
     * @param registrar The object responsible for registering block entity renderers.
     */
    default void RegisterBlockEntityRenderers(IBlockEntityRendererRegistrar registrar) {}

    /**
     * Provides the logic for registering model definitions.
     * @param registrar The object responsible for registering model definitions.
     */
    default void RegisterModelDefinitions(IModelDefinitionRegistrar registrar) {}

    /**
     * Provides the logic for registering particle providers.
     * @param registrar The object responsible for registering particle providers.
     */
    default void RegisterParticleProviders(IParticleProviderRegistrar registrar) {}
}
