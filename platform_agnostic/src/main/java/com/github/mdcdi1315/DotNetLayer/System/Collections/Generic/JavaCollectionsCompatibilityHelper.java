package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.google.common.collect.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Defines compatibility methods for interoperating safely with the Java collections framework.
 */
public final class JavaCollectionsCompatibilityHelper
{
    // Do not let anyone instantiate this class.
    private JavaCollectionsCompatibilityHelper() {}

    private record EnumerableToIterableAdapter<T>(IEnumerable<T> enumerable)
            implements Iterable<T>
    {
        private static class EnumeratorToIteratorAdapter<TE>
                implements Iterator<TE>
        {
            private final IEnumerator<TE> enumerator;

            public EnumeratorToIteratorAdapter(IEnumerator<TE> e) {
                enumerator = e;
            }

            @Override
            public boolean hasNext() {
                boolean result = enumerator.MoveNext();
                if (!result) {
                    enumerator.Dispose();
                }
                return result;
            }

            @Override
            public TE next() {
                return enumerator.getCurrent();
            }
        }

        public Iterator<T> iterator() {
            return new EnumeratorToIteratorAdapter<>(enumerable.GetEnumerator());
        }
    }

    private record IterableToEnumerableAdapter<T>(Iterable<T> iterable)
        implements IEnumerable<T>
    {
        private record IteratorToEnumeratorAdapter<TE>(Iterator<TE> iterator)
            implements IEnumerator<TE>
        {
            @Override
            public TE getCurrent() {
                return iterator.next();
            }

            @Override
            public boolean MoveNext() {
                return iterator.hasNext();
            }

            @Override
            public void Reset() {
                // Cannot reset Java's iterators.
                throw new InvalidOperationException("Cannot reset the enumerator state.");
            }

            @Override
            public void Dispose() {
                // Empty method
            }
        }

        public IEnumerator<T> GetEnumerator() {
            return new IteratorToEnumeratorAdapter<>(iterable().iterator());
        }
    }

    private record InternalMapEntry<TK, TV>(TK key, TV value)
        implements Map.Entry<TK , TV>
    {
        @Override
        public TK getKey() {
            return key;
        }

        @Override
        public TV getValue() {
            return value;
        }

        // Java says that this implementation should update the backing map. We do not know from where KeyValuePair has came from, so we cannot assume that.
        @Override
        public TV setValue(TV value) {
            throw new NotSupportedException("This operation is not supported.");
        }
    }

    /**
     * Efficiently converts an {@link IEnumerable} instance to an {@link Iterable} instance. <br />
     * Conversion is straightforward and does not perform any allocations.
     * @param enumerable The {@link IEnumerable} instance to convert.
     * @return An {@link Iterable} instance representing the converted {@link IEnumerable} instance.
     * @param <T> The type of the elements that the enumerable conversion is performed for.
     * @throws ArgumentNullException {@code enumerable} was {@code null}.
     */
    public static <T> Iterable<T> AsIterable(IEnumerable<T> enumerable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable , "enumerable");
        return new EnumerableToIterableAdapter<>(enumerable);
    }

    /**
     * Efficiently converts an {@link Iterable} instance to an {@link IEnumerable} instance. <br />
     * Conversion is straightforward and does not perform any allocations.
     * @param iterable The {@link Iterable} to convert.
     * @return An {@link IEnumerable} instance representing the converted {@link Iterable} instance.
     * @param <T> The type of the elements that the enumerable conversion is performed for.
     * @throws ArgumentNullException {@code iterable} was {@code null}.
     */
    public static <T> IEnumerable<T> AsEnumerable(Iterable<T> iterable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(iterable, "iterable");
        return new IterableToEnumerableAdapter<>(iterable);
    }

    public static <T> ArrayList<T> AsArrayList(IList<T> list)
    {
        ArrayList<T> al = new ArrayList<>(list.getCount());
        for (int I = 0; I < list.getCount(); I++)
        {
            al.add(list.getItem(I));
        }
        return al;
    }

    public static <T> java.util.List<T> AsReadOnlyList(IList<T> list)
    {
        int count = list.getCount();
        ImmutableList.Builder<T> builder = ImmutableList.builderWithExpectedSize(count);
        for (int I = 0; I < count; I++)
        {
            builder.add(list.getItem(I));
        }
        return builder.build();
    }

    public static <TK, TV> HashMap<TK, TV> AsHashMap(IDictionary<TK, TV> dictionary)
    {
        HashMap<TK , TV> ret = new HashMap<>(dictionary.getCount());
        IEnumerator<KeyValuePair<TK , TV>> en = dictionary.GetEnumerator();
        try {
            KeyValuePair<TK , TV> kvp;
            while (en.MoveNext())
            {
                kvp = en.getCurrent();
                ret.put(kvp.getKey(), kvp.getValue());
            }
        } finally {
            en.Dispose();
        }
        return ret;
    }

    public static <TK, TV> Map<TK, TV> AsReadOnlyMap(IDictionary<TK , TV> dictionary)
    {
        ImmutableMap.Builder<TK , TV> mapbuilder = ImmutableMap.builderWithExpectedSize(dictionary.getCount());
        IEnumerator<KeyValuePair<TK , TV>> en = dictionary.GetEnumerator();
        try {
            KeyValuePair<TK , TV> kvp;
            while (en.MoveNext())
            {
                kvp = en.getCurrent();
                mapbuilder.put(kvp.getKey(), kvp.getValue());
            }
        } finally {
            en.Dispose();
        }
        return mapbuilder.build();
    }

    public static <TK, TV> Map.Entry<TK, TV> AsMapEntry(KeyValuePair<TK , TV> kvp)
    {
        return new InternalMapEntry<>(kvp.getKey(), kvp.getValue());
    }


}
