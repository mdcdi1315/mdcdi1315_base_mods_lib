package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.DefaultHashFunction;

/**
 * Provides a function that can compute hash codes of the specified type.
 * @param <T> The type of objects supported to be hashed by this function.
 * @since 1.0.37
 */
@FunctionalInterface
public interface HashCodeFunction<T>
{
    /**
     * Computes the hash code for {@code object}. <br />
     * While there is not a clear requirement on the fact that this implementation
     * must support {@code null} objects as well, if it does so, it should return the value 0.
     * @param object The object to compute its hash code for.
     * @return The hash code of {@code object}.
     */
    int GetHashCode(@AllowNull T object);

    /**
     * Gets a default implementation of the {@link HashCodeFunction} interface.
     * @return A default implementation of the {@link HashCodeFunction} for type {@link T}.
     * @param <T> The type of objects to be hashed by the returned function.
     */
    @NotNull
    static <T> HashCodeFunction<T> GetDefault() { return new DefaultHashFunction<>(); }
}
