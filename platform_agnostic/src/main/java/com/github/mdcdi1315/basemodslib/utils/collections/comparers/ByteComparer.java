package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} implementation for {@link Byte} values.
 */
public final class ByteComparer
        extends AbstractNumericComparer<Byte>
{
    private ByteComparer() {}

    /**
     * Gets the single and only instance of the {@link ByteComparer} class.
     */
    public static final ByteComparer INSTANCE = new ByteComparer();

    @Pure
    public boolean Equals(byte x, byte y) { return x == y; }

    @Pure
    public int Compare(byte x, byte y) { return Byte.compare(x, y); }

    @Pure
    @Override
    public int GetHashCode(Byte obj) { return obj; }

    @Pure
    @Override
    protected boolean EqualsImpl(Byte x, Byte y) { return x.equals(y); }

    @Pure
    @Override
    protected int CompareImpl(Byte x, Byte y) { return x.compareTo(y); }
}
