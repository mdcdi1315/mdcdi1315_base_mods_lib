

package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.IDisposable;

/**
 * Supports a simple iteration over a generic collection.
 * @param <T> The type of objects to enumerate.
 */
public interface IEnumerator<@DotNetByRefParameter(ByRefParameterType.OUT) T>
        extends com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator , IDisposable
{
    /**
     * Gets the element in the collection at the current position of the enumerator.
     * @return The element in the collection at the current position of the enumerator.
     */
    T getCurrent();
}