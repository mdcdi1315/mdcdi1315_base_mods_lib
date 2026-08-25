package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} implementation for {@link Integer} values.
 */
public final class IntComparer
    extends AbstractNumericComparer<Integer>
{
    private IntComparer() {}

    /**
     * Gets the single and only instance of the {@link IntComparer} class.
     */
    public static final IntComparer INSTANCE = new IntComparer();

    @Pure
    public boolean Equals(int x, int y) { return x == y; }

    @Pure
    public int Compare(int x, int y) { return Integer.compare(x, y); }

    @Pure
    @Override
    public int GetHashCode(Integer obj) { return obj; }

    @Pure
    @Override
    protected int CompareImpl(Integer x, Integer y) { return x.compareTo(y); }

    @Pure
    @Override
    protected boolean EqualsImpl(Integer x, Integer y) { return x.equals(y); }
}
