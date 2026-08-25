package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} implementation for {@link Double} values.
 */
public final class DoubleComparer
    extends AbstractNumericComparer<Double>
{
    private DoubleComparer() {}

    /**
     * Gets the single and only instance of the {@link DoubleComparer} class.
     */
    public static final DoubleComparer INSTANCE = new DoubleComparer();

    @Pure
    public boolean Equals(double x, double y) { return x == y; }

    @Pure
    public int Compare(double x, double y) { return Double.compare(x, y); }

    @Pure
    @Override
    protected int CompareImpl(Double x, Double y) { return x.compareTo(y); }

    @Pure
    @Override
    protected boolean EqualsImpl(Double x, Double y) { return x.equals(y); }
}
