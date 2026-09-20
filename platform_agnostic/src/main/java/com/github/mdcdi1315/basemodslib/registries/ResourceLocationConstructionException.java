package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

import net.minecraft.IdentifierException;

/**
 * Exception class thrown when a {@link net.minecraft.resources.Identifier} cannot be constructed.
 */
public final class ResourceLocationConstructionException
        extends BaseModsLibraryException
{
    private final IdentifierException rle;

    /**
     * Initializes a new instance of the {@link ResourceLocationConstructionException} class, specifying why the resource location instance could not be constructed.
     * @param message The reason why a {@link net.minecraft.resources.Identifier} cannot be constructed.
     */
    public ResourceLocationConstructionException(String message)
    {
        super(message);
        rle = null;
    }

    /**
     * Initializes a new instance of the {@link ResourceLocationConstructionException} class, specifying why the resource location instance could not be constructed,
     * and the specific reason why that was not possible.
     * @param message The reason why a {@link net.minecraft.resources.Identifier} cannot be constructed.
     * @param inner An {@link IdentifierException} instance that better describes why the resource location construction was failed.
     */
    public ResourceLocationConstructionException(String message, IdentifierException inner)
    {
        super(message);
        addSuppressed(rle = inner);
    }

    /**
     * Gets the resource location exception that caused this exception to be thrown. <br />
     * Can be {@code null} if no cause could be specified.
     * @return The cause of this exception. May be {@code null}.
     */
    @MaybeNull
    public IdentifierException GetCause() { return rle; }
}
