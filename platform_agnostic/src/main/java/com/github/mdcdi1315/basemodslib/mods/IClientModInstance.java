package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.client.*;
import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;

import net.minecraft.client.gui.screens.Screen;

/**
 * Defines the client-side mod instance. <br />
 * The mod instance is expected to be initialized and finally destroyed by using the {@link #Dispose()} method.
 */
@ClientOnlyEnvironment
public interface IClientModInstance
    extends IModInstance
{
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

    /**
     * Provides a way for registering additional menu screens to Minecraft.
     * @param registrar The object responsible for registering additional menu screens.
     * @since 1.0.7
     */
    default void RegisterMenuScreens(IMenuScreensRegistrar registrar) {}

    /**
     * Provides a way for registering additional special model renderers to Minecraft.
     * @param registrar The object responsible for registering additional special model renderers.
     * @since 1.0.11
     */
    default void RegisterSpecialModelRenderers(ISpecialModelRendererRegistrar registrar) {}

    /**
     * Called only once per mod instance to provide a custom configuration screen for their needs. <br />
     * Can also be {@code null}, indicating that the config screen is not applicable of or for any failure creating the GUI.
     * @return The screen configuration factory object to return.
     */
    @MaybeNull
    default ConfigurationScreenFactory<? extends Screen> RegisterConfigurationScreenFactory() {
        return null; // Default implementation returns no factory. You must explicitly register one.
    }
}
