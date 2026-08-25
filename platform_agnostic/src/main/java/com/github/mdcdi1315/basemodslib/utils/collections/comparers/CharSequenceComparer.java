package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import java.util.PrimitiveIterator;

/**
 * Provides a comparer implementation for character sequences. <br />
 * The comparer compares sequences as follows:
 * <ol>
 *     <li>Gets the length of both sequences.</li>
 *     <li>If their lengths are <em>NOT</em> the same, their length difference is returned as a result.</li>
 *     <li>
 *         Otherwise, it gets each character from each sequence one by one, and if a character
 *         happens to have a different value from the character from the other sequence,
 *         their value difference is returned as a result. <br />
 *         Comparison of characters can be done sensitive or insensitive,
 *         depending on whether the {@link #INSENSITIVE} or the {@link #SENSITIVE} instance is used.
 *     </li>
 *     <li>
 *         If the above completed without any outcome, it means that both sequences
 *         are equal, and as such 0 is returned.
 *     </li>
 * </ol>
 */
public final class CharSequenceComparer
    extends AbstractObjectComparer<CharSequence>
{
    private final boolean ignore_case;

    private CharSequenceComparer(boolean ignore_case) { this.ignore_case = ignore_case; }

    /**
     * Gets the instance of the {@link CharSequenceComparer} class, comparing
     * the sequences in a case-sensitive manner.
     */
    public static final CharSequenceComparer SENSITIVE = new CharSequenceComparer(false);

    /**
     * Gets the instance of the {@link CharSequenceComparer} class, comparing
     * the sequences in a case-insensitive manner.
     */
    public static final CharSequenceComparer INSENSITIVE = new CharSequenceComparer(true);

    private int CompareSensitive(CharSequence x, CharSequence y)
    {
        int len_x = x.length();
        int len_y = y.length();

        if (len_x == len_y) {
            int ch_x, ch_y;
            // Iterate over the characters.
            PrimitiveIterator.OfInt iter_x = x.chars().iterator();
            PrimitiveIterator.OfInt iter_y = y.chars().iterator();
            while (iter_x.hasNext() && iter_y.hasNext())
            {
                ch_x = iter_x.nextInt();
                ch_y = iter_y.nextInt();
                // If these do not match, we have our comparison;
                // again, the one that is greater is favored.
                if (ch_x != ch_y) { return ch_x - ch_y; }
            }
            // Otherwise, the sequences are equal to each other.
            return 0;
        } else {
            // If their lengths do not match, favor the one whose length is greater than the other.
            return len_x - len_y;
        }
    }

    private int CompareInsensitive(CharSequence x, CharSequence y)
    {
        int len_x = x.length();
        int len_y = y.length();

        if (len_x == len_y) {
            int ch_x, ch_y;
            // Iterate over the characters.
            PrimitiveIterator.OfInt iter_x = x.chars().iterator();
            PrimitiveIterator.OfInt iter_y = y.chars().iterator();
            while (iter_x.hasNext() && iter_y.hasNext())
            {
                ch_x = iter_x.nextInt();
                ch_y = iter_y.nextInt();
                // If these do not match, we have our comparison;
                // again, the one that is greater is favored.
                if (Character.toLowerCase(ch_x) != Character.toLowerCase(ch_y)) { return ch_x - ch_y; }
            }
            // Otherwise, the sequences are equal to each other.
            return 0;
        } else {
            // If their lengths do not match, favor the one whose length is greater than the other.
            return len_x - len_y;
        }
    }

    @Override
    protected int CompareImpl(CharSequence x, CharSequence y)
    {
        return ignore_case ?
                CompareInsensitive(x, y) :
                CompareSensitive(x, y);
    }
}
