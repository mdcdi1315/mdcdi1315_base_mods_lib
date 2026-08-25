package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

/**
 * Provides a base enumerator abstraction class depending on both {@link BaseEnumerator} and {@link IDoubleEnumerator} classes.
 * @since 1.0.37
 */
public abstract class BaseDoubleEnumerator
    extends BaseEnumerator<Double>
    implements IDoubleEnumerator
{
    /**
     * Initializes an instance of the {@link BaseDoubleEnumerator} class.
     */
    protected BaseDoubleEnumerator() { super(); }

    @Override
    public abstract double getUncastedCurrent();

    @NotNull
    @Override
    public Double getCurrent() { return getUncastedCurrent(); }
}
