package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

/**
 * Provides the base interface for primitive double-precision floating-point integer enumerables. <br />
 * Extends from the {@link IEnumerable} interface.
 */
public interface IDoubleEnumerable
    extends IEnumerable<Double>
{
    /**
     * {@inheritDoc}
     */
    IDoubleEnumerator GetEnumerator();
}
