package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

/**
 * Provides a base enumerator abstraction class depending on both {@link BaseEnumerator} and {@link ICharEnumerator} classes.
 * @since 1.0.37
 */
public abstract class BaseCharEnumerator
    extends BaseEnumerator<Character>
    implements ICharEnumerator
{
    /**
     * Initializes an instance of the {@link BaseCharEnumerator} class.
     */
    protected BaseCharEnumerator() { super(); }

    @Override
    public abstract char getUncastedCurrent();

    @NotNull
    @Override
    public Character getCurrent() { return getUncastedCurrent(); }
}
