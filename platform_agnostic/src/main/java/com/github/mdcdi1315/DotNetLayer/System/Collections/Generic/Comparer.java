package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

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
            if (x == null && y == null) {
                return 0;
            } else if (x == null) {
                return 1;
            } else if (y == null) {
                return -1;
            } else if (x instanceof Comparable c) {
                return c.compareTo(y);
            } else if (x instanceof IComparable c) {
                return c.CompareTo(y);
            } else {
                return x.equals(y) ? 0 : 1;
            }
        }
    }

    @Override
    public abstract int Compare(@AllowNull T x, @AllowNull T y);
}
