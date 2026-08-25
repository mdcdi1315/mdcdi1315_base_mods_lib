package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator;

/**
 * Marker interface applied on classes that represent empty collection objects. <br />
 * The {@link CollectionManipulations} class will query against this interface if/when required. <br />
 * Collections implementing this interface must:
 * <ol>
 *     <li>Implement the {@link IEnumerable} interface at least.</li>
 *     <li>Must return an enumerator that does not return any elements ({@link IEnumerator#MoveNext()} must always return {@code false}).</li>
 * </ol>
 * @since 1.0.37
 */
public interface IEmptyEnumerable { }
