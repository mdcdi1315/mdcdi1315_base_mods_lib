package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.resources.Identifier;

/**
 * Provides an exception for throwing it when a registry lookup to the root registry has been failed. <br />
 * This class relates to {@link RegistryNotFoundException} class, but this is thrown when the requested registry is not found, not when an object of the in question registry was not found.
 */
public class RegistryNotFoundException
        extends BaseModsLibraryException
{
    @NotNull
    private final Identifier requested_location;

    /**
     * Creates a new instance of the {@link RegistryNotFoundException} class, specifying the location of the registry that was not found.
     * @param registry_location The location of the registry that was not found.
     * @throws ArgumentNullException {@code registry_location} is {@code null}.
     */
    public RegistryNotFoundException(Identifier registry_location)
            throws ArgumentNullException
    {
        super();
        if (registry_location == null) {
            throw new ArgumentNullException("registry_location", "The not found registry object location is null while trying to create a registry not found exception.");
        }
        requested_location = registry_location;
    }

    /**
     * Gets the location of the registry that was not found.
     * @return The location of the registry that was not found.
     */
    @NotNull
    public Identifier GetLocation() {
        return requested_location;
    }

    /**
     * Returns a suitable description for this exception type.
     * @return A description string describing the registry object that was not found.
     */
    @Override
    public String getMessage() {
        return String.format("""
The specified registry was not found in the root registry!
Registry looked up: ROOT
Registry object requested: %s""",
                requested_location
        );
    }
}
