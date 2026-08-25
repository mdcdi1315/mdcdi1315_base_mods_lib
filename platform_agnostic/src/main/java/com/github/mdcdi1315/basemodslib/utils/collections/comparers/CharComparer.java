package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.HashCodeFunction;

import java.util.Objects;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} implementation for {@link Character} values.
 */
public final class CharComparer
    extends AbstractObjectComparer<Character>
    implements IEqualityComparer<Character>,
        HashCodeFunction<Character>
{
    private CharComparer() {}

    /**
     * Gets the single and only instance of the {@link CharComparer} class.
     */
    public static final CharComparer INSTANCE = new CharComparer();

    @Pure
    public boolean Equals(char x, char y) { return x == y; }

    @Pure
    public int Compare(char x, char y) { return Character.compare(x, y); }

    @Pure
    @Override
    protected int CompareImpl(Character x, Character y) { return x.compareTo(y); }

    @Pure
    @Override
    public boolean Equals(Character x, Character y)
    {
        boolean x_is_null = x == null;
        if (x_is_null) {
            return y == null;
        } else if (y == null) {
            return false;
        } else {
            return x.charValue() == y.charValue();
        }
    }

    @Pure
    @Override
    public int GetHashCode(Character obj) { return Objects.hashCode(obj); }
}
