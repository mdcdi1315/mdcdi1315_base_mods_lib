package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyList;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.io.ByteArrayLE;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;

import java.util.Optional;

/**
 * Provides utility methods around the {@link IRandomSource} interface.
 */
public final class RandomUtils
{
    private RandomUtils() { }

    private static final double MUL_CONVERSION_MULTIPLIER = 1.0d / (1L << 53);
    private static final float MUL_CONVERSION_MULTIPLIER_FLOAT = 1.0f / (1 << 24);

    /**
     * Creates a thread-safe wrapper for the given random source.
     * @param source The {@link IRandomSource} to create a thread-safe wrapper for.
     * @return A new instance of the {@link IRandomSource} interface representing a thread-safe
     *         wrapper for {@code source}.
     * @throws ArgumentNullException {@code source} is {@code null}.
     * @implNote The returned value does also implement the {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized} interface.
     */
    @NotNull
    public static IRandomSource CreateSynchronizedWrapper(IRandomSource source)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(source, "source");
        return new ThreadSafeRandomSourceWrapper(source);
    }

    /**
     * Gets a random seed value.
     * @return A random seed value.
     * @apiNote The value may be becoming from a clock source or from an initialized and reliable random source.
     */
    public static long GetRandomSeed() { return System.currentTimeMillis(); }

    /**
     * Computes a double-precision floating-point random number being into the range [0..1).
     * @param random The random source to use for producing the number.
     * @return The produced {@code double} value.
     */
    public static double NextDouble(IRandomSource random) { return (random.NextLong() >>> 11) * MUL_CONVERSION_MULTIPLIER; }

    /**
     * Computes a single-precision floating-point random number being into the range [0..1).
     * @param random The random source to use for producing the number.
     * @return The produced {@code float} value.
     */
    public static float NextFloat(IRandomSource random) { return (random.NextLong() >>> 40) * MUL_CONVERSION_MULTIPLIER_FLOAT; }

    /**
     * Computes a random number being into the range [{@link Integer#MIN_VALUE}..{@link Integer#MAX_VALUE}].
     * @param random The random source to use for producing the number.
     * @return The produced {@code int} value.
     * @see #NextIntExtractHigh(IRandomSource)
     * @see #NextIntReturnBoth(IRandomSource)
     */
    // Extract the lower 32-bits of the value.
    public static int NextInt(IRandomSource random) { return (int)(random.NextLong() & 0x00000000FFFFFFFFL); }

    /**
     * Computes a random number being into the range [{@link Integer#MIN_VALUE}..{@link Integer#MAX_VALUE}].
     * @param random The random source to use for producing the number.
     * @return The produced {@code int} value.
     * @apiNote This API prefers the high bits of the {@code long} and returns those instead.
     * @see #NextInt(IRandomSource)
     * @see #NextIntReturnBoth(IRandomSource)
     */
    // Extract the high 32-bits of the value.
    public static int NextIntExtractHigh(IRandomSource random) { return (int)(random.NextLong() >> 32); }

    /**
     * Computes a random number being into the range [0..{@link Integer#MAX_VALUE}].
     * @param random The random source to use for producing the number.
     * @return The produced {@code int} value.
     * @see #NextPositiveIntReturnBoth(IRandomSource)
     * @see #NextPositiveIntExtractHigh(IRandomSource)
     * @see #NextPositiveIntInRangeExclusive(IRandomSource, int)
     */
    // Extract the lower 32-bits of the value, but keep only the positive or zero values.
    public static int NextPositiveInt(IRandomSource random) { return (int)(random.NextLong() & 0x000000007FFFFFFFL); }

    /**
     * Computes a random number being into the range [0..{@link Integer#MAX_VALUE}].
     * @param random The random source to use for producing the number.
     * @return The produced {@code int} value.
     * @apiNote This API prefers the high bits of the {@code long} and returns those instead.
     * @see #NextPositiveIntReturnBoth(IRandomSource)
     * @see #NextPositiveInt(IRandomSource)
     */
    // Extract the high 32-bits of the value, but keep only the positive or zero values.
    public static int NextPositiveIntExtractHigh(IRandomSource random) { return (int)(random.NextLong() >> 32) & 0x7FFFFFFF; }

    /**
     * Computes a random number being into the range [0..{@code max_exclusive}).
     * @param random The random source to use for producing the number.
     * @param max_exclusive The maximum, exclusive value of the acceptable range of values.
     * @return The produced {@code int} value.
     */
    // Extract the lower 32-bits of the value, but keep only the positive or zero values.
    // From these values, bound it to max_exclusive with the remainder.
    public static int NextPositiveIntInRangeExclusive(IRandomSource random, int max_exclusive) { return NextPositiveInt(random) % max_exclusive; }

    /**
     * Computes two random numbers being into the range [{@link Integer#MIN_VALUE}..{@link Integer#MAX_VALUE}].
     * @param random The random source to use for producing the number.
     * @return The two produced {@code int} values.
     */
    @NotNull
    public static GeneratedIntPair NextIntReturnBoth(IRandomSource random)
    {
        long value = random.NextLong();
        return new GeneratedIntPair(
                (int)(value & 0x00000000FFFFFFFFL),
                (int)((value >> 32) & 0x00000000FFFFFFFFL)
        );
    }

    /**
     * Computes two random numbers being into the range [0..{@link Integer#MAX_VALUE}].
     * @param random The random source to use for producing the number.
     * @return The two produced {@code int} values.
     */
    @NotNull
    public static GeneratedIntPair NextPositiveIntReturnBoth(IRandomSource random)
    {
        long value = random.NextLong();
        return new GeneratedIntPair(
                (int)(value & 0x000000007FFFFFFFL),
                (int)((value >> 32) & 0x000000007FFFFFFFL)
        );
    }

    /**
     * Computes a random number being into the range [0..{@link Long#MAX_VALUE}].
     * @param random The random source to use for producing the number.
     * @return The produced {@code long} value.
     */
    public static long NextPositiveLong(IRandomSource random) { return random.NextLong() & 0x7FFFFFFFFFFFFFFFL; }

    /**
     * Computes a random number being into the range [0..{@code max_exclusive}).
     * @param random The random source to use for producing the number.
     * @param max_exclusive The maximum, exclusive value of the acceptable range of values.
     * @return The produced {@code long} value.
     */
    public static long NextPositiveLongInRangeExclusive(IRandomSource random, long max_exclusive) { return NextPositiveLong(random) % max_exclusive; }

    /**
     * Computes a random boolean.
     * @param random The random source to use for producing the boolean value.
     * @return The produced {@code boolean} value.
     * @see #NextBoolean(IRandomSource, float)
     */
    public static boolean NextBoolean(IRandomSource random) { return random.NextLong() > 0; }

    /**
     * Computes a random boolean, given the specified probability value.
     * @param random The random source to use for producing the boolean value.
     * @param probability The probability value (range [0..1]) that does define the probability of the return value to be {@code true}. <br />
     *                    Note that specifying 1 effectively makes this method to always return {@code true}.
     * @return The produced {@code boolean} value.
     * @see #NextBoolean(IRandomSource)
     */
    public static boolean NextBoolean(IRandomSource random, float probability) { return NextFloat(random) < probability; }

    /**
     * Computes a random number being into the range [{@code min}..{@code max}].
     * @param random The random source to use for producing the number.
     * @param min The minimum, inclusive bound of the acceptable return values.
     * @param max The maximum, inclusive bound of the acceptable return values.
     * @return A random {@code int} number, that will have a value into the [{@code min}..{@code max}] range.
     * @see #NextLongInRange(IRandomSource, long, long)
     * @see #NextIntInRangeExclusive(IRandomSource, int, int)
     */
    public static int NextIntInRange(IRandomSource random, int min, int max) { return (int)Extensions.Lerp(NextFloat(random), min, max + 1); }

    /**
     * Computes a random number being into the range [{@code min}..{@code max}].
     * @param random The random source to use for producing the number.
     * @param min The minimum, inclusive bound of the acceptable return values.
     * @param max The maximum, inclusive bound of the acceptable return values.
     * @return A random {@code long} number, that will have a value into the [{@code min}..{@code max}] range.
     * @see #NextIntInRange(IRandomSource, int, int)
     * @see #NextLongInRangeExclusive(IRandomSource, long, long)
     */
    public static long NextLongInRange(IRandomSource random, long min, long max) { return (long)Extensions.Lerp(NextDouble(random), min, max + 1L); }

    /**
     * Computes a random number being into the range [{@code min_inclusive}..{@code max_exclusive}).
     * @param random The random source to use for producing the number.
     * @param min_inclusive The minimum, inclusive bound of the acceptable return values.
     * @param max_exclusive The maximum, exclusive bound of the acceptable return values.
     * @return A random {@code int} number, that will have a value into the [{@code min_inclusive}..{@code max_exclusive}) range.
     */
    public static int NextIntInRangeExclusive(IRandomSource random, int min_inclusive, int max_exclusive) { return (int)Extensions.Lerp(NextFloat(random), min_inclusive, max_exclusive); }

    /**
     * Computes a random number being into the range [{@code min_inclusive}..{@code max_exclusive}).
     * @param random The random source to use for producing the number.
     * @param min_inclusive The minimum, inclusive bound of the acceptable return values.
     * @param max_exclusive The maximum, exclusive bound of the acceptable return values.
     * @return A random {@code long} number, that will have a value into the [{@code min_inclusive}..{@code max_exclusive}) range.
     */
    public static long NextLongInRangeExclusive(IRandomSource random, long min_inclusive, long max_exclusive) { return (long)Extensions.Lerp(NextDouble(random), min_inclusive, max_exclusive); }

    /**
     * Computes a random number being into the range [{@code min}..{@code max}].
     * @param random The random source to use for producing the number.
     * @param min The minimum, inclusive bound of the acceptable return values.
     * @param max The maximum, exclusive bound of the acceptable return values.
     * @return A random {@code float} number, that will have a value into the [{@code min}..{@code max}] range.
     */
    public static float NextFloatInRange(IRandomSource random, float min, float max) { return Extensions.Lerp(NextFloat(random), min, max); }

    /**
     * Computes a random number being into the range [{@code min}..{@code max}].
     * @param random The random source to use for producing the number.
     * @param min The minimum, inclusive bound of the acceptable return values.
     * @param max The maximum, exclusive bound of the acceptable return values.
     * @return A random {@code double} number, that will have a value into the [{@code min}..{@code max}] range.
     */
    public static double NextDoubleInRange(IRandomSource random, double min, double max) { return Extensions.Lerp(NextDouble(random), min, max); }

    /**
     * Ensures that the given random source is appropriately initialized.
     * @param random The random source to initialize.
     */
    public static void InitializeSource(IRandomSource random)
    {
        int v = NextIntInRange(random, 1, 50);
        for (int I = 0; I < v; I++) { random.NextLong(); }
    }

    /**
     * Creates a new string with random contents filled in by the specified {@link IRandomSource}.
     * @param random The random source to produce the random characters for the string.
     * @param length The desired length for the string.
     * @return A string containing random characters produced with {@code random}.
     * @throws ArgumentOutOfRangeException {@code length} is a negative value.
     */
    @NotNull
    public static String NextString(IRandomSource random, int length)
            throws ArgumentOutOfRangeException
    {
        if (length < 0) {
            throw new ArgumentOutOfRangeException("length", "Length cannot be a negative value.");
        } else {
            char[] result = new char[length];
            for (int I = 0; I < length; I++)
            {
                result[I] = (char) Extensions.Lerp(NextFloat(random), 7f, 128f);
            }
            return new String(result);
        }
    }

    /**
     * Creates a new string with random contents filled in by the specified {@link IRandomSource}. <br />
     * The random contents that the string can take is from a-z (and capitals) and 0-9.
     * @param random The random source to produce the random characters for the string.
     * @param length The desired length for the string.
     * @return A string containing printable-only, random characters produced with {@code random}.
     * @throws ArgumentOutOfRangeException {@code length} is a negative value.
     */
    @NotNull
    public static String NextStringPrintableCharsOnly(IRandomSource random, int length)
            throws ArgumentOutOfRangeException
    {
        if (length < 0) {
            throw new ArgumentOutOfRangeException("length", "Length cannot be a negative value.");
        } else {
            char[] result = new char[length];
            for (int I = 0; I < length; I++)
            {
                float value = NextFloat(random);
                // Pick a random boolean to select between a number or a character.
                result[I] = (char) (
                    NextBoolean(random) ?
                        // A number was picked.
                        Extensions.Lerp(value, '0', '9')
                    : (
                        // A character was picked.
                        NextBoolean(random) ?
                            Extensions.Lerp(value, 'a', 'z') : // The character will be a simple letter.
                            Extensions.Lerp(value, 'A', 'Z') // The character will be a capital letter.
                    )
                );
            }
            return new String(result);
        }
    }

    /**
     * Given a random source, it creates a new instance of the {@link IIntEnumerable} interface,
     * wrapping the random source. The values returned from the enumerator are random values
     * produced by the provided random source.
     * @param random The random source that can produce random values.
     * @param count The number of random values to generate.
     * @return A new instance of the {@link IIntEnumerable} interface returning {@code count} random values.
     * @throws ArgumentNullException {@code random} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     */
    @NotNull
    public static IIntEnumerable GetRandomValuesEnumerable(IRandomSource random, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(random, "random");
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Number of values to return cannot be a negative value.");
        } else {
            return new RandomSourceEnumerable_Int(random, count);
        }
    }

    // Copied from CollectionHelpers.java
    @StackTraceHidden
    private static void CheckIndexCountInsideArrayBound(int index, int count, int array_length)
            throws OverflowException, ArgumentException
    {
        int r = index + count;
        // HD 2-12 Overflow if both arguments have the opposite sign of the result
        if (((index ^ r) & (count ^ r)) < 0) {
            throw new OverflowException("The specified combination of index and count parameters do exceed the maximum integer value.");
        } else if (r > array_length) {
            throw new ArgumentException("The specified combination of the index and count parameters are outside of the array's bounds.");
        }
    }

    /**
     * Fills the specified array with random bytes.
     * @param random The {@link IRandomSource} instance to use.
     * @param array The byte array to place random bytes to.
     * @param index The index inside {@code array} where to start placing bytes to.
     * @param count The number of bytes in {@code array} to fill with random data.
     * @throws ArgumentNullException {@code random} and/or {@code array} are {@code null}.
     * @throws ArgumentException {@code index} and/or {@code count} are negative values. <br /> <br />
     *                           -or- <br /> <br />
     *                           The combination of {@code index} and/or {@code count} parameters exceed the array's bounds.
     * @throws OverflowException The combination of {@code index} and {@code count} parameters exceed the maximum integer value.
     */
    public static void FillArray(IRandomSource random, byte[] array, int index, int count)
            throws ArgumentNullException, ArgumentException, OverflowException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(random, "random");
        if (index < 0) {
            throw new ArgumentException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Number of bytes to fill cannot be a negative value.");
        } else {
            CheckIndexCountInsideArrayBound(index, count, array.length);
            int longs = count / Long.SIZE;
            for (int I = 0; I < longs; I++) { ByteArrayLE.SetLong(array, I * Long.SIZE, random.NextLong()); }
            int rem = count % Long.SIZE;
            if (rem > 0)
            {
                byte[] temp = new byte[Long.SIZE];
                ByteArrayLE.SetLong(temp, 0, random.NextLong());
                System.arraycopy(temp, 0, array, (index + count) - rem, rem);
            }
        }
    }

    /**
     * Fills the specified array with random {@code long} values.
     * @param random The {@link IRandomSource} instance to use.
     * @param array The byte array to place random long values to.
     * @param index The index inside {@code array} where to start placing {@code long}s to.
     * @param count The number of {@code long}s in {@code array} to fill with random {@code long}s.
     * @throws ArgumentNullException {@code random} and/or {@code array} are {@code null}.
     * @throws ArgumentException {@code index} and/or {@code count} are negative values. <br /> <br />
     *                           -or- <br /> <br />
     *                           The combination of {@code index} and/or {@code count} parameters exceed the array's bounds.
     * @throws OverflowException The combination of {@code index} and {@code count} parameters exceed the maximum integer value.
     */
    public static void FillArray(IRandomSource random, long[] array, int index, int count)
            throws ArgumentNullException, ArgumentException, OverflowException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(random, "random");
        if (index < 0) {
            throw new ArgumentException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Number of bytes to fill cannot be a negative value.");
        } else {
            CheckIndexCountInsideArrayBound(index, count, array.length);
            int total = index + count;
            for (int I = index; I < total; I++) { array[I] = random.NextLong(); }
        }
    }

    /**
     * Fills the specified array with random {@code int} values.
     * @param random The {@link IRandomSource} instance to use.
     * @param array The byte array to place random integer values to.
     * @param index The index inside {@code array} where to start placing {@code int}s to.
     * @param count The number of {@code long}s in {@code array} to fill with random {@code int}s.
     * @throws ArgumentNullException {@code random} and/or {@code array} are {@code null}.
     * @throws ArgumentException {@code index} and/or {@code count} are negative values. <br /> <br />
     *                           -or- <br /> <br />
     *                           The combination of {@code index} and/or {@code count} parameters exceed the array's bounds.
     * @throws OverflowException The combination of {@code index} and {@code count} parameters exceed the maximum integer value.
     */
    public static void FillArray(IRandomSource random, int[] array, int index, int count)
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(random, "random");
        if (index < 0) {
            throw new ArgumentException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Number of bytes to fill cannot be a negative value.");
        } else {
            CheckIndexCountInsideArrayBound(index, count, array.length);
            long tmp;
            for (int I = index, total = index + count; I < total; )
            {
                tmp = random.NextLong();
                array[I++] = (int)(tmp & 0x00000000FFFFFFFFL);
                if (I < total) { array[I++] = (int)((tmp >> 32) & 0x00000000FFFFFFFFL); }
            }
        }
    }

    /**
     * Fills the specified array with random {@code float} values.
     * @param random The {@link IRandomSource} instance to use.
     * @param array The byte array to place random floating-point values to.
     * @param index The index inside {@code array} where to start placing {@code float}s to.
     * @param count The number of {@code long}s in {@code array} to fill with random {@code float}s.
     * @throws ArgumentNullException {@code random} and/or {@code array} are {@code null}.
     * @throws ArgumentException {@code index} and/or {@code count} are negative values. <br /> <br />
     *                           -or- <br /> <br />
     *                           The combination of {@code index} and/or {@code count} parameters exceed the array's bounds.
     * @throws OverflowException The combination of {@code index} and {@code count} parameters exceed the maximum integer value.
     */
    public static void FillArray(IRandomSource random, float[] array, int index, int count)
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(random, "random");
        if (index < 0) {
            throw new ArgumentException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Number of bytes to fill cannot be a negative value.");
        } else {
            CheckIndexCountInsideArrayBound(index, count, array.length);
            int total = index + count;
            for (int I = index; I < total; I++) { array[I] = NextFloat(random); }
        }
    }

    /**
     * Fills the specified array with random {@code double} values.
     * @param random The {@link IRandomSource} instance to use.
     * @param array The byte array to place random double-precision floating-point values to.
     * @param index The index inside {@code array} where to start placing {@code double}s to.
     * @param count The number of {@code long}s in {@code array} to fill with random {@code double}s.
     * @throws ArgumentNullException {@code random} and/or {@code array} are {@code null}.
     * @throws ArgumentException {@code index} and/or {@code count} are negative values. <br /> <br />
     *                           -or- <br /> <br />
     *                           The combination of {@code index} and/or {@code count} parameters exceed the array's bounds.
     * @throws OverflowException The combination of {@code index} and {@code count} parameters exceed the maximum integer value.
     */
    public static void FillArray(IRandomSource random, double[] array, int index, int count)
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        ArgumentNullException.ThrowIfNull(random, "random");
        if (index < 0) {
            throw new ArgumentException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Number of bytes to fill cannot be a negative value.");
        } else {
            CheckIndexCountInsideArrayBound(index, count, array.length);
            int total = index + count;
            for (int I = index; I < total; I++) { array[I] = NextDouble(random); }
        }
    }

    /**
     * Given a Java array of type {@link T}, selects a random item from it.
     * @param random The random source to use for selecting a random item.
     * @param options The array of options to pick from.
     * @return An {@link Optional} value indicating whether an item selected from the array. <br />
     *         This will additionally return the empty value if the element itself is {@code null}.
     * @param <T> The type of the element that is held by the array.
     */
    @NotNull
    @SafeVarargs
    public static <T> Optional<T> SelectRandomItem(IRandomSource random, T... options)
    {
        int count = options.length;
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(options[NextPositiveIntInRangeExclusive(random, count)]);
    }

    /**
     * Given a Java Collections Framework list object, selects a random item from it.
     * @param random The random source to use for selecting a random item.
     * @param list The list object to use for selecting a random item.
     * @return An {@link Optional} value indicating whether an item selected from the list. <br />
     *         This will additionally return the empty value if the element itself is {@code null}.
     * @param <T> The type of the element that is held by the collection.
     */
    @NotNull
    public static <T> Optional<T> SelectRandomItem(IRandomSource random, java.util.List<T> list)
    {
        int count = list.size();
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(list.get(NextPositiveIntInRangeExclusive(random, count)));
    }

    /**
     * Given a traversable collection object, selects a random item from it.
     * @param random The random source to use for selecting a random item.
     * @param collection The traversable collection object to use for selecting a random item.
     * @return An {@link Optional} value indicating whether an item selected from the traversable collection. <br />
     *         This will additionally return the empty value if the element itself is {@code null}.
     * @param <T> The type of the element that is held by the collection.
     */
    @NotNull
    public static <T> Optional<T> SelectRandomItem(IRandomSource random, ITraversableCollection<T> collection)
    {
        int count = collection.GetCount();
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(collection.GetItem(NextPositiveIntInRangeExclusive(random, count)));
    }

    /**
     * Given a list object, selects a random item from it.
     * @param random The random source to use for selecting a random item.
     * @param list The list object to use for selecting a random item.
     * @return An {@link Optional} value indicating whether an item selected from the list. <br />
     *         This will additionally return the empty value if the element itself is {@code null}.
     * @param <T> The type of the element that is held by the collection.
     */
    @NotNull
    public static <T> Optional<T> SelectRandomItem(IRandomSource random, IList<T> list)
    {
        int count = list.getCount();
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(list.getItem(NextPositiveIntInRangeExclusive(random, count)));
    }

    /**
     * Given a read-only list object, selects a random item from it.
     * @param random The random source to use for selecting a random item.
     * @param list The list object to use for selecting a random item.
     * @return An {@link Optional} value indicating whether an item selected from the list. <br />
     *         This will additionally return the empty value if the element itself is {@code null}.
     * @param <T> The type of the element that is held by the collection.
     */
    @NotNull
    public static <T> Optional<T> SelectRandomItem(IRandomSource random, IReadOnlyList<T> list)
    {
        int count = list.getCount();
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(list.getItem(NextPositiveIntInRangeExclusive(random, count)));
    }

    /**
     * Given a Java Collections Framework list object, creates a {@link IRandomLookup}
     * that can return random elements of the collection object.
     * @param random The random source to use.
     * @param list The list object to use for getting random elements.
     * @return A new instance of the {@link IRandomLookup} interface.
     * @param <T> The type of the element that is held by the collection.
     * @throws ArgumentNullException {@code random} and/or {@code list} are {@code null}.
     */
    @NotNull
    public static <T> IRandomLookup<T> CreateRandomLookup(IRandomSource random, java.util.List<T> list)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        ArgumentNullException.ThrowIfNull(random, "random");
        return new JavaUtilListRandomLookup<>(random, list);
    }

    /**
     * Given an {@link IReadOnlyList} object, creates a {@link IRandomLookup}
     * that can return random elements of the collection object.
     * @param random The random source to use.
     * @param list The list object to use for getting random elements.
     * @return A new instance of the {@link IRandomLookup} interface.
     * @param <T> The type of the element that is held by the collection.
     * @throws ArgumentNullException {@code random} and/or {@code list} are {@code null}.
     */
    @NotNull
    public static <T> IRandomLookup<T> CreateRandomLookup(IRandomSource random, IReadOnlyList<T> list)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        ArgumentNullException.ThrowIfNull(random, "random");
        return new IReadOnlyListRandomLookup<>(random, list);
    }

    /**
     * Given an {@link IList} object, creates a {@link IRandomLookup}
     * that can return random elements of the collection object.
     * @param random The random source to use.
     * @param list The list object to use for getting random elements.
     * @return A new instance of the {@link IRandomLookup} interface.
     * @param <T> The type of the element that is held by the collection.
     * @throws ArgumentNullException {@code random} and/or {@code list} are {@code null}.
     */
    @NotNull
    public static <T> IRandomLookup<T> CreateRandomLookup(IRandomSource random, IList<T> list)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        ArgumentNullException.ThrowIfNull(random, "random");
        return new IListRandomLookup<>(random, list);
    }

    /**
     * Given an {@link ITraversableCollection} object, creates a {@link IRandomLookup}
     * that can return random elements of the collection object.
     * @param random The random source to use.
     * @param collection The traversable collection object to use for getting random elements.
     * @return A new instance of the {@link IRandomLookup} interface.
     * @param <T> The type of the element that is held by the collection.
     * @throws ArgumentNullException {@code random} and/or {@code list} are {@code null}.
     */
    @NotNull
    public static <T> IRandomLookup<T> CreateRandomLookup(IRandomSource random, ITraversableCollection<T> collection)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(random, "random");
        ArgumentNullException.ThrowIfNull(collection, "collection");
        return new TraversableCollectionRandomLookup<>(random, collection);
    }
}
