package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

import net.minecraft.resources.ResourceLocation;

/**
 * Provides an exception class when any registry was failed to be constructed. <br />
 * This class can be inherited.
 * @since 1.0.35
 */
public class RegistryConstructionException
        extends BaseModsLibraryException
{
    private final ResourceLocation registry;

    private static final String UNSPECIFIED_FAILURE_MSG = "Unspecified failure while constructing the registry.";

    /**
     * Constructs a new instance of the {@link RegistryConstructionException} class, providing the resource location of the registry that failed construction.
     * @param registry The registry that has a failure.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public RegistryConstructionException(ResourceLocation registry) { this(registry, null); }

    /**
     * Constructs a new instance of the {@link RegistryConstructionException} class, providing the
     * resource location of the registry that failed construction, as well as the custom message
     * providing the reason why this exception is being created.
     * @param registry The registry that has a failure.
     * @param message The reason behind registry construction failure.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public RegistryConstructionException(ResourceLocation registry, @AllowNull String message) { this(registry, message, null); }

    /**
     * Constructs a new instance of the {@link RegistryConstructionException} class, providing the
     * resource location of the registry that failed construction, as well as the custom message
     * providing the reason why this exception is being created, and the {@link Exception} object
     * that is the cause of this exception being created.
     * @param registry The registry that has a failure.
     * @param message The reason behind registry construction failure.
     * @param inner The {@link Exception} object that is the reason why this exception object was created.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public RegistryConstructionException(ResourceLocation registry, @AllowNull String message, @AllowNull Exception inner)
    {
        super(StringUtils.IsNullOrEmpty(message) ? UNSPECIFIED_FAILURE_MSG : message, inner);
        ArgumentNullException.ThrowIfNull(registry, "registry");
        this.registry = registry;
    }

    @NotNull
    public final ResourceLocation GetLocation() { return registry; }

    @Override
    public final String getMessage() { return super.getMessage() + "\nRegistry location: " + registry; }
}
