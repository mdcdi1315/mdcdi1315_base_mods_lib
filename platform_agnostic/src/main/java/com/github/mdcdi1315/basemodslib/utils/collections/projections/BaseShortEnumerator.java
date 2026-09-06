package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

/**
 * Provides a base enumerator abstraction class depending on both {@link BaseEnumerator} and {@link IShortEnumerator} classes.
 * @since 1.0.37
 */
public abstract class BaseShortEnumerator
    extends BaseEnumerator<Short>
    implements IShortEnumerator
{
    /**
     * Initializes an instance of the {@link BaseShortEnumerator} class.
     */
    protected BaseShortEnumerator() { super(); }

    @Override
    public abstract short getUncastedCurrent();

    @NotNull
    @Override
    public Short getCurrent() { return getUncastedCurrent(); }
}
