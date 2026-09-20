package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.ArrayView;

import java.util.Optional;

/**
 * Provides a {@link IRandomLookup} implementation for {@link Enum} types.
 * @param <T> The actual enumeration type.
 */
public final class EnumRandomLookup<T extends Enum<T>>
    implements IRandomLookup<T>
{
    private final T[] constants;
    private final Class<T> enum_class;
    private final IRandomSource random;

    /**
     * Initializes a new instance of the {@link EnumRandomLookup} class, providing
     * the random source to use, as well as the enumeration class to use for obtaining its constants.
     * @param random The {@link IRandomSource} to use for producing random indexes to use for obtaining random elements.
     * @param enum_class The {@link Class} of an {@link Enum} to get the constants it declares.
     * @throws ArgumentNullException {@code random} and/or {@code enum_class} are {@code null}.
     */
    public EnumRandomLookup(IRandomSource random, Class<T> enum_class)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(random, "random");
        ArgumentNullException.ThrowIfNull(enum_class, "enum_class");
        this.random = random;
        this.constants = (this.enum_class = enum_class).getEnumConstants();
    }

    /**
     * Gets the enumeration class object whose elements are randomly returned through the {@link #GetRandomElement()} method.
     * @return The enumeration class object.
     */
    @Pure
    @NotNull
    public Class<T> GetEnumClass() { return enum_class; }

    @Pure
    @NotNull
    @Override
    public IRandomSource GetRandomSource() { return random; }

    @NotNull
    @Override
    public IEnumerable<T> GetCollection() { return new ArrayView<>(constants); }

    @NotNull
    @Override
    public Optional<T> GetRandomElement()
    {
        int count = constants.length;
        return (count == 0) ?
                Optional.empty() :
                Optional.of(constants[RandomUtils.NextPositiveIntInRangeExclusive(random, count)]);
    }
}
