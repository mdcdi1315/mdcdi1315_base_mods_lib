package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.ICharEnumerator;

/**
 * Provides an {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator}
 * implementation for {@link CharSequence} instances.
 * @since 1.0.35
 */
@Pure
public final class CharSequenceEnumerator
    extends BaseEnumerator<Character>
    implements ICharEnumerator
{
    private int index;
    private CharSequence sequence;

    /**
     * Initializes a new instance of the {@link CharSequenceEnumerator} class.
     * @param sequence The {@link CharSequence} to initialize the enumerator from.
     * @throws ArgumentNullException {@code sequence} is {@code null}.
     */
    public CharSequenceEnumerator(CharSequence sequence)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(sequence, "sequence");
        index = -1;
        this.sequence = sequence;
    }

    @Pure
    @Override
    protected void ResetImpl() { index = -1; }

    @Pure
    @NotNull
    @Override
    public Character getCurrent() { return sequence.charAt(index); }

    @Pure
    @Override
    public char getUncastedCurrent() { return sequence.charAt(index); }

    @Pure
    @Override
    protected boolean MoveNextImpl() { return ++index < sequence.length(); }

    @Pure
    @Override
    public void Dispose()
    {
        synchronized (this)
        {
            super.Dispose();
            index = -1;
            sequence = null;
        }
    }
}
