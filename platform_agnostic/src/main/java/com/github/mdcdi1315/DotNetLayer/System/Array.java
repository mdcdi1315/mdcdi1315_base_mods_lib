package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.RequiresDynamicCode;

import java.lang.reflect.Type;
import java.util.Arrays;

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

    public static final int MaxLength = 0x7FFFFFFF;

    public static <T> void Clear(T[] array , int index , int count)
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
    private static <T> T[] CreateInstanceFast(Class<T> elementType, int length)
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
        if (index < 0)
        {
            throw new ArgumentOutOfRangeException("index" , "Must not be negative.");
            // ThrowHelper.ThrowLengthArgumentOutOfRange_ArgumentOutOfRange_NeedNonNegNum();
        }
        if (length < 0)
        {
            throw new ArgumentOutOfRangeException("length" , "Must not be negative.");
            // ThrowHelper.ThrowLengthArgumentOutOfRange_ArgumentOutOfRange_NeedNonNegNum();
        }

        if (array.length - index < length)
            throw new ArgumentException("The given offset and length were outside the array bounds.");
            //ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);

        if (length <= 1)
            return;

        InternalReverse(array , index , length);
        //SpanHelpers.Reverse(ref Unsafe.Add(ref MemoryMarshal.GetArrayDataReference(array), index), (nuint)length);
    }

    public static <T> int FindIndex(T[] array, int startIndex, int count, Predicate<T> match)
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
            if (match.predicate(array[i]))
                return i;
        }
        return -1;
    }

    public static <T> int FindIndex(T[] array, Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(array , "array");

        return FindIndex(array, 0, array.length, match);
    }

    public static <T> int FindIndex(T[] array, int startIndex, Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(array , "array");

        return FindIndex(array, startIndex, array.length - startIndex, match);
    }

    public static <T> T[] FindAll(T[] array, Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(array , "array");
        ArgumentNullException.ThrowIfNull(match , "match");

        List<T> list = new List<>();
        for (T t : array) {
            if (match.predicate(t)) {
                list.Add(t);
            }
        }
        return list.ToArray();
    }

    public static <T> @MaybeNull T Find(T[] array, Predicate<T> match)
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

    public static <T> boolean Exists(T[] array, Predicate<T> match)
    {
        return FindIndex(array, match) != -1;
    }

    public static <T> int IndexOf(T[] array , T item , int startIndex , int count)
    {
        return FindIndex(array , startIndex , count , (T cmp) -> {
            if (cmp == null) { return false; }
            return cmp.equals(item);
        });
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

    public static <T> void Fill(T[] array, @MaybeNull T value)
    {
        // .NET's method does not also seem to throw ArgumentNullException, so we will follow Java behavior as their behavior seems to be identical.
        Arrays.fill(array, value);
    }

    public static <T> void Fill(T[] array, @MaybeNull T value, int startIndex, int count)
    {
        int ctf = startIndex + count;
        for (int I = startIndex; I < ctf; I++) {
            array[I] = value;
        }
    }

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
