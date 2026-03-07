package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.Extension;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;

import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
import net.minecraft.util.Mth;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * I like better this term here than 'Utilities'.
 */
@Extension
@SuppressWarnings("unused")
public final class Extensions
{
    private Extensions() {}

    /**
     * Provides the value closer to the ratio of the circumference of a circle to its diameter, as a single floating-point number.
     */
    public static final float PI = (float) Math.PI;
    /**
     * Provides the value closer to the ratio of the circumference of a circle to its diameter multiplied by 2, as a single floating-point number.
     */
    public static final float TWO_PI = PI * 2;

    /**
     * Produces a random {@link Direction} value, excluding the {@link Direction#UP} and {@link Direction#DOWN} constant values.
     * @param rs The {@link RandomSource} instance to produce the random direction from.
     * @return The produced random direction.
     */
    @Extension
    public static Direction GetRandomDirectionExcludingUpDown(RandomSource rs)
    {
        Direction ret;
        Direction[] values = Direction.values();
        int len = values.length - 1;
        do {
            ret = values[RandomBetweenInclusiveUnsafe(rs, 0 , len)];
        } while (ret == Direction.UP || ret == Direction.DOWN);
        return ret;
    }

    /**
     * Gets a random direction pair, excluding {@link Direction#UP} and {@link Direction#DOWN} constant values.
     * @param rs The {@link RandomSource} instance to use for selecting the direction pair.
     * @return A {@link KeyValuePair} instance containing two randomly-selected {@link Direction} values.
     * @apiNote Note that the API does not validate input arguments at all. <br />
     * This is done to avoid the overhead that the validation methods do have.
     */
    @Extension
    public static KeyValuePair<Direction , Direction> GetRandomDirectionPairNonUpDown(RandomSource rs)
    {
        return SelectRandomFromListUnsafe(List.of(
                new KeyValuePair<>(Direction.EAST , Direction.NORTH),
                new KeyValuePair<>(Direction.WEST , Direction.NORTH),
                new KeyValuePair<>(Direction.EAST , Direction.SOUTH),
                new KeyValuePair<>(Direction.WEST , Direction.SOUTH)
        ) , rs);
    }

    /**
     * Computes the trigonometric sine of the specified angle. <br />
     * For more information, see the {@link Math#sin(double)} function.
     * @param v The angle to compute its trigonometric sine.
     * @return The trigonometric sine of {@code v}.
     */
    public static float Sin(float v)
    {
        return Mth.sin(v); // Currently forwards to Minecraft's math class, we need to find a better alternative for this
    }

    /**
     * Computes the trigonometric sine of the specified angle. <br />
     * For more information, see the {@link Math#sin(double)} function.
     * @param v The angle to compute its trigonometric sine.
     * @return The trigonometric sine of {@code v}.
     */
    public static double Sin(double v) { return Math.sin(v); }

    /**
     * Computes the trigonometric cosine of the specified angle. <br />
     * For more information, see the {@link Math#cos(double)} function.
     * @param v The angle to compute its trigonometric cosine.
     * @return The trigonometric cosine of {@code v}.
     */
    public static float Cos(float v)
    {
        return Mth.cos(v); // Currently forwards to Minecraft's math class, we need to find a better alternative for this
    }

    /**
     * Computes the trigonometric cosine of the specified angle. <br />
     * For more information, see the {@link Math#cos(double)} function.
     * @param v The angle to compute its trigonometric cosine.
     * @return The trigonometric cosine of {@code v}.
     */
    public static double Cos(double v)
    {
        return Math.cos(v); // Currently forwards to Minecraft's math class, we need to find a better alternative for this
    }

    /**
     * Computes the inverted square root of {@code d}.
     * @param d The value to compute it's inverted square root.
     * @return The inverted square root of {@code d}.
     */
    public static double InvertedSquareRoot(double d) { return 1.0d / Math.sqrt(d); }

    /**
     * Computes the inverted square root of {@code d}.
     * @param d The value to compute it's inverted square root.
     * @return The inverted square root of {@code d}.
     */
    public static float InvertedSquareRoot(float d) { return (float) (1.0d / Math.sqrt(d)); }

    /**
     * Computes the integer closest to {@code value}. <br />
     * If the value has a fractional part, a value of 1 is added before the method returns.
     * @param value The value to be computed as {@link Integer}.
     * @return The {@link Integer} corresponding to {@code value}.
     */
    public static int Ceiling(float value) {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    /**
     * Computes the integer closest to {@code value}. <br />
     * If the value has a fractional part, a value of 1 is added before the method returns.
     * @param value The value to be computed as {@link Integer}.
     * @return The {@link Integer} corresponding to {@code value}.
     */
    public static int Ceiling(double value) {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

    /**
     * Linearly interpolates a value ranging from 0 to 1 to the specified range, and returns the result.
     * @param delta The value to linearly interpolate.
     * @param start The minimum inclusive bound of the mapped range.
     * @param end The maximum inclusive bound of the mapped range.
     * @return The interpolated value.
     */
    public static float Lerp(float delta, float start, float end)
    {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        return start + (delta * (end - start));
    }

    /**
     * Linearly interpolates a value ranging from 0 to 1 to the specified range, and returns the result.
     * @param delta The value to linearly interpolate.
     * @param start The minimum inclusive bound of the mapped range.
     * @param end The maximum inclusive bound of the mapped range.
     * @return The interpolated value.
     */
    public static double Lerp(double delta, double start, double end)
    {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        return start + (delta * (end - start));
    }

    /**
     * Computes the integer closest to {@code value}. <br />
     * If the value has a fractional part, a value of 1 is removed before the method returns.
     * @param value The value to be computed as {@link Integer}.
     * @return The {@link Integer} corresponding to {@code value}.
     */
    public static int Floor(float value) {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    /**
     * Computes the integer closest to {@code value}. <br />
     * If the value has a fractional part, a value of 1 is removed before the method returns.
     * @param value The value to be computed as {@link Integer}.
     * @return The {@link Integer} corresponding to {@code value}.
     */
    public static int Floor(double value) {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    /**
     * Computes the power of {@code input} raised to 2.
     * @param input The input argument.
     * @return The square of {@code input}.
     */
    public static float Square(float input) { return input * input; }

    /**
     * Computes the power of {@code input} raised to 2.
     * @param input The input argument.
     * @return The square of {@code input}.
     */
    public static double Square(double input) { return input * input; }

    /**
     * Computes the power of {@code input} raised to 2.
     * @param input The input argument.
     * @return The square of {@code input}.
     */
    public static int Square(int input) { return input * input; }

    /**
     * Computes the square root of {@code value}.
     * @param value The value to compute it's square root.
     * @return The square root of {@code value}.
     */
    public static float SquareRoot(float value) { return (float)Math.sqrt(value); }

    /**
     * Gets a random item from the specified list, and returns that item.
     * @param elements The list of items to get a random item from.
     * @param rs The random source to use for getting the random item.
     * @return The random item.
     * @param <T> The type of the items to select from. An item of such type is returned.
     * @throws ArgumentNullException {@code elements} or {@code rs} were {@code null}.
     */
    public static <T> T SelectRandomFromList(List<T> elements, RandomSource rs)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(elements , "elements");
        ArgumentNullException.ThrowIfNull(rs , "rs");
        return SelectRandomFromListUnsafe(elements , rs);
    }

    /**
     * Gets a random item from the specified list, and returns that item.
     * Additionally, it ensures that the specified item is not selected in any way.
     * @param list The list of items to get a random item from.
     * @param item_to_exclude The item instance to exclude from the possible outcomes.
     * @param source The random source to use for getting the random item.
     * @return The random item, ensuring that is not the instance specified in {@code item_to_exclude}.
     * @param <T> The type of the items to select from. An item of such type is returned.
     */
    public static <T> T SelectRandomFromListWithExclusion(List<T> list , @MaybeNull T item_to_exclude , RandomSource source)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list , "list");
        ArgumentNullException.ThrowIfNull(source , "random");
        return SelectRandomFromListWithExclusionUnsafe(list , item_to_exclude , source);
    }

    /**
     * Gets a random item from the specified list, and returns that item.
     * @param elements The list of items to get a random item from.
     * @param rs The random source to use for getting the random item.
     * @return The random item.
     * @param <T> The type of the items to select from. An item of such type is returned.
     * @apiNote This is the unsafe variant of {@link #SelectRandomFromList(List, RandomSource)}. 
     * When you are unsure about the input arguments, use that method to validate them instead. <br />
     * Using this without ensuring that the objects passed to this method are valid, this call can cause unspecified issues.
     */
    public static <T> T SelectRandomFromListUnsafe(List<T> elements, RandomSource rs) { return elements.get(RandomBetweenInclusiveUnsafe(rs, 0, elements.size() - 1)); }

    /**
     * Unsafely gets a random item from the specified list, and returns that item.
     * Additionally, it ensures that the specified item is not selected in any way.
     * @param list The list of items to get a random item from.
     * @param item_to_exclude The item instance to exclude from the possible outcomes.
     * @param source The random source to use for getting the random item.
     * @param comparer The equality comparer to use for testing the objects for equality.
     * @return The random item, ensuring that is not the instance specified in {@code item_to_exclude}.
     * @param <T> The type of the items to select from. An item of such type is returned.
     * @apiNote This is the unsafe variant of {@link #SelectRandomFromListWithExclusion(List, Object, RandomSource)}. 
     * When you are unsure about the input arguments, use that method to validate them instead. <br />
     * Using this without ensuring that the objects passed to this method are valid, this call can cause unspecified issues.
     */
    public static <T> T SelectRandomFromListWithExclusionUnsafe(List<T> list , @MaybeNull T item_to_exclude , IEqualityComparer<T> comparer, RandomSource source)
    {
        T item;
        int size = list.size() - 1;
        if (size == 0) {
            return list.get(0);
        }
        do {
            item = list.get(RandomBetweenInclusiveUnsafe(source ,0, size));
        } while (comparer.Equals(item_to_exclude , item));
        return item;
    }

    /**
     * Gets a random item from the specified list, and returns that item.
     * Additionally, it ensures that the specified item is not selected in any way.
     * @param list The list of items to get a random item from.
     * @param item_to_exclude The item instance to exclude from the possible outcomes.
     * @param source The random source to use for getting the random item.
     * @return The random item, ensuring that is not the instance specified in {@code item_to_exclude}.
     * @param <T> The type of the items to select from. An item of such type is returned.
     * @apiNote This method uses the {@link Object#equals(Object)} pattern to compare the objects.
     * Use {@link #SelectRandomFromListWithExclusionUnsafe(List, Object, IEqualityComparer, RandomSource)} if you want to control how comparison is done.
     */
    public static <T> T SelectRandomFromListWithExclusionUnsafe(List<T> list , @MaybeNull T item_to_exclude , RandomSource source)
    {
        return SelectRandomFromListWithExclusionUnsafe(list , item_to_exclude , new JavaObjectEqualsEqualityComparer<>() , source);
    }

    /**
     * Attempts to dispose all the elements defined in an iterable.
     * @param iterable The iterable to dispose all it's elements.
     * @param <T> The type of the elements contained in the iterable and are to be disposed of.
     * @throws ArgumentNullException {@code iterable} is {@code null}.
     * @throws AggregateException One or more exceptions occurred while calling {@linkplain T#Dispose()}.
     */
    public static <T extends IDisposable> void DisposeAll(Iterable<T> iterable)
            throws ArgumentNullException, AggregateException
    {
        ArgumentNullException.ThrowIfNull(iterable , "iterable");
        var exceptions = new SingleLinkedList<com.github.mdcdi1315.DotNetLayer.System.Exception>();
        for (T i : iterable)
        {
            try {
                i.Dispose();
            } catch (com.github.mdcdi1315.DotNetLayer.System.Exception e) {
                exceptions.Add(e);
            }
        }
        if (exceptions.getCount() > 0) {
            throw new AggregateException("One or more elements failed to be disposed of.", exceptions);
        }
    }

    /**
     * Attempts to dispose all the elements defined in an enumerable.
     * @param enumerable The enumerable to dispose all it's elements.
     * @param <T> The type of the elements contained in the enumerable and are to be disposed of.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     * @throws AggregateException One or more exceptions occurred while calling {@linkplain T#Dispose()}.
     * @since 1.0.18
     */
    public static <T extends IDisposable> void DisposeAll(IEnumerable<T> enumerable)
            throws ArgumentNullException, AggregateException
    {
        ArgumentNullException.ThrowIfNull(enumerable , "enumerable");
        var exceptions = new SingleLinkedList<com.github.mdcdi1315.DotNetLayer.System.Exception>();
        var en = enumerable.GetEnumerator();
        try {
            while (en.MoveNext()) {
                try {
                    en.getCurrent().Dispose();
                } catch (com.github.mdcdi1315.DotNetLayer.System.Exception e) {
                    exceptions.Add(e);
                }
            }
        } finally {
            en.Dispose();
        }
        if (exceptions.getCount() > 0) {
            throw new AggregateException("One or more elements failed to be disposed of.", exceptions);
        }
    }

    /**
     * Enumerates all the items contained in the specified enumerable object, and executes the specified {@link Action1} on them.
     * @param enumerable The enumerable to iterate all of its elements.
     * @param action The action to execute in each one of the items returned by {@code enumerable}.
     * @param <T> The type of the elements contained in the enumerable.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     * @throws AggregateException An exception was occurred while calling {@link Action1#action(Object)}.
     * @since 1.0.18
     */
    public static <T> void ForEachInEnumerable(IEnumerable<T> enumerable, Action1<T> action)
            throws ArgumentNullException, AggregateException
    {
        ArgumentNullException.ThrowIfNull(action, "action");
        ArgumentNullException.ThrowIfNull(enumerable , "enumerable");
        var en = enumerable.GetEnumerator();
        try {
            while (en.MoveNext()) { action.action(en.getCurrent()); }
        } catch (com.github.mdcdi1315.DotNetLayer.System.Exception e) {
            throw new AggregateException("An exception was occurred while iterating an enumerable.", e);
        } finally {
            en.Dispose();
        }
    }

    /**
     * Computes a random integer between {@code min_inclusive} and {@code max_inclusive} values.
     * @param rs The {@link RandomSource} to compute the random integer from.
     * @param min_inclusive The minimum inclusive bound of the returned value.
     * @param max_inclusive The maximum inclusive bound of the returned value.
     * @return A random integer value between {@code min_inclusive} and {@code max_inclusive} values.
     * @throws ArgumentNullException {@code rs} is {@code null}.
     */
    public static int RandomBetweenInclusive(RandomSource rs , int min_inclusive , int max_inclusive)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(rs , "rs");
        return RandomBetweenInclusiveUnsafe(rs , min_inclusive , max_inclusive);
    }

    /**
     * Unsafely computes a random integer between {@code min_inclusive} and {@code max_inclusive} values.
     * @param rs The {@link RandomSource} to compute the random integer from.
     * @param min_inclusive The minimum inclusive bound of the returned value.
     * @param max_inclusive The maximum inclusive bound of the returned value.
     * @return A random integer value between {@code min_inclusive} and {@code max_inclusive} values.
     */
    public static int RandomBetweenInclusiveUnsafe(RandomSource rs, int min_inclusive, int max_inclusive) { return rs.nextInt(max_inclusive - min_inclusive + 1) + min_inclusive; }

    /**
     * Computes a random value between {@code min_inclusive} and {@code max_exclusive} values.
     * @implNote From 1.0.18, this method forwards to {@link #Lerp(float, float, float)} passing as the 'delta' parameter the value computed by {@link RandomSource#nextFloat()} method.
     * @param rs The {@link RandomSource} to compute the random value from.
     * @param min_inclusive The minimum inclusive bound of the returned value.
     * @param max_exclusive The maximum exclusive bound of the returned value.
     * @return A random value between {@code min_inclusive} and {@code max_exclusive} values.
     * @throws ArgumentNullException {@code rs} is {@code null}.
     */
    public static float RandomBetween(RandomSource rs, float min_inclusive, float max_exclusive)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(rs , "rs");
        return RandomBetweenUnsafe(rs , min_inclusive , max_exclusive);
    }

    /**
     * Computes a random value between {@code min_inclusive} and {@code max_exclusive} values.
     * @implNote From 1.0.18, this method forwards to {@link #Lerp(double, double, double)} passing as the 'delta' parameter the value computed by {@link RandomSource#nextDouble()} method.
     * @param rs The {@link RandomSource} to compute the random value from.
     * @param min_inclusive The minimum inclusive bound of the returned value.
     * @param max_exclusive The maximum exclusive bound of the returned value.
     * @return A random value between {@code min_inclusive} and {@code max_exclusive} values.
     * @throws ArgumentNullException {@code rs} is {@code null}.
     */
    public static double RandomBetween(RandomSource rs, double min_inclusive, double max_exclusive)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(rs , "rs");
        return RandomBetweenUnsafe(rs , min_inclusive , max_exclusive);
    }

    /**
     * Unsafely computes a random value between {@code min_inclusive} and {@code max_exclusive} values.
     * @implNote From 1.0.18, this method forwards to {@link #Lerp(float, float, float)} passing as the 'delta' parameter the value computed by {@link RandomSource#nextFloat()} method.
     * @param random The {@link RandomSource} to compute the random value from.
     * @param min_inclusive The minimum inclusive bound of the returned value.
     * @param max_exclusive The maximum exclusive bound of the returned value.
     * @return A random value between {@code min_inclusive} and {@code max_exclusive} values.
     */
    public static float RandomBetweenUnsafe(RandomSource random, float min_inclusive, float max_exclusive) { return Lerp(random.nextFloat(), min_inclusive, max_exclusive); }

    /**
     * Unsafely computes a random value between {@code min_inclusive} and {@code max_exclusive} values.
     * @implNote From 1.0.18, this method forwards to {@link #Lerp(double, double, double)} passing as the 'delta' parameter the value computed by {@link RandomSource#nextDouble()} method.
     * @param random The {@link RandomSource} to compute the random value from.
     * @param min_inclusive The minimum inclusive bound of the returned value.
     * @param max_exclusive The maximum exclusive bound of the returned value.
     * @return A random value between {@code min_inclusive} and {@code max_exclusive} values.
     */
    public static double RandomBetweenUnsafe(RandomSource random, double min_inclusive, double max_exclusive) { return Lerp(random.nextDouble(), min_inclusive, max_exclusive); }

    /**
     * Initializes appropriately the given random number generator.
     * @param random The random source to initialize.
     * @throws ArgumentNullException {@code random} was {@code null}.
     */
    public static void InitializeRandomSource(RandomSource random)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(random);
        for (byte I = 0; I < 10; I++) {
            random.nextInt();
        }
    }

    /**
     * Chains a second function to a function of type {@link Func2} and it maps the result to {@link TR}, as such
     * a transformation happens such as that {@link TS} -&gt; {@link TM} -&gt; {@link TR}.
     * @param first The first {@link Func2} that accepts a parameter of type {@link TS} and returns an object of type {@link TM}.
     * @param second The second {@link Func2} that accepts a parameter of type {@link TM } and returns an object of type {@link TR}.
     * @return A new {@link Func2} that now accepts an argument of type {@link TS} and converts it to an object of type {@link TR}.
     * @param <TS> The type of the parameter that is passed on the returned method reference.
     * @param <TM> The type of the parameter that it's converted object from the {@code first} parameter is fed as an argument to the {@code second} parameter.
     * @param <TR> The type that is returned as a result of invoking the returned function.
     * @throws ArgumentNullException {@code first} and/or {@code second} is {@code null}.
     * @deprecated This method forwards to {@link FunctionManipulations#ThenMap(Func2, Func2)} since 1.0.20. There are no plans to remove this method, but newer consumers should use the mentioned method instead.
     * @since 1.0.19
     */
    @Deprecated(since = "1.0.20")
    public static <TS, TM, TR> Func2<TS, TR> ThenExecute(Func2<TS, TM> first, Func2<TM, TR> second) throws ArgumentNullException { return FunctionManipulations.ThenMap(first, second); }

    /**
     * Chains a second action to a function of type {@link Action0}.
     * @param first The first {@link Action0} to chain.
     * @param second The second {@link Action0} to chain.
     * @return An {@link Action0} that first executes the function reference provided in {@code first}, then the second one provided in {@code second}.
     * @throws ArgumentNullException {@code first} and/or {@code second} is {@code null}.
     * @deprecated This method forwards to {@link FunctionManipulations#ThenMap(Func2, Func2)} since 1.0.20. There are no plans to remove this method, but newer consumers should use the mentioned method instead.
     * @since 1.0.19
     */
    @Deprecated(since = "1.0.20")
    public static Action0 ThenExecute(Action0 first, Action0 second) throws ArgumentNullException { return FunctionManipulations.ThenDo(first, second); }

    /**
     * Type-casts the specified object to the specified type.
     * @param o The {@link Object} to cast.
     * @return The cast type, if casting is possible to {@link TR}; otherwise {@code null}.
     * @param <TR> The type to cast to.
     * @since 1.0.19
     */
    public static <TR> TR TypeCast(Object o) { try { return (TR) o; } catch (ClassCastException e) { return null; } }

    /**
     * This method call is deprecated. Use the {@link #Lerp(double, double, double)} method instead.
     */
    @Deprecated(forRemoval = true, since = "1.0.18")
    public static double NumberMap(double input , double inputbase , double outputbase) { return ((input / inputbase) * outputbase); }

    /**
     * Clamps a value to the range specified by the {@code minimum} and {@code maximum} parameters.
     * @param value The value to clamp into [{@code minimum}..{@code maximum}].
     * @param minimum The minimum inclusive bound of the range to clamp {@code value}.
     * @param maximum The maximum inclusive bound of the range to clamp {@code value}.
     * @return The value of the {@code value} parameter, or if less than the {@code minimum} value, the value of {@code minimum}.
     * Or, if {@code value} is greater than {@code maximum}, the value of {@code maximum}.
     */
    public static double Clamp(double value , double minimum , double maximum) { return value < minimum ? minimum : Math.min(value, maximum); }

    /**
     * Clamps a value to the range specified by the {@code minimum} and {@code maximum} parameters.
     * @param value The value to clamp into [{@code minimum}..{@code maximum}].
     * @param minimum The minimum inclusive bound of the range to clamp {@code value}.
     * @param maximum The maximum inclusive bound of the range to clamp {@code value}.
     * @return The value of the {@code value} parameter, or if less than the {@code minimum} value, the value of {@code minimum}.
     * Or, if {@code value} is greater than {@code maximum}, the value of {@code maximum}.
     */
    public static float Clamp(float value , float minimum , float maximum) { return value < minimum ? minimum : Math.min(value, maximum); }

    /**
     * Clamps a value to the range specified by the {@code minimum} and {@code maximum} parameters.
     * @param value The value to clamp into [{@code minimum}..{@code maximum}].
     * @param minimum The minimum inclusive bound of the range to clamp {@code value}.
     * @param maximum The maximum inclusive bound of the range to clamp {@code value}.
     * @return The value of the {@code value} parameter, or if less than the {@code minimum} value, the value of {@code minimum}.
     * Or, if {@code value} is greater than {@code maximum}, the value of {@code maximum}.
     */
    public static int Clamp(int value , int minimum , int maximum) { return value < minimum ? minimum : Math.min(value, maximum); }

    /**
     * Normalizes the specified value to the range [0..1].
     * The {@code min} and {@code max} parameters specify the range of the input number.
     * @param v The value to normalize.
     * @param min The minimum inclusive bound of values that the {@code v} parameter can accept.
     * @param max The maximum inclusive bound of values that the {@code v} parameter can accept.
     * @return A value in the range [0..1].
     */
    public static float ToNormalRange(float v, float min, float max) { return Math.abs(v - min) / Math.abs(min - max); }

    /**
     * Normalizes the specified value to the range [0..1].
     * The {@code min} and {@code max} parameters specify the range of the input number.
     * @param v The value to normalize.
     * @param min The minimum inclusive bound of values that the {@code v} parameter can accept.
     * @param max The maximum inclusive bound of values that the {@code v} parameter can accept.
     * @return A value in the range [0..1].
     */
    public static double ToNormalRange(double v, double min, double max) { return Math.abs(v - min) / Math.abs(min - max); }

    public static double MapToRange(double input, double inputlowerbound, double inputupperbound, double outputlowerbound, double outputupperbound)
    {
        return (ToNormalRange(input, inputlowerbound, inputupperbound) * (outputupperbound - outputlowerbound)) + outputlowerbound;
    }

    public static float MapToRange(float input, float inputlowerbound, float inputupperbound, float outputlowerbound, float outputupperbound)
    {
        return (ToNormalRange(input, inputlowerbound, inputupperbound) * (outputupperbound - outputlowerbound)) + outputlowerbound;
    }

    public static double ClampedMapToRange(double input, double inputlowerbound, double inputupperbound, double outputlowerbound, double outputupperbound)
    {
        return (ToNormalRange(input > inputupperbound ? inputupperbound : Math.max(input, inputlowerbound), inputlowerbound, inputupperbound) * (outputupperbound - outputlowerbound)) + outputlowerbound;
    }

    public static float ClampedMapToRange(float input, float inputlowerbound, float inputupperbound, float outputlowerbound, float outputupperbound)
    {
        return (ToNormalRange(input > inputupperbound ? inputupperbound : Math.max(input, inputlowerbound), inputlowerbound, inputupperbound) * (outputupperbound - outputlowerbound)) + outputlowerbound;
    }

}
