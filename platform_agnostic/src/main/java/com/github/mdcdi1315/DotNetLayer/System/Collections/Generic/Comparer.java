package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.IComparable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

public abstract class Comparer<T>
    implements IComparer<T>
{
    @NotNull
    public static <T> Comparer<T> GetDefault() { return new Default<>(); }

    private static final class Default<T>
        extends Comparer<T>
    {
        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public int Compare(T x, T y)
        {
            boolean y_is_null = y == null;
            if (x == null) {
                return y_is_null ? 0 : 1;
            } else if (y_is_null) {
                return -1;
            } else if (x instanceof CharSequence c && y instanceof CharSequence c2) {
                return CharSequence.compare(c, c2);
            } else if (x instanceof Comparable c) {
                return c.compareTo(y);
            } else if (x instanceof IComparable c) {
                return c.CompareTo(y);
            } else if (y instanceof Comparable c) {
                return -c.compareTo(x);
            } else if (y instanceof IComparable c) {
                return -c.CompareTo(x);
            } else {
                throw new ArgumentException("Could not compare the input values because they do not either implement any comparable interface.");
            }
        }
    }

    @Override
    public abstract int Compare(@AllowNull T x, @AllowNull T y);
}
