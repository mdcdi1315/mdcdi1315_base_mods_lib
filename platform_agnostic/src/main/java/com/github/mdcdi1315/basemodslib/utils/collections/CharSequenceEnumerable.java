package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.ICharEnumerable;

/**
 * Provides an enumerable object that manages a {@link CharSequence} instance efficiently. <br />
 * It does also implement the {@link ICharEnumerable}, the {@link ITraversableCollection} and
 * the {@link ISupportsCloning} collection interfaces.
 * @since 1.0.35
 */
public final class CharSequenceEnumerable
    extends BaseEnumerable<Character>
    implements ICharEnumerable,
        ITraversableCollection<Character>,
        ISupportsCloning<Character>,
        ISynchronized
{
    private final CharSequence sequence;

    /**
     * Gets an instance of the {@link CharSequenceEnumerable} class that does not have any contents.
     * @since 1.0.37
     */
    public static final CharSequenceEnumerable EMPTY = new CharSequenceEnumerable(StringUtils.Empty);

    /**
     * Initializes a new instance of the {@link CharSequenceEnumerable} class.
     * @param sequence The {@link CharSequence} to initialize the enumerable from.
     * @throws ArgumentNullException {@code sequence} is {@code null}.
     */
    public CharSequenceEnumerable(CharSequence sequence)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(sequence, "sequence");
        this.sequence = sequence;
    }

    /**
     * Constructs a new instance of the {@link CharSequenceEnumerable} class, obeying
     * the existence and semantics of the {@link #EMPTY} constant.
     * @param sequence The {@link CharSequence} to initialize the enumerable from.
     * @return A new instance of the {@link CharSequenceEnumerable} class, or {@link #EMPTY} is {@code sequence} is empty.
     * @throws ArgumentNullException {@code sequence} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static CharSequenceEnumerable Of(CharSequence sequence)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(sequence, "sequence");
        return sequence.isEmpty() ? EMPTY : new CharSequenceEnumerable(sequence);
    }

    /**
     * Constructs a new instance of the {@link CharSequenceEnumerable} class, obeying
     * the existence and semantics of the {@link #EMPTY} constant.
     * @param sequence The {@link CharSequence} to initialize the enumerable from.
     * @return A new instance of the {@link CharSequenceEnumerable} class, or {@link #EMPTY} is {@code sequence} is empty or {@code null}.
     * @since 1.0.37
     */
    @Pure
    @NotNull
    public static CharSequenceEnumerable OfNullable(@AllowNull CharSequence sequence)
    {
        return (sequence == null || sequence.isEmpty()) ? EMPTY : new CharSequenceEnumerable(sequence);
    }

    @NotNull
    @Override
    public Character GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        try {
            return sequence.charAt(index);
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentOutOfRangeException("index", e.getMessage());
        }
    }

    /**
     * Performs similarly as the {@link #GetItem(int)} method, but returns the value unboxed.
     * @param index The position of the element to get.
     * @return The element at {@code index}.
     * @throws IndexOutOfRangeException {@code index} is negative -or- is outside the array's bounds.
     */
    public char GetUnboxedItem(int index)
            throws IndexOutOfRangeException
    {
        try {
            return sequence.charAt(index);
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentOutOfRangeException("index", e.getMessage());
        }
    }

    @NotNull
    @Override
    public CharSequenceEnumerable Slice(int index, int count)
            throws ArgumentException
    {
        if (count == 0) {
            return EMPTY;
        } else {
            try {
                return new CharSequenceEnumerable(sequence.subSequence(index, index + count));
            } catch (IndexOutOfBoundsException e) {
                throw new ArgumentException(e.getMessage());
            }
        }
    }

    @NotNull
    @Override
    public CharSequenceEnumerable FilterBy(Predicate<Character> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (sequence.isEmpty() || FunctionManipulations.IsAlwaysFalse(predicate)) {
            return EMPTY;
        } else {
            StringBuilder builder = new StringBuilder(sequence.length());

            try (var enumerator = GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    Character current = enumerator.getCurrent();
                    if (predicate.predicate(current)) { builder.append(current); }
                }
            }

            return builder.isEmpty() ? EMPTY : new CharSequenceEnumerable(builder);
        }
    }

    @Override
    public int GetCount() { return sequence.length(); }

    /**
     * Returns a string containing the characters in this
     * enumerable in the same order as this enumerable.
     * The length of the string will be the length of this enumerable,
     * which can be queried using the {@link #GetCount()} method.
     * @return A string consisting of exactly this enumerable of characters.
     */
    @NotNull
    public String GetString() { return sequence.toString(); }

    @NotNull
    @Override
    public CharSequenceEnumerator GetEnumerator() { return new CharSequenceEnumerator(sequence); }

    @NotNull
    @Override
    public CharSequenceEnumerable Slice(int count) throws ArgumentException { return Slice(0, count); }

    @Pure
    @NotNull
    @Override
    public CharSequenceEnumerable Clone() { return sequence.isEmpty() ? EMPTY : new CharSequenceEnumerable(sequence); }

    @NotNull
    @Override
    public String toString() { return String.format("CharSequenceEnumerable (%d) { \"%s\" }", sequence.length(), sequence); }
}
