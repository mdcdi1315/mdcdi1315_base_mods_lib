package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.IArrayBasedCollection;
import com.github.mdcdi1315.basemodslib.utils.function.ConvertsToIntegerFunction;

import java.util.Objects;
import java.util.Iterator;
import java.util.Collection;
import java.lang.reflect.Array;

public final class CollectionHelpers
{
    private CollectionHelpers() {}

    public static <T, TE extends IEnumerable<T>> Object[] ToArray(TE enumerable, ConvertsToIntegerFunction<TE> count_accessor)
    {
        Object[] result = new Object[count_accessor.to_int(enumerable)];
        try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
        {
            int I = 0;
            while (I < result.length && enumerator.MoveNext())
            {
                result[I++] = enumerator.getCurrent();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, T1, TE extends IEnumerable<T>> T1[] ToArray(TE enumerable, ConvertsToIntegerFunction<TE> count_accessor, T1[] input_array)
    {
        int count = count_accessor.to_int(enumerable);
        if (input_array.length < count)
        {
            input_array = (T1[]) Array.newInstance(input_array.getClass().componentType(), count);
        }
        try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
        {
            int I = 0;
            while (I < input_array.length && enumerator.MoveNext())
            {
                input_array[I++] = (T1) enumerator.getCurrent();
            }
        }
        return input_array;
    }

    public static <T> boolean Contains(IEnumerable<T> enumerable, Object value)
    {
        try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
        {
            while (enumerator.MoveNext())
            {
                if (Objects.equals(enumerator.getCurrent(), value)) { return true; }
            }
        }
        return false;
    }

    public static <T> boolean ContainsAll(IEnumerable<T> enumerable, Collection<?> values)
    {
        Iterator<?> iterator = values.iterator();

        Object element;
        while (iterator.hasNext())
        {
            element = iterator.next();
            try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    if (!Objects.equals(enumerator.getCurrent(), element)) { return false; }
                }
            }
        }
        return true;
    }

    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public static <T, TE extends IEnumerable<T>> boolean AddAll(TE enumerable, Collection<? extends T> collection, Action1<T> adder)
    {
        boolean changed = false;
        if (enumerable instanceof IArrayBasedCollection ac) { ac.EnsureCapacity(collection.size()); changed = true; }
        if (collection.size() > 0) { changed = true; }
        for (T t : collection) { adder.action(t); }
        return changed;
    }

    @StackTraceHidden
    public static void CheckIndexCountInsideCollectionBound(int index, int count, int collection_count)
            throws OverflowException, ArgumentException
    {
        int r = index + count;
        // HD 2-12 Overflow if both arguments have the opposite sign of the result
        if (((index ^ r) & (count ^ r)) < 0) {
            throw new OverflowException("The specified combination of index and count parameters do exceed the maximum integer value.");
        } else if (r > collection_count) {
            throw new ArgumentException("The specified combination of the index and count parameters are outside of the collections's bounds.");
        }
    }

    @StackTraceHidden
    public static void CheckCopyToArguments(int array_index, int array_count, int collection_count)
            throws OverflowException, ArgumentException
    {
        int r = array_index + collection_count;
        // HD 2-12 Overflow if both arguments have the opposite sign of the result
        if (((array_index ^ r) & (collection_count ^ r)) < 0) {
            throw new OverflowException("The specified combination of array index parameter and the collection's count do exceed the maximum integer value.");
        } else if (r > array_count) {
            throw new ArgumentException("The array does not have enough space to place all the elements of the current collection object.");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> int IndexOfArray(Object[] array, int index, int count, @AllowNull T item, IEqualityComparer<T> comparer)
    {
        for (int I = 0; I < count; I++)
        {
            if (comparer.Equals((T)array[I + index], item)) { return I; }
        }
        return -1;
    }

    @NotNull
    public static String GetStringSafe(Object o)
    {
        if (o == null) { return "<NULL>"; }
        try {
            return o.toString();
        } catch (Exception ex) {
            return StringUtils.Empty;
        }
    }

    @NotNull
    public static <T> String PutArrayContentsToString(T[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(GetStringSafe(elements[index]));
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(GetStringSafe(elements[I]));
                    builder.append(", ");
                }
                builder.append(GetStringSafe(elements[bound]));
                break;
        }

        return builder.append(" }").toString();
    }

    @NotNull
    public static String PutArrayContentsToString(char[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(elements[index]);
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(elements[I]);
                    builder.append(", ");
                }
                builder.append(elements[bound]);
                break;
        }

        return builder.append(" }").toString();
    }

    @NotNull
    public static String PutArrayContentsToString(byte[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(elements[index]);
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(elements[I]);
                    builder.append(", ");
                }
                builder.append(elements[bound]);
                break;
        }

        return builder.append(" }").toString();
    }

    @NotNull
    public static String PutArrayContentsToString(short[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(elements[index]);
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(elements[I]);
                    builder.append(", ");
                }
                builder.append(elements[bound]);
                break;
        }

        return builder.append(" }").toString();
    }

    @NotNull
    public static String PutArrayContentsToString(int[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(elements[index]);
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(elements[I]);
                    builder.append(", ");
                }
                builder.append(elements[bound]);
                break;
        }

        return builder.append(" }").toString();
    }

    @NotNull
    public static String PutArrayContentsToString(long[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(elements[index]);
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(elements[I]);
                    builder.append(", ");
                }
                builder.append(elements[bound]);
                break;
        }

        return builder.append(" }").toString();
    }

    @NotNull
    public static String PutArrayContentsToString(float[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(elements[index]);
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(elements[I]);
                    builder.append(", ");
                }
                builder.append(elements[bound]);
                break;
        }

        return builder.append(" }").toString();
    }

    @NotNull
    public static String PutArrayContentsToString(double[] elements, int index, int count)
    {
        StringBuilder builder = new StringBuilder(elements.length * 15).append("{ ");

        switch (count)
        {
            case 0:
                builder.append("<EMPTY>");
                break;
            case 1:
                builder.append(elements[index]);
                break;
            default:
                int bound = index + count - 1;
                for (int I = index; I < bound; I++)
                {
                    builder.append(elements[I]);
                    builder.append(", ");
                }
                builder.append(elements[bound]);
                break;
        }

        return builder.append(" }").toString();
    }

}
