package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

/**
 * Provides the base interface for primitive single-precision floating-point integer enumerables. <br />
 * Extends from the {@link IEnumerable} interface.
 */
public interface IFloatEnumerable
        extends IEnumerable<Float>
{
    /**
     * {@inheritDoc}
     */
    IFloatEnumerator GetEnumerator();
}
