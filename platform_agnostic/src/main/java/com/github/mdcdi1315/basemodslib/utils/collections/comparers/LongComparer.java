package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} implementation for {@link Long} values.
 */
public final class LongComparer
    extends AbstractNumericComparer<Long>
{
    private LongComparer() {}

    /**
     * Gets the single and only instance of the {@link LongComparer} class.
     */
    public static final LongComparer INSTANCE = new LongComparer();

    @Pure
    public boolean Equals(long x, long y) { return x == y; }

    @Pure
    public int Compare(long a, long b) { return Long.compare(a, b); }

    @Pure
    @Override
    protected int CompareImpl(Long x, Long y) { return x.compareTo(y); }

    @Pure
    @Override
    protected boolean EqualsImpl(Long x, Long y) { return x.equals(y); }
}
