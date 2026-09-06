package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

/**
 * Provides a base enumerator abstraction class depending on both {@link BaseEnumerator} and {@link IFloatEnumerator} classes.
 * @since 1.0.37
 */
public abstract class BaseFloatEnumerator
    extends BaseEnumerator<Float>
    implements IFloatEnumerator
{
    /**
     * Initializes an instance of the {@link BaseFloatEnumerator} class.
     */
    protected BaseFloatEnumerator() { super(); }

    @Override
    public abstract float getUncastedCurrent();

    @NotNull
    @Override
    public Float getCurrent() { return getUncastedCurrent(); }
}
