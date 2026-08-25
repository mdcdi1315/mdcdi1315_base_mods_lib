package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

/**
 * Provides an {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer}
 * implementation for comparing character sequences.
 */
public final class CharSequenceEqualityComparer
    extends AbstractObjectEqualityComparer<CharSequence>
{
    private final boolean ignore_case;

    private CharSequenceEqualityComparer(boolean ignore_case) { this.ignore_case = ignore_case; }

    /**
     * Gets the single and only instance of the {@link CharSequenceEqualityComparer} class,
     * comparing the sequences in a case-sensitive manner.
     */
    public static final CharSequenceEqualityComparer SENSITIVE = new CharSequenceEqualityComparer(false);

    /**
     * Gets the single and only instance of the {@link CharSequenceEqualityComparer} class,
     * comparing the sequences in a case-insensitive manner.
     */
    public static final CharSequenceEqualityComparer INSENSITIVE = new CharSequenceEqualityComparer(true);

    @Override
    protected int GetHashCodeImpl(CharSequence obj) { return obj.hashCode(); }

    private boolean EqualsSensitive(CharSequence x, CharSequence y)
    {
        int len = x.length();
        if (len == y.length()) {
            for (int I = 0; I < len; I++)
            {
                if (x.charAt(I) != y.charAt(I)) { return false; }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean EqualsInsensitive(CharSequence x, CharSequence y)
    {
        int len = x.length();
        if (len == y.length()) {
            for (int I = 0; I < len; I++)
            {
                if (Character.toLowerCase(x.charAt(I)) != Character.toLowerCase(y.charAt(I))) { return false; }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean EqualsImpl(CharSequence x, CharSequence y)
    {
        return ignore_case ?
                EqualsInsensitive(x, y) :
                EqualsSensitive(x, y);
    }
}
