package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotImplementedException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.HashCodeFunction;

import java.util.Objects;

/**
 * Provides a template class that depends on an instance
 * of the {@link IComparer} interface to compare values.
 * Hash code method needs still to be implemented by the consumers.
 * @param <T> The type of the elements to compare.
 */
public abstract class ComparerBasedEqualityComparer<T>
    implements IEqualityComparer<T>, HashCodeFunction<T>
{
    private final IComparer<T> comparer;

    protected ComparerBasedEqualityComparer(IComparer<T> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.comparer = comparer, "comparer");
    }

    /**
     * Returns a new instance of the {@link ComparerBasedEqualityComparer} class, that it's
     * {@link #GetHashCode(Object)} method is unimplemented and terminally throws {@link NotImplementedException}.
     * @param comparer The {@link IComparer} to use.
     * @return A new instance of the {@link ComparerBasedEqualityComparer} class that has it's {@link #GetHashCode(Object)} method unimplemented.
     * @param <T> The type of the elements to compare.
     * @throws ArgumentNullException {@code comparer} is {@code null}.
     */
    @NotNull
    public static <T> ComparerBasedEqualityComparer<T> Of(IComparer<T> comparer) throws ArgumentNullException { return new GetHashCodeUnimplemented<>(comparer); }

    /**
     * Returns a new instance of the {@link ComparerBasedEqualityComparer} class, that it's
     * {@link #GetHashCode(Object)} method is implemented by using the {@link Objects#hashCode(Object)} method.
     * @param comparer The {@link IComparer} to use.
     * @return A new instance of the {@link ComparerBasedEqualityComparer} class that has it's
     * {@link #GetHashCode(Object)} method implemented using the {@link Objects#hashCode(Object)} method.
     * @param <T> The type of the elements to compare.
     * @throws ArgumentNullException {@code comparer} is {@code null}.
     */
    @NotNull
    public static <T> ComparerBasedEqualityComparer<T> OfDefaultHashCodeFunction(IComparer<T> comparer)
        throws ArgumentNullException { return new GetHashCodeByJavaObject<>(comparer); }

    /**
     * Returns a new instance of the {@link ComparerBasedEqualityComparer} class, that it's
     * {@link #GetHashCode(Object)} method is implemented by using the function reference
     * provided in {@code function} parameter.
     * @param comparer The {@link IComparer} to use.
     * @param function The {@link HashCodeFunction} to use for hashing objects of type {@link T}.
     * @return A new instance of the {@link ComparerBasedEqualityComparer} class that has it's
     * {@link #GetHashCode(Object)} method implemented using the function reference provided in {@code function}.
     * @param <T> The type of the elements to compare.
     * @throws ArgumentNullException {@code comparer} and/or {@code function} are {@code null}.
     */
    @NotNull
    public static <T> ComparerBasedEqualityComparer<T> ByHashCodeFunction(IComparer<T> comparer, HashCodeFunction<T> function)
            throws ArgumentNullException { return new GetHashCodeByFunction<>(comparer, function); }

    private static final class GetHashCodeUnimplemented<T>
        extends ComparerBasedEqualityComparer<T>
    {
        public GetHashCodeUnimplemented(IComparer<T> comparer) throws ArgumentNullException { super(comparer); }

        @Override
        public int GetHashCode(T obj) { throw new NotImplementedException(); }
    }

    private static final class GetHashCodeByJavaObject<T>
        extends ComparerBasedEqualityComparer<T>
    {
        public GetHashCodeByJavaObject(IComparer<T> comparer) throws ArgumentNullException { super(comparer); }

        @Override
        public int GetHashCode(T obj) { return Objects.hashCode(obj); }
    }

    private static final class GetHashCodeByFunction<T>
        extends ComparerBasedEqualityComparer<T>
    {
        private final HashCodeFunction<T> function;

        public GetHashCodeByFunction(IComparer<T> comparer, HashCodeFunction<T> function)
                throws ArgumentNullException
        {
            super(comparer);
            ArgumentNullException.ThrowIfNull(this.function = function, "function");
        }

        @Override
        public int GetHashCode(T obj) { return function.GetHashCode(obj); }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int GetHashCode(@DisallowNull T obj);

    /**
     * Gets the {@link IComparer} instance that is in use. <br />
     * Always non-null.
     * @return The comparer instance that this instance uses.
     */
    @Pure
    @NotNull
    public final IComparer<T> GetComparer() { return comparer; }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean Equals(@AllowNull T x, @AllowNull T y) { return comparer.Compare(x, y) == 0; }
}
