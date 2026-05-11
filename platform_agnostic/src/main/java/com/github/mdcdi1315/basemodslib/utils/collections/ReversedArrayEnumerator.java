package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

/**
 * Provides an enumerator implementation for returning array elements in the reverse order <br />
 * of how they are saved in without needing to perform any copies of the underlying array. <br />
 * If is needed, however, the arrays to be copied to avoid thread-safety issues, it is also possible. <br /> <br />
 *
 * This class cannot be inherited, albeit the fact that is abstract.
 * @param <T> The type of the elements to reverse.
 * @since 1.0.18
 */
public abstract class ReversedArrayEnumerator<T>
        extends BaseEnumerator<T>
{
    private ReversedArrayEnumerator() { super(); }

    @SuppressWarnings("unchecked")
    private static <T> T[] CreateCopy(T[] array)
    {
        T[] copy = (T[]) Array.CreateInstance(array.getClass().componentType(), array.length);
        System.arraycopy(array, 0, copy, 0, array.length);
        return copy;
    }

    private static abstract class BaseSimpleReversedArrayEnumerator<T, TArray>
            extends ReversedArrayEnumerator<T>
    {
        protected int index;
        protected TArray[] array;

        public BaseSimpleReversedArrayEnumerator(TArray[] array)
        {
            super();
            index = (this.array = array).length;
        }

        @Override
        public abstract T getCurrent();

        protected boolean MoveNextImpl() { return --index > -1; }

        protected void ResetImpl() { index = array.length; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static abstract class BaseSimpleThreadSafeReversedArrayEnumerator<T, TArray>
        extends BaseSimpleReversedArrayEnumerator<T, TArray>
        implements ISynchronized
    {
        public BaseSimpleThreadSafeReversedArrayEnumerator(TArray[] array) { super(CreateCopy(array)); }
    }

    private static final class SimpleReversedArrayEnumerator<T>
            extends BaseSimpleReversedArrayEnumerator<T, T>
    {
        public SimpleReversedArrayEnumerator(T[] array)  { super(array); }

        @Override
        public T getCurrent() { return array[index]; }
    }

    private static final class CastedSimpleArrayReversedEnumerator<T>
        extends BaseSimpleReversedArrayEnumerator<T, Object>
    {
        public CastedSimpleArrayReversedEnumerator(Object[] array) { super(array); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) array[index]; }
    }

    private static final class SimpleReversedArrayEnumeratorThreadSafe<T>
            extends BaseSimpleThreadSafeReversedArrayEnumerator<T, T>
    {
        public SimpleReversedArrayEnumeratorThreadSafe(T[] array) { super(array); }

        @Override
        public T getCurrent() { return array[index]; }
    }

    private static final class CastedSimpleReversedArrayEnumeratorThreadSafe<T>
        extends BaseSimpleThreadSafeReversedArrayEnumerator<T, Object>
    {
        public CastedSimpleReversedArrayEnumeratorThreadSafe(Object[] array) { super(array); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) array[index]; }
    }

    private static abstract class BaseBoundedReversedArrayEnumerator<T, TArray>
        extends ReversedArrayEnumerator<T>
    {
        protected TArray[] array;
        protected int current_index;
        private final int index, count;

        public BaseBoundedReversedArrayEnumerator(int index, int count)
        {
            super();
            current_index = (this.index = index) + (this.count = count);
        }

        public BaseBoundedReversedArrayEnumerator(TArray[] array, int index, int count)
        {
            this(index, count);
            this.array = array;
        }

        @Override
        public abstract T getCurrent();

        @Override
        protected boolean MoveNextImpl() { return --current_index >= index; }

        @Override
        protected void ResetImpl() { current_index = index + count; }

        @Override
        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static abstract class BaseBoundedThreadSafeReversedArrayEnumerator<T, TArray>
        extends BaseSimpleReversedArrayEnumerator<T, TArray>
        implements ISynchronized
    {
        @SuppressWarnings("unchecked")
        private static <T> T[] CreateCopy_BThreadSafe(T[] array, int index, int count)
        {
            T[] copy = (T[]) Array.CreateInstance(array.getClass().componentType(), count);
            System.arraycopy(array, index, copy, 0, count);
            return copy;
        }

        public BaseBoundedThreadSafeReversedArrayEnumerator(TArray[] array, int index, int count) { super(CreateCopy_BThreadSafe(array, index, count)); }
    }

    private static final class BoundedReversedArrayEnumerator<T>
            extends BaseBoundedReversedArrayEnumerator<T, T>
    {
        public BoundedReversedArrayEnumerator(T[] array, int index, int count) { super(array, index, count); }

        @Override
        public T getCurrent() { return array[current_index]; }
    }

    private static final class CastedBoundedReversedArrayEnumerator<T>
        extends BaseBoundedReversedArrayEnumerator<T, Object>
    {
        public CastedBoundedReversedArrayEnumerator(Object[] array, int index, int count) { super(array, index, count); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) array[current_index]; }
    }

    private static final class BoundedReversedArrayEnumeratorThreadSafe<T>
            extends BaseBoundedThreadSafeReversedArrayEnumerator<T, T>
    {
        public BoundedReversedArrayEnumeratorThreadSafe(T[] array, int index, int count) { super(array, index, count); }

        @Override
        public T getCurrent() { return array[index]; }
    }

    private static final class CastedBoundedReversedArrayEnumeratorThreadSafe<T>
        extends BaseBoundedThreadSafeReversedArrayEnumerator<T, Object>
    {
        public CastedBoundedReversedArrayEnumeratorThreadSafe(Object[] array, int index, int count) { super(array, index, count); }

        @Override
        @SuppressWarnings("unchecked")
        public T getCurrent() { return (T) array[index]; }
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed.
     * @param array The array to create a reversed array enumerator from.
     * @return An enumerator implementation that will return the elements of {@code array} in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public static <T> ReversedArrayEnumerator<T> Of(T[] array)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return new SimpleReversedArrayEnumerator<>(array);
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed.
     * @param array The array to create a reversed array enumerator from.
     * @return An enumerator implementation that will return the elements of {@code array} in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ReversedArrayEnumerator<T> OfCasted(Object[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return new CastedSimpleArrayReversedEnumerator<>(array);
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed.
     * @param array The array to create a reversed array enumerator from.
     * @return An enumerator implementation that will return the elements of {@code array} in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public static <T> ReversedArrayEnumerator<T> OfCopied(T[] array)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return new SimpleReversedArrayEnumeratorThreadSafe<>(array);
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed.
     * @param array The array to create a reversed array enumerator from.
     * @return An enumerator implementation that will return the elements of {@code array} in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ReversedArrayEnumerator<T> OfCastedAndCopied(Object[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return new CastedSimpleReversedArrayEnumeratorThreadSafe<>(array);
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed. <br />
     * The {@code index} and {@code count} parameters indicate the portion of the array to be actually enumerated.
     * @param array The array to create a reversed array enumerator from.
     * @param index The index where to start reversing elements.
     * @param count The number of elements to select and reverse.
     * @return An enumerator implementation that will return the elements of {@code array} by the specified bounds in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     */
    public static <T> ReversedArrayEnumerator<T> ByBounds(T[] array, int index, int count)
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
            return new BoundedReversedArrayEnumerator<>(array, index, count);
        }
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed. <br />
     * The {@code index} and {@code count} parameters indicate the portion of the array to be actually enumerated.
     * @param array The array to create a reversed array enumerator from.
     * @param index The index where to start reversing elements.
     * @param count The number of elements to select and reverse.
     * @return An enumerator implementation that will return the elements of {@code array} by the specified bounds in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ReversedArrayEnumerator<T> ByBoundsCasted(Object[] array, int index, int count)
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
            return new CastedBoundedReversedArrayEnumerator<>(array, index, count);
        }
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed. <br />
     * The {@code index} and {@code count} parameters indicate the portion of the array to be actually enumerated.
     * @param array The array to create a reversed array enumerator from.
     * @param index The index where to start reversing elements.
     * @param count The number of elements to select and reverse.
     * @return An enumerator implementation that will return the elements of {@code array} by the specified bounds in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     */
    public static <T> ReversedArrayEnumerator<T> ByBoundsCopied(T[] array, int index, int count)
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
            return new BoundedReversedArrayEnumeratorThreadSafe<>(array, index, count);
        }
    }

    /**
     * Creates a new reversed array enumerator that will return all the elements in the {@code array} parameter reversed. <br />
     * The {@code index} and {@code count} parameters indicate the portion of the array to be actually enumerated.
     * @param array The array to create a reversed array enumerator from.
     * @param index The index where to start reversing elements.
     * @param count The number of elements to select and reverse.
     * @return An enumerator implementation that will return the elements of {@code array} by the specified bounds in reverse manner.
     * @param <T> The type of the array elements to be enumerated.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @apiNote This API is used when dealing with Object arrays on collection types implementations.
     * @since 1.0.31
     */
    public static <T> ReversedArrayEnumerator<T> ByBoundsCopiedAndCasted(Object[] array, int index, int count)
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
            return new CastedBoundedReversedArrayEnumeratorThreadSafe<>(array, index, count);
        }
    }
}