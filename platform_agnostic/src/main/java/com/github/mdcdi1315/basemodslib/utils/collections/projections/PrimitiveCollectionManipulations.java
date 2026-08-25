package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.ArithmeticException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections.*;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

/**
 * Complements the {@link com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations}
 * class by providing additional static helper methods that are specific to primitive enumerable instances only.
 */
public final class PrimitiveCollectionManipulations
{
    private PrimitiveCollectionManipulations() {}

    /**
     * Computes the sum of all the enumerable's elements.
     * @param input The elements to deduce their sum.
     * @return The sum of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    public static int Sum(IIntEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        int sum = 0;
        try (IIntEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
            }
        }
        return sum;
    }

    /**
     * Computes the sum of all the enumerable's elements.
     * @param input The elements to deduce their sum.
     * @return The sum of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    public static long Sum(ILongEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        long sum = 0L;
        try (ILongEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
            }
        }
        return sum;
    }

    /**
     * Computes the sum of all the enumerable's elements.
     * @param input The elements to deduce their sum.
     * @return The sum of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    public static double Sum(IDoubleEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        double sum = 0d;
        try (IDoubleEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
            }
        }
        return sum;
    }

    /**
     * Computes the sum of all the enumerable's elements.
     * @param input The elements to deduce their sum.
     * @return The sum of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    public static float Sum(IFloatEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        float sum = 0f;
        try (IFloatEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
            }
        }
        return sum;
    }

    // Sum not meaningful for short and byte primitive types - upcast them to IIntEnumerable type and then compute it.

    /**
     * Computes the average value of all the elements of {@code input}.
     * @param input The elements to compute the average value from.
     * @return The average value of all the elements of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArithmeticException No elements were defined, and a division by zero was attempted.
     */
    public static int Average(IIntEnumerable input)
            throws ArgumentNullException, ArithmeticException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        int sum = 0;
        int count = 0;
        try (IIntEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
                count++;
            }
        }
        return sum / count;
    }

    /**
     * Computes the average value of all the elements of {@code input}.
     * @param input The elements to compute the average value from.
     * @return The average value of all the elements of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArithmeticException No elements were defined, and a division by zero was attempted.
     */
    public static long Average(ILongEnumerable input)
            throws ArgumentNullException, ArithmeticException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        long sum = 0L;
        long count = 0L;
        try (ILongEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
                count++;
            }
        }
        return sum / count;
    }

    /**
     * Computes the average value of all the elements of {@code input}.
     * @param input The elements to compute the average value from.
     * @return The average value of all the elements of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    public static float Average(IFloatEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        float sum = 0f;
        long count = 0L;
        try (IFloatEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
                count++;
            }
        }
        return sum / count;
    }

    /**
     * Computes the average value of all the elements of {@code input}.
     * @param input The elements to compute the average value from.
     * @return The average value of all the elements of {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    public static double Average(IDoubleEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        double sum = 0d;
        long count = 0L;
        try (IDoubleEnumerator en = input.GetEnumerator())
        {
            while (en.MoveNext())
            {
                sum += en.getUncastedCurrent();
                count++;
            }
        }
        return sum / count;
    }

    /**
     * Provides an {@link IFloatEnumerable} instance
     * that returns a sequence of floating-point elements
     * starting from the value of {@code min} and ending
     * up to the value of {@code max}. Each iteration
     * increments the next element by the value of {@code step}.
     * @param min The minimum, inclusive bound of the range.
     * @param max The maximum, inclusive bound of the range.
     * @param step The value that each next element from {@code min} will be incremented by.
     * @return A new instance of {@link IFloatEnumerable} returning the range of elements.
     * @throws ArgumentException {@code min} is greater than {@code max}.
     * @since 1.0.37
     */
    @NotNull
    public static IFloatEnumerable FloatRange(float min, float max, float step)
            throws ArgumentException
    {
        if (min > max) {
            throw new ArgumentException("Minimum bound cannot be greater than maximum bound");
        } else {
            return new FloatRangeEnumerable(min, max, step);
        }
    }

    /**
     * Provides an {@link IDoubleEnumerable} instance
     * that returns a sequence of floating-point elements
     * starting from the value of {@code min} and ending
     * up to the value of {@code max}. Each iteration
     * increments the next element by the value of {@code step}.
     * @param min The minimum, inclusive bound of the range.
     * @param max The maximum, inclusive bound of the range.
     * @param step The value that each next element from {@code min} will be incremented by.
     * @return A new instance of {@link IDoubleEnumerable} returning the range of elements.
     * @throws ArgumentException {@code min} is greater than {@code max}.
     * @since 1.0.37
     */
    @NotNull
    public static IDoubleEnumerable DoubleRange(double min, double max, double step)
            throws ArgumentException
    {
        if (min > max) {
            throw new ArgumentException("Minimum bound cannot be greater than maximum bound");
        } else {
            return new DoubleRangeEnumerable(min, max, step);
        }
    }

    /**
     * Takes the contents of the specified {@link ICharEnumerable} instance
     * and puts them into a new {@link String} instance.
     * @param enumerable The {@link ICharEnumerable} instance to copy its contents into a {@link String}.
     * @return The {@link String}, containing all the characters contained into the {@code enumerable}.
     * @since 1.0.37
     */
    @NotNull
    public static String ToString(ICharEnumerable enumerable)
        throws ArgumentNullException
    {
        if (CollectionManipulations.IsEmpty(enumerable)) {
            return StringUtils.Empty;
        } else {
            StringBuilder builder;
            if (enumerable instanceof ITraversableCollection<?> c) {
                builder = new StringBuilder(c.GetCount());
            } else {
                builder = new StringBuilder(500);
            }

            try (ICharEnumerator en = enumerable.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    builder.append(en.getUncastedCurrent());
                }
            }

            return builder.toString();
        }
    }

    /**
     * Divides all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param divisor The value that each element is to be divided by.
     * @return A new instance of the {@link IIntEnumerable} interface, describing all the elements
     * of {@code input}, divided by {@code divisor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws DivideByZeroException {@code divisor} is 0.
     * @since 1.0.37
     */
    @NotNull
    public static IIntEnumerable DivideBy(IIntEnumerable input, int divisor)
            throws ArgumentNullException, DivideByZeroException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        if (divisor == 0) { throw new DivideByZeroException(); }
        return new DivideAllElementsEnumerable_Int(input, divisor);
    }

    /**
     * Divides all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param divisor The value that each element is to be divided by.
     * @return A new instance of the {@link IFloatEnumerable} interface, describing all the elements
     * of {@code input}, divided by {@code divisor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IFloatEnumerable DivideBy(IIntEnumerable input, float divisor)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new DivideAllElementsEnumerable_IntToFloat(input, divisor);
    }

    /**
     * Divides all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param divisor The value that each element is to be divided by.
     * @return A new instance of the {@link IFloatEnumerable} interface, describing all the elements
     * of {@code input}, divided by {@code divisor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IDoubleEnumerable DivideBy(IIntEnumerable input, double divisor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new DivideAllElementsEnumerable_IntToDouble(input, divisor);
    }

    /**
     * Divides all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param divisor The value that each element is to be divided by.
     * @return A new instance of the {@link IFloatEnumerable} interface, describing all the elements
     * of {@code input}, divided by {@code divisor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IFloatEnumerable DivideBy(IFloatEnumerable input, float divisor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new DivideAllElementsEnumerable_Float(input, divisor);
    }

    /**
     * Divides all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param divisor The value that each element is to be divided by.
     * @return A new instance of the {@link IDoubleEnumerable} interface, describing all the elements
     * of {@code input}, divided by {@code divisor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IDoubleEnumerable DivideBy(IFloatEnumerable input, double divisor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new DivideAllElementsEnumerable_FloatToDouble(input, divisor);
    }

    /**
     * Divides all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param divisor The value that each element is to be divided by.
     * @return A new instance of the {@link IDoubleEnumerable} interface, describing all the elements
     * of {@code input}, divided by {@code divisor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IDoubleEnumerable DivideBy(IDoubleEnumerable input, double divisor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new DivideAllElementsEnumerable_Double(input, divisor);
    }

    /**
     * Multiplies all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param factor The value that each element is to be multiplied by.
     * @return A new instance of the {@link IIntEnumerable} interface, describing all the elements
     * of {@code input}, multiplied by {@code factor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IIntEnumerable MultiplyBy(IIntEnumerable input, int factor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new MultiplyAllElementsEnumerable_Int(input, factor);
    }

    /**
     * Multiplies all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param factor The value that each element is to be multiplied by.
     * @return A new instance of the {@link IFloatEnumerable} interface, describing all the elements
     * of {@code input}, multiplied by {@code factor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IFloatEnumerable MultiplyBy(IIntEnumerable input, float factor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new MultiplyAllElementsEnumerable_IntToFloat(input, factor);
    }

    /**
     * Multiplies all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param factor The value that each element is to be multiplied by.
     * @return A new instance of the {@link IDoubleEnumerable} interface, describing all the elements
     * of {@code input}, multiplied by {@code factor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IDoubleEnumerable MultiplyBy(IIntEnumerable input, double factor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new MultiplyAllElementsEnumerable_IntToDouble(input, factor);
    }

    /**
     * Multiplies all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param factor The value that each element is to be multiplied by.
     * @return A new instance of the {@link IFloatEnumerable} interface, describing all the elements
     * of {@code input}, multiplied by {@code factor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IFloatEnumerable MultiplyBy(IFloatEnumerable input, float factor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new MultiplyAllElementsEnumerable_Float(input, factor);
    }

    /**
     * Multiplies all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param factor The value that each element is to be multiplied by.
     * @return A new instance of the {@link IDoubleEnumerable} interface, describing all the elements
     * of {@code input}, multiplied by {@code factor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IDoubleEnumerable MultiplyBy(IFloatEnumerable input, double factor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new MultiplyAllElementsEnumerable_FloatToDouble(input, factor);
    }

    /**
     * Multiplies all the elements of the given enumerable by the specified value.
     * @param input The enumerable that provides the elements.
     * @param factor The value that each element is to be multiplied by.
     * @return A new instance of the {@link IDoubleEnumerable} interface, describing all the elements
     * of {@code input}, multiplied by {@code factor}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static IDoubleEnumerable MultiplyBy(IDoubleEnumerable input, double factor)
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new MultiplyAllElementsEnumerable_Double(input, factor);
    }

    /**
     * Casts all the elements of the given {@link IFloatEnumerable} to {@link Double}, and returns
     * the transformation result as an instance of the {@link IDoubleEnumerable} interface.
     * @param input The input single-precision floating-point enumerable to convert.
     * @return A new instance of the {@link IDoubleEnumerable} interface representing the items of {@code input}, cast to {@link Double}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    @NotNull
    public static IDoubleEnumerable CastToDouble(IFloatEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new CastToDoubleFromFloatEnumerable(input);
    }

    /**
     * Casts all the elements of the given {@link IByteEnumerable} to {@link Integer}, and returns
     * the transformation result as an instance of the {@link IIntEnumerable} interface.
     * @param input The input byte enumerable to convert.
     * @return A new instance of the {@link IIntEnumerable} interface representing the items of {@code input}, cast to {@link Integer}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    @NotNull
    public static IIntEnumerable CastToInt(IByteEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new CastToIntFromByteEnumerable(input);
    }

    /**
     * Casts all the elements of the given {@link IShortEnumerable} to {@link Integer}, and returns
     * the transformation result as an instance of the {@link IIntEnumerable} interface.
     * @param input The input short integer enumerable to convert.
     * @return A new instance of the {@link IIntEnumerable} interface representing the items of {@code input}, cast to {@link Integer}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     */
    @NotNull
    public static IIntEnumerable CastToInt(IShortEnumerable input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        return new CastToIntFromShortEnumerable(input);
    }

    /**
     * Normalizes all the elements of the specified integer enumerable to the range [0..1].
     * @param input The enumerable that provides the elements to normalize.
     * @param min The minimum, inclusive bound of the range of values of the contained elements.
     * @param max The maximum, inclusive bound of the range of values of the contained elements.
     * @return The normalized values of all the elements provided in {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code min} is greater than the {@code max} value.
     */
    @NotNull
    public static IFloatEnumerable Normalize(IIntEnumerable input, int min, int max)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        if (min > max) {
            throw new ArgumentOutOfRangeException("min", "Minimum value cannot be greater than the maximum value");
        } else {
            return new MinMaxNormalization_IntToFloatEnumerable(input, min, max);
        }
    }

    /**
     * Normalizes all the elements of the specified integer enumerable to the range [0..1].
     * @param input The enumerable that provides the elements to normalize.
     * @param min The minimum, inclusive bound of the range of values of the contained elements.
     * @param max The maximum, inclusive bound of the range of values of the contained elements.
     * @return The normalized values of all the elements provided in {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code min} is greater than the {@code max} value.
     * @apiNote This API has the same semantics as {@link #Normalize(IIntEnumerable, int, int)},
     * but it computes and returns the values as {@link Double}.
     */
    @NotNull
    public static IDoubleEnumerable NormalizeAsDouble(IIntEnumerable input, int min, int max)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        if (min > max) {
            throw new ArgumentOutOfRangeException("min", "Minimum value cannot be greater than the maximum value");
        } else {
            return new MinMaxNormalization_IntToDoubleEnumerable(input, min, max);
        }
    }

    /**
     * Normalizes all the elements of the specified long integer enumerable to the range [0..1].
     * @param input The enumerable that provides the elements to normalize.
     * @param min The minimum, inclusive bound of the range of values of the contained elements.
     * @param max The maximum, inclusive bound of the range of values of the contained elements.
     * @return The normalized values of all the elements provided in {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code min} is greater than the {@code max} value.
     */
    @NotNull
    public static IFloatEnumerable Normalize(ILongEnumerable input, long min, long max)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        if (min > max) {
            throw new ArgumentOutOfRangeException("min", "Minimum value cannot be greater than the maximum value");
        } else {
            return new MinMaxNormalization_LongToFloatEnumerable(input, min, max);
        }
    }

    /**
     * Normalizes all the elements of the specified long integer enumerable to the range [0..1].
     * @param input The enumerable that provides the elements to normalize.
     * @param min The minimum, inclusive bound of the range of values of the contained elements.
     * @param max The maximum, inclusive bound of the range of values of the contained elements.
     * @return The normalized values of all the elements provided in {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code min} is greater than the {@code max} value.
     * @apiNote This API has the same semantics as {@link #Normalize(ILongEnumerable, long, long)},
     * but it computes and returns the values as {@link Double}.
     */
    @NotNull
    public static IDoubleEnumerable NormalizeAsDouble(ILongEnumerable input, long min, long max)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        if (min > max) {
            throw new ArgumentOutOfRangeException("min", "Minimum value cannot be greater than the maximum value");
        } else {
            return new MinMaxNormalization_LongToDoubleEnumerable(input, min, max);
        }
    }

    /**
     * Normalizes all the elements of the specified single-precision floating-point integer enumerable to the range [0..1].
     * @param input The enumerable that provides the elements to normalize.
     * @param min The minimum, inclusive bound of the range of values of the contained elements.
     * @param max The maximum, inclusive bound of the range of values of the contained elements.
     * @return The normalized values of all the elements provided in {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code min} is greater than the {@code max} value.
     */
    @NotNull
    public static IFloatEnumerable Normalize(IFloatEnumerable input, float min, float max)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        if (min > max) {
            throw new ArgumentOutOfRangeException("min", "Minimum value cannot be greater than the maximum value");
        } else {
            return new MinMaxNormalization_FloatToFloatEnumerable(input, min, max);
        }
    }

    /**
     * Normalizes all the elements of the specified double-precision floating-point integer enumerable to the range [0..1].
     * @param input The enumerable that provides the elements to normalize.
     * @param min The minimum, inclusive bound of the range of values of the contained elements.
     * @param max The maximum, inclusive bound of the range of values of the contained elements.
     * @return The normalized values of all the elements provided in {@code input}.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code min} is greater than the {@code max} value.
     */
    @NotNull
    public static IDoubleEnumerable Normalize(IDoubleEnumerable input, double min, double max)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(input, "input");
        if (min > max) {
            throw new ArgumentOutOfRangeException("min", "Minimum value cannot be greater than the maximum value");
        } else {
            return new MinMaxNormalization_DoubleToDoubleEnumerable(input, min, max);
        }
    }
}
