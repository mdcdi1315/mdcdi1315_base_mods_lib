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

    private static abstract class BaseSimpleArrayEnumerator<T, TArray>
            extends ArrayEnumerator<T>
    {
        protected int index;
        protected TArray[] array;

        public BaseSimpleArrayEnumerator()
        {
            super();
            index = -1;
        }

        public BaseSimpleArrayEnumerator(TArray[] array)
        {
            this();
            this.array = array;
        }

        @Override
        public abstract T getCurrent();

        @Override
        protected void ResetImpl() { index = -1; }

        @Override
        protected boolean MoveNextImpl() { return ++index < array.length; }

        @Override
        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static abstract class BaseSimpleThreadSafeArrayEnumerator<T, TArray>
        extends BaseSimpleArrayEnumerator<T, TArray>
        implements ISynchronized
    {
        @SuppressWarnings("unchecked")
        public BaseSimpleThreadSafeArrayEnumerator(TArray[] array)
        {
            super();
            Array.Copy(array, 0, this.array = (TArray[]) Array.CreateInstance(array.getClass().componentType(), array.length), 0, array.length);
        }
    }

    private static final class SimpleArrayEnumerator<T>
            extends BaseSimpleArrayEnumerator<T, T>
    {
        public SimpleArrayEnumerator(T[] array) { super(array); }

        @Override
        public T getCurrent() { return array[index]; }
    }

    private static final class CastedSimpleArrayEnumerator<T>
            extends BaseSimpleArrayEnumerator<T, Object>
    {
        public CastedSimpleArrayEnumerator(Object[] array) { super(array); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) array[index]; }
    }

    private static final class SimpleArrayEnumeratorThreadSafe<T>
            extends BaseSimpleThreadSafeArrayEnumerator<T, T>
    {
        public SimpleArrayEnumeratorThreadSafe(T[] array) { super(array); }

        @Override
        public T getCurrent() { return array[index]; }
    }

    private static final class CastedSimpleArrayEnumeratorThreadSafe<T>
        extends BaseSimpleThreadSafeArrayEnumerator<T, Object>
    {
        public CastedSimpleArrayEnumeratorThreadSafe(Object[] array) { super(array); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) array[index]; }
    }

    private static abstract class BaseBoundedArrayEnumerator<T, TArray>
        extends ArrayEnumerator<T>
    {
        protected int current;
        protected TArray[] elements;
        private final int bound, index;

        public BaseBoundedArrayEnumerator(int index, int count)
        {
            super();
            bound = (this.index = index) + count;
            current = this.index - 1;
        }

        public BaseBoundedArrayEnumerator(TArray[] elements, int index, int count)
        {
            this(index, count);
            this.elements = elements;
        }

        @Override
        public abstract T getCurrent();

        @Override
        protected void ResetImpl() { current = index - 1; }

        @Override
        protected boolean MoveNextImpl() { return ++current < bound; }

        @Override
        public void Dispose()
        {
            super.Dispose();
            elements = null;
        }
    }

    private static abstract class BaseBoundedArrayEnumeratorThreadSafe<T, TArray>
        extends BaseSimpleArrayEnumerator<T, TArray>
        implements ISynchronized
    {
        @SuppressWarnings("unchecked")
        public BaseBoundedArrayEnumeratorThreadSafe(TArray[] elements, int index, int count)
        {
            super();
            Array.Copy(elements, index, this.array = (TArray[]) Array.CreateInstance(elements.getClass().componentType(), count), 0, count);
        }
    }

    private static final class BoundedArrayEnumerator<T>
            extends BaseBoundedArrayEnumerator<T, T>
    {
        public BoundedArrayEnumerator(T[] elements, int index, int count)  { super(elements, index, count); }

        @Override
        public T getCurrent() { return elements[current]; }
    }

    private static final class CastedBoundedArrayEnumerator<T>
        extends BaseBoundedArrayEnumerator<T, Object>
    {
        public CastedBoundedArrayEnumerator(Object[] elements, int index, int count) { super(elements, index, count); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) elements[current]; }
    }

    private static final class BoundedArrayEnumeratorThreadSafe<T>
            extends BaseBoundedArrayEnumeratorThreadSafe<T, T>
    {
        public BoundedArrayEnumeratorThreadSafe(T[] array, int index, int count) { super(array, index, count); }

        @Override
        public T getCurrent() { return array[index]; }
    }

    private static final class CastedBoundedArrayEnumeratorThreadSafe<T>
        extends BaseBoundedArrayEnumeratorThreadSafe<T, Object>
    {
        public CastedBoundedArrayEnumeratorThreadSafe(Object[] elements, int index, int count) { super(elements, index, count); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) array[index]; }
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
        ArgumentNullException.ThrowIfNull(array, "array");
        return new SimpleArrayEnumerator<>(array);
    }

    /**
     * Creates a new instance of the {@link ArrayEnumerator} class from the specified array. <br />
     * All the elements of the array will be returned by the enumerator.
     * @param array The array to be enumerated.
     * @return A new {@link ArrayEnumerator} instance.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ArrayEnumerator<T> OfCasted(Object[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return new CastedSimpleArrayEnumerator<>(array);
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
        ArgumentNullException.ThrowIfNull(array, "array");
        return new SimpleArrayEnumeratorThreadSafe<>(array);
    }

    /**
     * Creates a new instance of the {@link ArrayEnumerator} class from the specified array. <br />
     * All the elements of the array will be returned by the enumerator.
     * @param array The array to be enumerated.
     * @return A new {@link ArrayEnumerator} instance.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ArrayEnumerator<T> OfCopiedAndCasted(Object[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return new CastedSimpleArrayEnumeratorThreadSafe<>(array);
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
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ArrayEnumerator<T> ByBoundsCasted(Object[] array, int index, int count)
            throws ArgumentNullException, ArgumentException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if ((index + count) > array.length) {
            throw new ArgumentException("Specified index and count parameters are out of the given array bounds.");
        } else {
            return new CastedBoundedArrayEnumerator<>(array, index, count);
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
        ArgumentNullException.ThrowIfNull(array, "array");
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
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ArrayEnumerator<T> ByBoundsCopiedAndCasted(Object[] array, int index, int count)
            throws ArgumentNullException, ArgumentException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if ((index + count) > array.length) {
            throw new ArgumentException("Specified index and count parameters are out of the given array bounds.");
        } else {
            return new CastedBoundedArrayEnumeratorThreadSafe<>(array, index, count);
        }
    }
}