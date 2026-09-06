package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import java.util.Objects;

/**
 * {@link IEqualityComparer} implementation for Java objects in general.
 * @param <T> The type of the Java object to be compared.
 * @deprecated Use the {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.EqualityComparer.ObjectEqualityComparer}
 * class instead.
 * This class provides the same features and semantics as that one, so this definition has become redundant. <br />
 * All the library components that were using this are now using the beforementioned class. <br />
 * It won't be removed because it is harmless, but this one is not recommended for new development,
 * and probably won't make it to the Minecraft 26.1 port of the library.
 */
@Deprecated(since = "1.0.37")
public record JavaObjectEqualsEqualityComparer<T>()
    implements IEqualityComparer<T>, ISynchronized
{
    @Override
    public boolean Equals(T x, T y) { return Objects.equals(x, y); }

    @Override
    public int GetHashCode(T obj) { return Objects.hashCode(obj); }
}
