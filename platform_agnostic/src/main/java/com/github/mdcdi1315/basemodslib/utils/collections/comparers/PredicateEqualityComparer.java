package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.function.BiPredicate;
import com.github.mdcdi1315.basemodslib.utils.collections.HashCodeFunction;

/**
 * Provides an equality comparer that it's equality function is dependent on a predicate.
 * @param <T> The type of the item to be compared.
 */
public final class PredicateEqualityComparer<T>
    extends AbstractObjectEqualityComparer<T>
{
    private final BiPredicate<T, T> predicate;
    private final HashCodeFunction<T> hash_code;

    /**
     * Initializes a new instance of the {@link PredicateEqualityComparer} class.
     * @param predicate The predicate to use for equality.
     * @param hash_code The function providing hash codes for the type {@link T}. Can be {@code null}.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    public PredicateEqualityComparer(BiPredicate<T, T> predicate, @AllowNull HashCodeFunction<T> hash_code)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        this.predicate = predicate;
        this.hash_code = hash_code == null ? HashCodeFunction.GetDefault() : hash_code;
    }

    @Override
    protected int GetHashCodeImpl(T obj) { return hash_code.GetHashCode(obj); }

    @Override
    protected boolean EqualsImpl(T x, T y) { return predicate.predicate(x, y); }
}
