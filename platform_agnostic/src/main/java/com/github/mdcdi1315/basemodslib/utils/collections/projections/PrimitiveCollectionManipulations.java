package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections.*;

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
        long sum = 0;
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
        double sum = 0;
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
        float sum = 0;
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
        long sum = 0;
        long count = 0;
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
        float sum = 0;
        long count = 0;
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
        double sum = 0;
        long count = 0;
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
