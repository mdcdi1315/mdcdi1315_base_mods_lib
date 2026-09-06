package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} implementation for {@link Short} values.
 */
public final class ShortComparer
    extends AbstractNumericComparer<Short>
{
    private ShortComparer() {}

    /**
     * Gets the single and only instance of the {@link ShortComparer} class.
     */
    public static final ShortComparer INSTANCE = new ShortComparer();

    @Pure
    public boolean Equals(short x, short y) { return x == y; }

    @Pure
    public int Compare(short x, short y) { return Short.compare(x, y); }

    @Pure
    @Override
    public int GetHashCode(Short obj) { return obj; }

    @Pure
    @Override
    protected int CompareImpl(Short x, Short y) { return x.compareTo(y); }

    @Pure
    @Override
    protected boolean EqualsImpl(Short x, Short y) { return x.equals(y); }
}
