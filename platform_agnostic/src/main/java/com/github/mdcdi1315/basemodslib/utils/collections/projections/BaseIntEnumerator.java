package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

/**
 * Provides a base enumerator abstraction class depending on both {@link BaseEnumerator} and {@link IIntEnumerator} classes.
 * @since 1.0.37
 */
public abstract class BaseIntEnumerator
    extends BaseEnumerator<Integer>
    implements IIntEnumerator
{
    /**
     * Initializes an instance of the {@link BaseIntEnumerator} class.
     */
    protected BaseIntEnumerator() { super(); }

    @Override
    public abstract int getUncastedCurrent();

    @NotNull
    @Override
    public Integer getCurrent() { return getUncastedCurrent(); }
}
