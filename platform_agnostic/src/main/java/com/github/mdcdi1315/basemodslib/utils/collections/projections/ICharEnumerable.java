package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

/**
 * Provides the base interface for primitive character enumerables. <br />
 * Extends from the {@link IEnumerable} interface.
 */
@SuppressWarnings("SpellCheckingInspection")
public interface ICharEnumerable
    extends IEnumerable<Character>
{
    /**
     * {@inheritDoc}
     */
    @Override
    ICharEnumerator GetEnumerator();
}
