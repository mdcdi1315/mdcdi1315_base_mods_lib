package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

/**
 * Represents a read-only collection of elements that can be accessed by index.
 * @param <T> The type of elements in the read-only list.
 */
public interface IReadOnlyList<@DotNetByRefParameter(ByRefParameterType.OUT) T>
    extends IReadOnlyCollection<T>
{
    /**
     * Gets the element at the specified index in the read-only list.
     * @param index The zero-based index of the element to get.
     * @return The element at the specified index in the read-only list.
     */
    T getItem(int index);
}
