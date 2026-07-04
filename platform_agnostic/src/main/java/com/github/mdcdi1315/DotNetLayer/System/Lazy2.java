package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.DotNetLayer.System.Threading.LazyThreadSafetyMode;

/**
 * Provides a lazy indirect reference to an object and its associated metadata for use by the Managed Extensibility Framework.
 * @param <T> The type of the object referenced.
 * @param <TMetadata> The type of the metadata.
 */
public class Lazy2<T, TMetadata>
    extends Lazy1<T>
{
    @AllowNull
    private final TMetadata metadata;

    /**
     * Initializes a new instance of the {@link Lazy2} class with the specified metadata that uses the specified function to get the referenced object.
     * @param valueFactory A function that returns the referenced object.
     * @param metadata The metadata associated with the referenced object.
     * @throws ArgumentNullException {@code valueFactory} is {@code null}.
     */
    public Lazy2(Func1<T> valueFactory, @AllowNull TMetadata metadata)
            throws ArgumentNullException
    {
        super(valueFactory);
        this.metadata = metadata;
    }

    /**
     * Initializes a new instance of the {@link Lazy2} class with the specified metadata and thread safety value that uses the specified function to get the referenced object.
     * @param valueFactory A function that returns the referenced object.
     * @param metadata The metadata associated with the referenced object.
     * @param isThreadSafe Indicates whether the {@link Lazy2} object that is created will be thread-safe.
     * @throws ArgumentNullException {@code valueFactory} is {@code null}.
     */
    public Lazy2(Func1<T> valueFactory, @AllowNull TMetadata metadata, boolean isThreadSafe)
            throws ArgumentNullException
    {
        super(valueFactory, isThreadSafe);
        this.metadata = metadata;
    }

    /**
     * Initializes a new instance of the {@link Lazy2} class with the specified metadata and thread synchronization mode that uses the specified function to get the referenced object.
     * @param valueFactory A function that returns the referenced object.
     * @param metadata The metadata associated with the referenced object.
     * @param mode The thread synchronization mode.
     * @throws ArgumentNullException {@code valueFactory} is {@code null}.
     */
    public Lazy2(Func1<T> valueFactory, @AllowNull TMetadata metadata, LazyThreadSafetyMode mode)
            throws ArgumentNullException
    {
        super(valueFactory, mode);
        this.metadata = metadata;
    }

    /**
     * Gets the metadata associated with the referenced object.
     * @return The metadata associated with the referenced object.
     */
    @MaybeNull
    public TMetadata GetMetadata() { return this.metadata; }
}
