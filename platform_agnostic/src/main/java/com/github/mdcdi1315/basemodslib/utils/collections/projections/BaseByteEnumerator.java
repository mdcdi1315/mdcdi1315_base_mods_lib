package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

/**
 * Provides a base enumerator abstraction class depending on both {@link BaseEnumerator} and {@link IByteEnumerator} classes.
 * @since 1.0.37
 */
public abstract class BaseByteEnumerator
    extends BaseEnumerator<Byte>
    implements IByteEnumerator
{
    /**
     * Initializes an instance of the {@link BaseByteEnumerator} class.
     */
    protected BaseByteEnumerator() { super(); }

    @Override
    public abstract byte getUncastedCurrent();

    @NotNull
    @Override
    public Byte getCurrent() { return getUncastedCurrent(); }
}
