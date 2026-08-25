package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

/**
 * Provides a base enumerator abstraction class depending on both {@link BaseEnumerator} and {@link ILongEnumerator} classes.
 * @since 1.0.37
 */
public abstract class BaseLongEnumerator
    extends BaseEnumerator<Long>
    implements ILongEnumerator
{
    /**
     * Initializes an instance of the {@link BaseLongEnumerator} class.
     */
    protected BaseLongEnumerator() { super(); }

    @Override
    public abstract long getUncastedCurrent();

    @NotNull
    @Override
    public Long getCurrent() { return getUncastedCurrent(); }
}
