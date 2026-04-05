package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.RequiresDynamicCode;

import java.util.Arrays;
import java.util.Objects;
import java.lang.reflect.Type;

/**
 * Provides methods for creating, manipulating, searching, and sorting arrays, thereby serving as the base class for all arrays in the common language runtime.
 */
public final class Array
{
    private record TYPEDESCRIPTOR<T>()
    {
        @SuppressWarnings("unchecked")
        public Class<T> DescribeTypeT()
        {
            try {
                Type gentype = getClass().getTypeParameters()[0].getBounds()[0];
                return (Class<T>) Class.forName(gentype.getTypeName());
            } catch (ClassNotFoundException ignored) {}
            return null;
        }
    }

    /**
     * Gets the maximum number of elements that may be contained in an array.
     */
    public static final int MaxLength = 0x7FFFFFFF;

    /**
     * Sets a range of elements in an array to the default value of each element type.
     * @param array The array whose elements need to be cleared.
     * @param index The starting index of the range of elements to clear.
     * @param count The number of elements to clear.
     * @param <T> The array's underlying type.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public static <T> void Clear(T[] array , int index , int count)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array , "array");
        var ct = index + count;
        for (int I = index; I < ct; I++)
        {
            array[I] = null;
        }
    }

    public static <T> void Copy(T[] source , T[] dest , int count)
    {
        ArgumentNullException.ThrowIfNull(source , "source");
        try {
            if (count >= 0) {
                System.arraycopy(source, 0, dest, 0, count);
            }
        } catch (IndexOutOfBoundsException b) {
            throw new IndexOutOfRangeException(b.getMessage());
        } catch (ArrayStoreException ase) {
            throw new ArrayTypeMismatchException(ase.getMessage());
        }
    }

    public static <T> void Copy(T[] sourceArray, int sourceIndex, T[] destinationArray, int destinationIndex, int length)
    {
        ArgumentNullException.ThrowIfNull(sourceArray , "sourceArray");
        ArgumentNullException.ThrowIfNull(destinationArray , "destinationArray");

        try {
            if (length > 0) {
                System.arraycopy(sourceArray, sourceIndex, destinationArray, destinationIndex, length);
            }
        } catch (IndexOutOfBoundsException b) {
            throw new IndexOutOfRangeException(b.getMessage());
        } catch (ArrayStoreException ase) {
            throw new ArrayTypeMismatchException(ase.getMessage());
        }
    }

    @RequiresDynamicCode(Message = "The code for an array of the specified type might not be available.")
    public static Object CreateInstance(Class<?> elementType , int length)
    {
        try {
            return java.lang.reflect.Array.newInstance(elementType, length);
        } catch (NullPointerException npe) {
            throw new ArgumentNullException("elementType");
        } catch (NegativeArraySizeException nase) {
            throw new ArgumentOutOfRangeException("length" , "length cannot be a negative number.");
        } catch (IllegalArgumentException iae) {
            throw new NotSupportedException(String.format("Type not supported: %s" , elementType.getName()));
        }
    }

    @RequiresDynamicCode(Message = "The code for an array of the specified type might not be available.")
    public static Object CreateInstance(Class<?> elementType, int... lengths)
    {
        try {
            return java.lang.reflect.Array.newInstance(elementType, lengths);
        } catch (NullPointerException npe) {
            throw new ArgumentNullException("elementType");
        } catch (NegativeArraySizeException nase) {
            throw new ArgumentOutOfRangeException("lengths" , "An element in the dimension array was negative.");
        } catch (IllegalArgumentException iae) {
            throw new NotSupportedException(String.format("Type not supported: %s" , elementType.getName()));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] CreateInstanceFast(Class<?> elementType, int length)
    {
        try {
            return (T[]) java.lang.reflect.Array.newInstance(elementType, length);
        } catch (NullPointerException npe) {
            throw new ArgumentNullException("elementType");
        } catch (NegativeArraySizeException nase) {
            throw new ArgumentOutOfRangeException("lengths" , "An element in the dimension array was negative.");
        } catch (IllegalArgumentException iae) {
            throw new NotSupportedException(String.format("Type not supported: %s" , elementType.getName()));
        }
    }

    public static <T> void Reverse(T[] array)
    {
        ArgumentNullException.ThrowIfNull(array , "array");
        if (array.length > 1)
        {
            // SpanHelpers.Reverse(ref MemoryMarshal.GetArrayDataReference(array), (nuint)array.Length);
            InternalReverse(array , 0 , array.length);
        }
    }

    public static <T> void Reverse(T[] array, int index, int length)
    {
        ArgumentNullException.ThrowIfNull(array , "array");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index" , "Must not be negative.");
            // ThrowHelper.ThrowLengthArgumentOutOfRange_ArgumentOutOfRange_NeedNonNegNum();
        } else if (length < 0) {
            throw new ArgumentOutOfRangeException("length" , "Must not be negative.");
            // ThrowHelper.ThrowLengthArgumentOutOfRange_ArgumentOutOfRange_NeedNonNegNum();
        } else if (array.length - index < length) {
            throw new ArgumentException("The given offset and length were outside the array bounds.");
            //ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);
        } else if (length > 1) {
            InternalReverse(array , index , length);
            //SpanHelpers.Reverse(ref Unsafe.Add(ref MemoryMarshal.GetArrayDataReference(array), index), (nuint)length);
        }
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate,
     * and returns the zero-based index of the first occurrence within the range of elements
     * in the {@link Array} that starts at the specified index and contains the specified number of elements.
     * @param array The one-dimensional, zero-based {@link java.lang.reflect.Array} to search.
     * @param startIndex The zero-based starting index of the search.
     * @param count The number of elements in the section to search.
     * @param match The {@link Predicate} that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for {@code array}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than zero. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code startIndex} and {@code count} do not specify a valid section in {@code array}.
     */
    public static <T> int FindIndex(T[] array, int startIndex, int count, Predicate<T> match)
        throws ArgumentNullException , ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(match , "match");

        if (startIndex < 0 || startIndex > array.length)
        {
            throw new ArgumentOutOfRangeException("startIndex" , "Index must be less or equal than the array length and be non-negative.");
           // ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLessOrEqual();
        }

        if (count < 0 || startIndex > array.length - count)
        {
            throw new ArgumentException("Count must not be negative and be less than the array bounds." , "count");
           // ThrowHelper.ThrowCountArgumentOutOfRange_ArgumentOutOfRange_Count();
        }

        int endIndex = startIndex + count;
        for (int i = startIndex; i < endIndex; i++)
        {
            if (match.predicate(array[i])) { return i; }
        }
        return -1;
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate,
     * and returns the zero-based index of the first occurrence within the entire {@link Array}.
     * @param array The one-dimensional, zero-based {@link java.lang.reflect.Array} to search.
     * @param match The {@link Predicate} that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     */
    public static <T> int FindIndex(T[] array, Predicate<T> match)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array , "array");

        return FindIndex(array, 0, array.length, match);
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate,
     * and returns the zero-based index of the first occurrence within the range of elements
     * in the {@link Array} that extends from the specified index to the last element.
     * @param array The one-dimensional, zero-based {@link java.lang.reflect.Array} to search.
     * @param startIndex The zero-based starting index of the search.
     * @param match The {@link Predicate} that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is less than zero or greater than the length of the {@code array}.
     */
    public static <T> int FindIndex(T[] array, int startIndex, Predicate<T> match)
        throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array , "array");

        return FindIndex(array, startIndex, array.length - startIndex, match);
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate,
     * and returns the zero-based index of the last occurrence within the range of elements
     * in the {@link Array} that contains the specified number of elements and ends at the specified index.
     * @param array The one-dimensional, zero-based {@link java.lang.reflect.Array} to search.
     * @param startIndex The zero-based starting index of the backward search.
     * @param count The number of elements in the section to search.
     * @param match The {@link Predicate} that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for {@code array}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than zero. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code startIndex} and {@code count} do not specify a valid section in {@code array}.
     */
    public static <T> int FindLastIndex(T[] array, int startIndex, int count, Predicate<T> match)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(match , "match");

        if (startIndex < 0 || startIndex > array.length)
        {
            throw new ArgumentOutOfRangeException("startIndex" , "Index must be less or equal than the array length and be non-negative.");
            // ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLessOrEqual();
        }

        if (count < 0 || startIndex > array.length - count)
        {
            throw new ArgumentException("Count must not be negative and be less than the array bounds." , "count");
            // ThrowHelper.ThrowCountArgumentOutOfRange_ArgumentOutOfRange_Count();
        }

        int endIndex = startIndex - count;
        for (int i = startIndex; i > endIndex; i--)
        {
            if (match.predicate(array[i])) { return i; }
        }
        return -1;
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate,
     * and returns the zero-based index of the last occurrence within the range of elements
     * in the {@link Array} that extends from the first element to the specified index.
     * @param array The one-dimensional, zero-based {@link java.lang.reflect.Array} to search.
     * @param startIndex The zero-based starting index of the backward search.
     * @param match The {@link Predicate} that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for {@code array}.
     */
    public static <T> int FindLastIndex(T[] array, int startIndex, Predicate<T> match)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        return FindLastIndex(array, startIndex, startIndex + 1, match);
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate,
     * and returns the zero-based index of the last occurrence within the entire {@link Array}.
     * @param array The one-dimensional, zero-based {@link java.lang.reflect.Array} to search.
     * @param match The {@link Predicate} that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     */
    public static <T> int FindLastIndex(T[] array, Predicate<T> match)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        return FindLastIndex(array, array.length - 1, array.length, match);
    }

    /**
     * Retrieves all the elements that match the conditions defined by the specified predicate.
     * @param array The one-dimensional, zero-based {@link Array} to search.
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return An {@link Array} containing all the elements that match the conditions defined by the specified predicate, if found; otherwise, an empty {@link Array}.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     */
    public static <T> T[] FindAll(T[] array, Predicate<T> match)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array , "array");
        ArgumentNullException.ThrowIfNull(match , "match");

        List<T> list = new List<>();
        for (T t : array) {
            if (match.predicate(t)) {
                list.Add(t);
            }
        }
        T[] result = CreateInstanceFast(array.getClass().componentType() , list.getCount());
        list.CopyTo(result, 0);
        return result;
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the first occurrence within the entire {@link Array}.
     * @param array The one-dimensional, zero-based array to search.
     * @param match The predicate that defines the conditions of the element to search for.
     * @return The first element that matches the conditions defined by the specified predicate, if found; otherwise, the default value for type {@link T}.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     */
    public static <T> @MaybeNull T Find(T[] array, Predicate<T> match)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array , "array");
        ArgumentNullException.ThrowIfNull(match , "match");

        for (T t : array) {
            if (match.predicate(t)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the last occurrence within the entire {@link Array}.
     * @param array The one-dimensional, zero-based array to search.
     * @param match The predicate that defines the conditions of the element to search for.
     * @return The last element that matches the conditions defined by the specified predicate, if found; otherwise, the default value for type {@link T}.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     */
    public static <T> @MaybeNull T FindLast(T[] array, Predicate<T> match)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(match, "match");

        T item;
        for (int I = array.length - 1; I > -1; I--)
        {
            item = array[I];
            if (match.predicate(item)) { return item; }
        }
        return null;
    }

    /**
     * Determines whether the specified array contains elements that match the conditions defined by the specified predicate.
     * @param array The one-dimensional, zero-based {@link Array} to search.
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return {@code true} if {@code array} contains one or more elements that match the conditions defined by the specified predicate; otherwise, {@code false}.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code match} are {@code null}.
     */
    public static <T> boolean Exists(T[] array, Predicate<T> match) throws ArgumentNullException { return FindIndex(array, match) > -1; }

    private record IndexOf_MethodWrapper<T>(T item)
        implements Predicate<T>
    {
        @Override
        public boolean test(T t) { return Objects.equals(t, item); }

        @Override
        public boolean predicate(T obj) { return Objects.equals(obj, item); }
    }

    /**
     * Searches for the specified object and returns the index of its first occurrence in a one-dimensional array.
     * @param array The one-dimensional, zero-based array to search.
     * @param value The object to locate in {@code array}.
     * @return The zero-based index of the first occurrence of {@code value} in the entire {@code array}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public static <T> int IndexOf(T[] array, @AllowNull T value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        return IndexOf(array, value, 0, array.length);
    }

    /**
     * Searches for the specified object in a range of elements of a one dimensional array,
     * and returns the index of its first occurrence.
     * The range extends from a specified index to the end of the array.
     * @param array The one-dimensional, zero-based array to search.
     * @param value The object to locate in {@code array}.
     * @param startIndex The zero-based starting index of the search. 0 (zero) is valid in an empty array.
     * @return The zero-based index of the first occurrence of {@code value} within the range of elements in {@code array} that extends from {@code startIndex} to the last element, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for {@code array}.
     */
    public static <T> int IndexOf(T[] array, @AllowNull T value, int startIndex)
        throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return IndexOf(array, value, startIndex, array.length - startIndex);
    }

    /**
     * Searches for the specified object in a range of elements of a one-dimensional array,
     * and returns the index of its first occurrence.
     * The range extends from a specified index for a specified number of elements.
     * @param array The one-dimensional, zero-based array to search.
     * @param item The object to locate in {@code array}.
     * @param startIndex The zero-based starting index of the search. 0 (zero) is valid in an empty array.
     * @param count The number of elements in the section to search.
     * @return The zero-based index of the first occurrence of {@code value} within the range of elements in {@code array} that starts at {@code startIndex} and contains the number of elements specified in {@code count}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for {@code array}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than zero. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code startIndex} and count do not specify a valid section in {@code array}. <br /> <br />
     */
    public static <T> int IndexOf(T[] array, @AllowNull T item, int startIndex, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        return FindIndex(array, startIndex, count, new IndexOf_MethodWrapper<>(item));
    }

    /**
     * Searches for the specified object and returns the index of the last occurrence within the entire {@link Array}.
     * @param array The one-dimensional, zero-based {@link Array} to search.
     * @param value The object to locate in {@code array}.
     * @return The zero-based index of the last occurrence of {@code value} within the entire {@code array}, if found; otherwise, -1.
     * @param <T> {@code array} is {@code null}.
     */
    public static <T> int LastIndexOf(T[] array, @AllowNull T value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        return LastIndexOf(array, value, array.length - 1, array.length);
    }

    /**
     * Searches for the specified object and returns the index of the last occurrence within the range of elements in the {@link Array} that extends from the first element to the specified index.
     * @param array The one-dimensional, zero-based array to search.
     * @param value The object to locate in {@code array}.
     * @param startIndex The zero-based starting index of the backward search.
     * @return The zero-based index of the last occurrence of {@code value} within the range of elements in {@code array} that extends from the first element to {@code startIndex}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for {@code array}.
     */
    public static <T> int LastIndexOf(T[] array, @AllowNull T value, int startIndex)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        // if array is empty and startIndex is 0, we need to pass 0 as count
        return LastIndexOf(array, value, startIndex, (array.length == 0) ? 0 : (startIndex + 1));
    }

    /**
     * Searches for the specified object and returns the index of the last occurrence within the range of elements
     * in the {@link Array} that contains the specified number of elements and ends at the specified index.
     * @param array The one-dimensional, zero-based array to search.
     * @param item The object to locate in {@code array}.
     * @param startIndex The zero-based starting index of the backward search.
     * @param count The number of elements in the section to search.
     * @return The zero-based index of the last occurrence of {@code value} within the range of elements in {@code array} that contains the number of elements specified in {@code count} and ends at {@code startIndex}, if found; otherwise, -1.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for {@code array}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than zero. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code startIndex} and count do not specify a valid section in {@code array}. <br /> <br />
     */
    public static <T> int LastIndexOf(T[] array, @AllowNull T item, int startIndex, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        return FindLastIndex(array, startIndex, count, new IndexOf_MethodWrapper<>(item));
    }

    private static <T> void InternalReverse(T[] array , int index , int length)
    {
        int firstindex = index;
        int lastindex = length - 1;
        do {
            T temp = array[firstindex];
            array[firstindex] = array[lastindex];
            array[lastindex] = temp;
            firstindex++;
            lastindex--;
        } while (firstindex < lastindex);
    }

    /**
     * Assigns the given {@code value} of type {@link T} to each element of the specified {@code array}.
     * @param array The array to be filled.
     * @param value The value to assign to each array element.
     * @param <T> The type of the elements in the array.
     */
    public static <T> void Fill(T[] array, @AllowNull T value)
    {
        // .NET's method does not also seem to throw ArgumentNullException, so we will follow Java behavior as their behavior seems to be identical.
        Arrays.fill(array, value);
    }

    /**
     * Assigns the given {@code value} of type {@link T} to the elements of the specified {@code array} that are within the range of {@code startIndex} (inclusive) and the next {@code count} number of indices.
     * @param array The array to be filled.
     * @param value The new value for the elements in the specified range.
     * @param startIndex A 32-bit integer that represents the index in {@code array} at which filling begins.
     * @param count The number of elements to copy.
     * @param <T> The type of the elements of the array.
     */
    public static <T> void Fill(T[] array, @AllowNull T value, int startIndex, int count)
    {
        int ctf = startIndex + count;
        for (int I = startIndex; I < ctf; I++) {
            array[I] = value;
        }
    }

    /**
     * Performs the specified action on each element of the specified array.
     * @param array The one-dimensional, zero-based {@link java.lang.reflect.Array} on whose elements the action is to be performed.
     * @param action The {@link Action1} to perform on each element of array.
     * @param <T> The type of the elements of the array.
     * @throws ArgumentNullException {@code array} and/or {@code action} are {@code null}.
     */
    public static <T> void ForEach(T[] array, Action1<T> action)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(action, "action");
        for (T element : array)
        {
            action.action(element);
        }
    }

    @Deprecated(forRemoval = true) // This will possibly not work properly.
    public static <T , TD> TD[] ConvertAll(T[] array , Converter<T , TD> converter)
    {
        ArgumentNullException.ThrowIfNull(array , "array");
        ArgumentNullException.ThrowIfNull(converter, "converter");
        TD[] dest = CreateInstanceFast(new TYPEDESCRIPTOR<TD>().DescribeTypeT() , array.length);
        for (int I = 0; I < dest.length; I++)
        {
            dest[I] = converter.convert(array[I]);
        }
        return dest;
    }

}
