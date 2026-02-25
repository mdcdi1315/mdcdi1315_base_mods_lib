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

    private static final class SimpleReversedArrayEnumerator<T>
            extends ReversedArrayEnumerator<T>
    {
        private T[] array;
        private int index;

        public SimpleReversedArrayEnumerator(T[] array)
        {
            super();
            index = (this.array = array).length;
        }

        @Override
        public T getCurrent() { return array[index]; }

        protected boolean MoveNextImpl() { return --index > -1; }

        protected void ResetImpl() { index = array.length; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static final class SimpleReversedArrayEnumeratorThreadSafe<T>
            extends ReversedArrayEnumerator<T>
            implements ISynchronized
    {
        private T[] array;
        private int index;

        public SimpleReversedArrayEnumeratorThreadSafe(T[] array)
        {
            super();
            this.array = (T[]) Array.CreateInstance(array.getClass().arrayType(), array.length);
            Array.Copy(array , this.array, array.length);
            index = this.array.length;
        }

        @Override
        public T getCurrent() { return array[index]; }

        protected boolean MoveNextImpl() { return --index > -1; }

        protected void ResetImpl() { index = array.length; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static final class BoundedReversedArrayEnumerator<T>
            extends ReversedArrayEnumerator<T>
    {
        private T[] array;
        private int current_index;
        private final int index, count;

        public BoundedReversedArrayEnumerator(T[] array, int index, int count)
        {
            super();
            this.array = array;
            current_index = (this.index = index) + (this.count = count);
        }

        @Override
        public T getCurrent() { return array[current_index]; }

        protected boolean MoveNextImpl() { return --current_index >= index; }

        protected void ResetImpl() { current_index = index + count; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
    }

    private static final class BoundedReversedArrayEnumeratorThreadSafe<T>
            extends ReversedArrayEnumerator<T>
            implements ISynchronized
    {
        private T[] array;
        private int index;

        public BoundedReversedArrayEnumeratorThreadSafe(T[] array, int index, int count)
        {
            super();
            Array.Copy(array, index, this.array = (T[])Array.CreateInstance(array.getClass().arrayType(), count), 0, count);
            this.index = this.array.length;
        }

        @Override
        public T getCurrent() { return array[index]; }

        protected boolean MoveNextImpl() { return --index > -1; }

        protected void ResetImpl() { index = array.length; }

        public void Dispose()
        {
            super.Dispose();
            array = null;
        }
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
        ArgumentNullException.ThrowIfNull(array);
        return new SimpleReversedArrayEnumerator<>(array);
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
        ArgumentNullException.ThrowIfNull(array);
        return new SimpleReversedArrayEnumeratorThreadSafe<>(array);
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
        ArgumentNullException.ThrowIfNull(array);
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
     */
    public static <T> ReversedArrayEnumerator<T> ByBoundsCopied(T[] array, int index, int count)
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
            return new BoundedReversedArrayEnumeratorThreadSafe<>(array, index, count);
        }
    }
}