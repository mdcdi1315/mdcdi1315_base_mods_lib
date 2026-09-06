package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer;} implementation for {@link Float} values.
 */
public final class FloatComparer
    extends AbstractNumericComparer<Float>
{
    private FloatComparer() {}

    /**
     * Gets the single and only instance of the {@link FloatComparer} class.
     */
    public static final FloatComparer INSTANCE = new FloatComparer();

    @Pure
    public boolean Equals(float x, float y) { return x == y; }

    @Pure
    public int Compare(float x, float y) { return Float.compare(x, y); }

    @Pure
    @Override
    protected int CompareImpl(Float x, Float y) { return x.compareTo(y); }

    @Pure
    @Override
    protected boolean EqualsImpl(Float x, Float y) { return x.equals(y); }
}
