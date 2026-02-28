package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

/**
 * A rather simple {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator} implementation for arrays.
 * @param <T> The type of the array elements to enumerate.
 * @see com.github.mdcdi1315.basemodslib.utils.collections.ReversedArrayEnumerator
 * @since 1.0.18
 */
public abstract class ArrayEnumerator<T>
        extends BaseEnumerator<T>
{
    private ArrayEnumerator() { super(); }

    private static final class SimpleArrayEnumerator<T>
            extends ArrayEnumerator<T>
    {
        private T[] array;
        private int index;

        public SimpleArrayEnumerator(T[] array)
        {
            super();
            this.array = array;
            index = -1;
        }

        public T getCurrent() { return array[index]; }

        protected void ResetImpl() { index = -1; }

        protected boolean MoveNextImpl() { return ++index < array.length; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static final class SimpleArrayEnumeratorThreadSafe<T>
            extends ArrayEnumerator<T>
            implements ISynchronized
    {
        private T[] array;
        private int index;

        public SimpleArrayEnumeratorThreadSafe(T[] array)
        {
            super();
            Array.Copy(array, 0, this.array = (T[]) Array.CreateInstance(array.getClass().arrayType(), array.length), 0, array.length);
            index = -1;
        }

        public T getCurrent() { return array[index]; }

        protected void ResetImpl() { index = -1; }

        protected boolean MoveNextImpl() { return ++index < array.length; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static final class BoundedArrayEnumerator<T>
            extends ArrayEnumerator<T>
    {
        private int current;
        private T[] elements;
        private final int bound, index;

        public BoundedArrayEnumerator(T[] elements, int index, int count)
        {
            super();
            this.elements = elements;
            bound = (this.index = index) + count;
            current = this.index - 1;
        }

        public T getCurrent() { return elements[current]; }

        protected void ResetImpl() { current = index - 1; }

        protected boolean MoveNextImpl() { return ++current < bound; }

        public void Dispose()
        {
            super.Dispose();
            elements = null;
        }
    }

    private static final class BoundedArrayEnumeratorThreadSafe<T>
            extends ArrayEnumerator<T>
            implements ISynchronized
    {
        private T[] array;
        private int index;

        public BoundedArrayEnumeratorThreadSafe(T[] array, int index, int count)
        {
            super();
            Array.Copy(array, index, this.array = (T[]) Array.CreateInstance(array.getClass().arrayType(), count), 0, count);
            this.index = -1;
        }

        public T getCurrent() { return array[index]; }

        protected void ResetImpl() { index = -1; }

        protected boolean MoveNextImpl() { return ++index < array.length; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    /**
     * Creates a new instance of the {@link ArrayEnumerator} class from the specified array. <br />
     * All the elements of the array will be returned by the enumerator.
     * @param array The array to be enumerated.
     * @return A new {@link ArrayEnumerator} instance.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public static <T> ArrayEnumerator<T> Of(T[] array)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array);
        return new SimpleArrayEnumerator<>(array);
    }

    /**
     * Creates a new instance of the {@link ArrayEnumerator} class from the specified array. <br />
     * All the elements of the array will be returned by the enumerator.
     * @param array The array to be enumerated.
     * @return A new {@link ArrayEnumerator} instance.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public static <T> ArrayEnumerator<T> OfCopied(T[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array);
        return new SimpleArrayEnumeratorThreadSafe<>(array);
    }

    /**
     * Creates a new instance of the array enumerator from the specified array. <br />
     * The {@code index} and {@code count} parameters indicate the portion of the array to be actually enumerated.
     * @param array The array to be enumerated.
     * @param index The index in {@code array} to start enumerating from.
     * @param count The number of items that the enumerator will return.
     * @return A new {@link ArrayEnumerator} instance.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     */
    public static <T> ArrayEnumerator<T> ByBounds(T[] array, int index, int count)
        throws ArgumentNullException, ArgumentException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array);
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if ((index + count) > array.length) {
            throw new ArgumentException("Specified index and count parameters are out of the given array bounds.");
        } else {
            return new BoundedArrayEnumerator<>(array, index, count);
        }
    }

    /**
     * Creates a new instance of the array enumerator from the specified array. <br />
     * The {@code index} and {@code count} parameters indicate the portion of the array to be actually enumerated.
     * @param array The array to be enumerated.
     * @param index The index in {@code array} to start enumerating from.
     * @param count The number of items that the enumerator will return.
     * @return A new {@link ArrayEnumerator} instance.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     */
    public static <T> ArrayEnumerator<T> ByBoundsCopied(T[] array, int index, int count)
            throws ArgumentNullException, ArgumentException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array);
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if ((index + count) > array.length) {
            throw new ArgumentException("Specified index and count parameters are out of the given array bounds.");
        } else {
            return new BoundedArrayEnumeratorThreadSafe<>(array, index, count);
        }
    }
}