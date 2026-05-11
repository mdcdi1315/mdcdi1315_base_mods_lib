package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.IArrayBasedCollection;

import java.util.Objects;
import java.util.Iterator;
import java.util.Collection;
import java.lang.reflect.Array;

public final class CollectionBridgingHelpers
{
    private CollectionBridgingHelpers() {}

    public static <T, TE extends IEnumerable<T>> Object[] ToArray(TE enumerable, Func2<TE, Integer> count_accessor)
    {
        Object[] result = new Object[count_accessor.function(enumerable)];
        IEnumerator<T> enumerator = enumerable.GetEnumerator();
        try {
            int I = 0;
            while (enumerator.MoveNext() && I < result.length)
            {
                result[I++] = enumerator.getCurrent();
            }
        } finally {
            enumerator.Dispose();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, T1, TE extends IEnumerable<T>> T1[] ToArray(TE enumerable, Func2<TE, Integer> count_accessor, T1[] input_array)
    {
        int count = count_accessor.function(enumerable);
        if (input_array.length != count)
        {
            input_array = (T1[]) Array.newInstance(input_array.getClass().componentType(), count);
        }
        IEnumerator<T> enumerator = enumerable.GetEnumerator();
        try {
            int I = 0;
            while (enumerator.MoveNext() && I < input_array.length)
            {
                input_array[I++] = (T1)enumerator.getCurrent();
            }
        } finally {
            enumerator.Dispose();
        }
        return input_array;
    }

    public static <T> boolean Contains(IEnumerable<T> enumerable, Object value)
    {
        IEnumerator<T> enumerator = enumerable.GetEnumerator();
        try {
            while (enumerator.MoveNext())
            {
                if (Objects.equals(enumerator.getCurrent(), value)) { return true; }
            }
        } finally {
            enumerator.Dispose();
        }
        return false;
    }

    public static <T> boolean ContainsAll(IEnumerable<T> enumerable, Collection<?> values)
    {
        IEnumerator<T> enumerator;
        Iterator<?> iterator = values.iterator();

        Object element;
        while (iterator.hasNext())
        {
            element = iterator.next();
            enumerator = enumerable.GetEnumerator();
            try {
                while (enumerator.MoveNext())
                {
                    if (!Objects.equals(enumerator.getCurrent(), element)) { return false; }
                }
            } finally {
                enumerator.Dispose();
            }
        }
        return true;
    }

    public static <T, TE extends IEnumerable<T>> boolean AddAll(TE enumerable, Collection<? extends T> collection, Action1<T> adder)
    {
        boolean changed = false;
        if (enumerable instanceof IArrayBasedCollection ac) { ac.EnsureCapacity(collection.size()); changed = true; }
        if (collection.size() > 0) { changed = true; }
        for (T t : collection) { adder.action(t); }
        return changed;
    }
}
