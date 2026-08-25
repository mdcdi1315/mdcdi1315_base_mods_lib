package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.IComparable;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import java.util.Locale;
import java.text.Collator;

/**
 * Compares two objects for equivalence, where string comparisons are case-sensitive.
 */
public final class Comparer
    implements IComparer
{
    private Comparer(boolean invariant)
    {
        collator = invariant ? Collator.getInstance(Locale.ROOT) : Collator.getInstance(Locale.getDefault());
    }

    private final Collator collator;

    /**
     * Represents an instance of {@link Comparer} that is associated with the CurrentCulture of the current thread.
     * This field is read-only.
     */
    public static final Comparer Default = new Comparer(false);
    /**
     * Represents an instance of {@link Comparer} that is associated with InvariantCulture.
     * This field is read-only.
     */
    public static final Comparer DefaultInvariant = new Comparer(true);

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int Compare(@AllowNull Object a, @AllowNull Object b)
    {
        if (a == b) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        } else {
            return switch (a)
            {
                case String sa when b instanceof String sb -> collator.compare(sa, sb);
                case IComparable ia -> ia.CompareTo(b);
                case Comparable ia -> ia.compareTo(b);
                default ->
                {
                    if (b instanceof IComparable ib)
                        yield -ib.CompareTo(a);
                    else if (b instanceof Comparable ib)
                        yield -ib.compareTo(a);
                    else
                        throw new ArgumentException("The specified arguments do not implement IComparable");
                }
            };
        }
    }
}
