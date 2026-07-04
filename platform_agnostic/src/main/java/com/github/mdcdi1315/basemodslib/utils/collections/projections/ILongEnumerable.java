package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

/**
 * Provides the base interface for primitive {@code long} integer enumerables. <br />
 * Extends from the {@link IEnumerable} interface.
 */
public interface ILongEnumerable
    extends IEnumerable<Long>
{
    /**
     * {@inheritDoc}
     */
    ILongEnumerator GetEnumerator();
}