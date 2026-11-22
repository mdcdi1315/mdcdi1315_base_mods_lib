package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.IDisposable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.mods.information.IModInfo;
import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;
import com.github.mdcdi1315.basemodslib.utils.collections.ICountableCollectionBase;

import java.util.List;
import java.nio.file.Path;
import java.util.ArrayList;

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
     * Implementers wishing to override this should communicate with the mod loader's mod list and look up the mod ID.
     * @param mod_id The ID of the mod to find.
     * @return A value whether the requested mod is loaded.
     */
    default boolean IsModLoaded(String mod_id) { return GetModInfo(mod_id) != null; }

    /**
     * Gets a list of the mods loaded by their IDs.
     * @return The loaded mods.
     * @deprecated This method is deprecated because it comes from the first
     * development versions of the library, where {@link IEnumerable} support
     * did not yet exist at the time. Use the {@link #GetLoadedModIDs()} method instead.
     */
    @NotNull
    @Deprecated(forRemoval = true, since = "1.0.42-beta8")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.42-beta8")
    default List<String> GetLoadedMods()
    {
        int count = 64;
        var elements = GetLoadedModIDs();
        if (elements instanceof ICountableCollectionBase c) { count = c.GetCount(); }
        else if (elements instanceof ICollection<String> c) { count = c.getCount(); }

        var list = new ArrayList<String>(count);

        try (var en = CollectionManipulations.GetEnumeratorSafe(elements))
        {
            while (en.MoveNext()) { list.add(en.getCurrent()); }
        }

        return list;
    }

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
     * Gets various information about a mod with the specified ID.
     * @param mod_id The ID of the mod to find.
     * @return Information about the mod with ID {@code mod_id}, or
     * {@code null} if the specified mod ID cannot be found.
     * @since 1.0.42-beta8
     */
    @MaybeNull
    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.42-beta8")
    IModInfo GetModInfo(String mod_id);

    /**
     * Gets a list of the mods loaded by their IDs.
     * @return The loaded mods.
     * @since 1.0.42-beta8
     */
    @NotNull
    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.42-beta8")
    IEnumerable<String> GetLoadedModIDs();
}
