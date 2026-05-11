package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Converter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithNextPointer;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithNextPointerEnumerator;

/**
 * Provides a simple and fast implementation of the {@link IRegister} interface. <br />
 * It is based off on a single-linked-list that has addition time O(1), and traversal time O(n),
 * where {@code n} the number of elements registered.
 * @param <T> The type of elements to be registered and enumerated at a later time.
 */
public class SingleLinkedListBasedRegister<T>
    extends BaseEnumerable<T>
    implements IRegister<T>
{
    @AllowNull
    private NodeWithNextPointer<T> root, current;

    /**
     * Initializes a new and empty instance of the {@link SingleLinkedListBasedRegister} class.
     */
    public SingleLinkedListBasedRegister() { root = current = null; }

    @Override
    public void Register(@AllowNull T item)
            throws ArgumentException
    {
        NodeWithNextPointer<T> n = new NodeWithNextPointer<>(item);
        if (root == null) {
            root = current = n;
        } else {
            current.Next = n;
            current = n;
        }
    }

    @NotNull
    @Override
    public IEnumerator<T> GetEnumerator() { return new NodeWithNextPointerEnumerator<>(root); }

    /**
     * Use this value to determine whether the {@link #Register(Object)} method has been called at least once.
     * @return A value whether at least one item is contained in this {@link SingleLinkedListBasedRegister} object.
     * @since 1.0.24
     */
    public boolean HasItems() { return root != null; }

    @NotNull
    @Override
    public <TO> SingleLinkedListBasedRegister<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");

        SingleLinkedListBasedRegister<TO> result = new SingleLinkedListBasedRegister<>();

        IEnumerator<T> enumerator = GetEnumerator();
        try {
            while (enumerator.MoveNext()) { result.Register(converter.convert(enumerator.getCurrent())); }
        } finally {
            enumerator.Dispose();
        }

        return result;
    }

    @NotNull
    @Override
    public SingleLinkedListBasedRegister<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new SingleLinkedListBasedRegister<>();
        } else {
            SingleLinkedListBasedRegister<T> register = new SingleLinkedListBasedRegister<>();
            IEnumerator<T> enumerator = GetEnumerator();
            try {
                T item;
                while (enumerator.MoveNext()) {
                    if (predicate.predicate(item = enumerator.getCurrent())) { register.Register(item); }
                }
            } finally {
                enumerator.Dispose();
            }
            return register;
        }
    }

    /**
     * Provides a string representation of this object. <br />
     * For debugging purposes only.
     * @return A string representation of this object.
     * @since 1.0.31
     */
    @NotNull
    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SingleLinkedListBasedRegister<?> { ");
        if (root == null) {
            sb.append("<EMPTY>");
        } else {
            NodeWithNextPointer<T> p = root, next;
            while (p != null)
            {
                sb.append(p.Value);
                if ((next = p.Next) != null) { sb.append(", "); }
                p = next;
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
