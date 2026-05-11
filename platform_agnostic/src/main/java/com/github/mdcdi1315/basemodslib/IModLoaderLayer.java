package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.IDisposable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import java.util.List;
import java.nio.file.Path;

/**
 * Provides the specification for a mod loader layer in a given mod loader.
 * <h3>What is the mod loader layer?</h3>
 *
 * The mod loader layer provides the base means for interconnecting the mod instance to the mod loader. <br />
 * Specific platform specific actions are taken in the implementations of all the designated methods. <br />
 * The layer is finally loaded once the library starts up. <br />
 * This is the server loader layer implementation. For the client, see the {@link IClientModLoaderLayer} interface.
 */
public interface IModLoaderLayer
    extends IDisposable, ISynchronized
{
    /**
     * Initializes a new server-side mod instance.
     * @param instance The server-side mod instance to further initialize.
     * @param mod_object The mod object provided by the mod loader. It is used for actually configuring the mod.
     */
    void InitializeServerModInstance(IServerModInstance instance, Object mod_object);

    /**
     * Gets a value whether the specified mod ID is present in this Minecraft instance. <br />
     * Implementers should communicate with the mod loader's mod list and look up the mod ID.
     * @param mod_id The ID of the mod to find.
     * @return A value whether the requested mod is loaded.
     */
    boolean IsModLoaded(String mod_id);

    /**
     * Gets a list of the mods loaded by their ID's.
     * @return The loaded mods.
     */
    @NotNull
    List<String> GetLoadedMods();

    /**
     * Gets the modding environment under which the library runs. <br />
     * See the {@link ModdingEnvironment} enumeration for more information.
     * @return The current modding environment as reported by the mod loader.
     */
    @NotNull
    ModdingEnvironment GetEnvironment();

    /**
     * Gets a friendly name of the mod loader, such as 'Fabric'.
     * @return The mod loader's friendly name.
     */
    @NotNull
    String GetModLoaderBranding();

    /**
     * Gets a {@link Version} instance providing the Minecraft (base game) version.
     * @return The Minecraft version.
     * @deprecated This method invocation is no longer required and most
     * mod-loaders do not provide a run-time version of the game version, neither the base game itself.
     * Instead now, the BML itself will provide the version that it was built against.
     * (Which it will typically be that one that will be used in a large modded instance)
     */
    @NotNull
    @Deprecated(since = "1.0.25")
    default Version GetMinecraftVersion() {
        return BaseModsLib.GetMinecraftVersion();
    }

    /**
     * Gets a {@link Version} instance providing the mod loader's version.
     * @return The mod loader version.
     */
    @NotNull
    Version GetModLoaderVersion();

    /**
     * Gets the directory where all the configuration files are stored.
     * @return The configuration directory path.
     */
    @NotNull
    Path GetConfigurationDirectory();

    /**
     * Gets the directory where the game runs.
     * @return The directory where the game has been started to.
     * @since 1.0.5
     */
    @NotNull
    Path GetMinecraftDirectory();

    /**
     * Gets a value whether this Minecraft build originates from a development environment.
     * @return A value whether the current instance originates as a result of running from a development environment.
     * @since 1.0.11
     */
    boolean IsDevelopmentEnvironmentBuild();

    /**
     * Returns a {@link IModResourceLookup} instance for the given mod id.
     * @param mod_id The mod id to get an {@link IModResourceLookup} instance for.
     * @return The mod resource lookup object for {@code mod_id}.
     * @since 1.0.31
     */
    @MaybeNull
    IModResourceLookup GetResourceLookupByID(String mod_id);
}
