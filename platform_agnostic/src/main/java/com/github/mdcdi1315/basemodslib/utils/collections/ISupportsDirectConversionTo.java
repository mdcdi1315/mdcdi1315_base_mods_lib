package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Converter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

/**
 * Provides a declaration for collections that return an instance of the same type,
 * but they are able to convert their elements based on a given {@link Converter} instance.
 * @param <T> The type of the elements in the implemented collection.
 * @since 1.0.26
 */
public interface ISupportsDirectConversionTo<T>
    extends IEnumerable<T>
{
    /**
     * Converts all the elements in the current collection by using the specified
     * {@code converter} and returns the converted elements in a new collection of the same type.
     * @param converter The {@link Converter} to use for converting an element of type {@link T} to {@link TO}.
     * @param comparer Optional. If the current collection supports equality comparison and a new equality
     *                 comparer is needed, this can specify the comparer to use in the new instance.
     * @return A new collection of the same type as this one, but with all of its elements mapped to type {@link TO}.
     * @param <TO> The type of the elements that the newly returned collection will have.
     * @throws ArgumentNullException {@code converter} is {@code null}.
     */
    @NotNull
    <TO> ISupportsDirectConversionTo<TO> ConvertAll(Converter<T, TO> converter, @AllowNull IEqualityComparer<TO> comparer) throws ArgumentNullException;

    /**
     * Converts all the elements in the current collection by using the specified
     * {@code converter} and returns the converted elements in a new collection of the same type.
     * @param converter The {@link Converter} to use for converting an element of type {@link T} to {@link TO}.
     * @return A new collection of the same type as this one, but with all of its elements mapped to type {@link TO}.
     * @param <TO> The type of the elements that the newly returned collection will have.
     * @throws ArgumentNullException {@code converter} is {@code null}.
     * @implNote This method calls {@link #ConvertAll(Converter, IEqualityComparer)} with {@code comparer} specified to {@code null}.
     */
    @NotNull
    default <TO> ISupportsDirectConversionTo<TO> ConvertAll(Converter<T, TO> converter)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return ConvertAll(converter, null);
    }
}
