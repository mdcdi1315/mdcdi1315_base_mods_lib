package com.github.mdcdi1315.basemodslib.mods.information;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

import java.util.Optional;

/**
 * Provides information about a mod.
 * <h3>End-to-end Extensibility</h3>
 * The interface does not define any common properties/methods or whatsoever. <br />
 * All the properties of the mod are queried through attributes, which they may be
 * defined or not to a given mod info instance.
 */
public interface IModInfo
    extends ISynchronized
{
    /**
     * Provides the name of the attribute that provides a friendly name of the mod. <br />
     * Type: {@link String}
     */
    String FRIENDLY_NAME = "Friendly Name";

    /**
     * Provides the name of the attribute that provides the author(s) of the mod. <br />
     * Type: {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable}, of type {@link String}.
     */
    String AUTHORS = "Authors";

    /**
     * Provides the name of the attribute that provides a short description of the mod. <br />
     * Type: {@link String}
     */
    String DESCRIPTION = "Description";

    /**
     * Provides the name of the attribute that provides the version of the mod. <br />
     * Type: {@link String}
     */
    String VERSION = "Version";

    /**
     * Provides the name of the attribute that provides a website/home URL of the mod's development. <br />
     * Type: {@link java.net.URI}
     */
    String MOD_URL = "URL";

    /**
     * Provides the name of the attribute that provides a URL for updating the mod. <br />
     * Type: {@link java.net.URI}
     */
    String UPDATE_URL = "Update URL";

    /**
     * Provides the name of the attribute that provides the dependencies that the mod contains. <br />
     * Type: {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable} of type {@link ModDependency}.
     */
    String DEPENDENCIES = "Dependencies";

    /**
     * Provides the name of the attribute that provides the environments under which the mod runs. <br />
     * Type: Array of {@link com.github.mdcdi1315.basemodslib.ModdingEnvironment} values.
     */
    String LOAD_ENVIRONMENT = "Loading Environment";

    /**
     * Provides the name of the attribute that provides a relative path to a file to use as the mod's logo. <br />
     * Type: {@link String}
     */
    String LOGO_FILE = "Logo File";

    /**
     * Gets the ID of this mod.
     * @return The mod's ID.
     */
    @NotNull
    String GetModID();

    /**
     * Gets the files that the mod contains.
     * @return The files contained inside the mod.
     */
    @NotNull
    IModFileSystem GetFileSystem();

    /**
     * Gets an attribute defined on this mod info.
     * @param name The name of the attribute to retrieve.
     * @return The value of the attribute, if existing and available.
     * @throws ArgumentNullException {@code name} is {@code null}.
     */
    @NotNull
    Optional<Object> GetAttribute(String name) throws ArgumentNullException;
}
