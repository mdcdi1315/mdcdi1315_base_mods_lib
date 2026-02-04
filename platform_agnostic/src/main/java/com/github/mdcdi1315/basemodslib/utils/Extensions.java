package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.Extension;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

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
        var values = Direction.values();
        int len = values.length - 1;
        do {
            ret = values[rs.nextIntBetweenInclusive(0 , len)];
        } while (ret == Direction.UP || ret == Direction.DOWN);
        return ret;
    }

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

    public static float Lerp(float delta, float start, float end)
    {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        return start + (delta * (end - start));
    }

    public static double Lerp(double delta, double start, double end)
    {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        return start + (delta * (end - start));
    }

    public static int Floor(float value) {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int Floor(double value) {
        // Borrowed from Minecraft's code, but this is roughly in all cases.
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static float Square(float input) {
        return input * input;
    }

    public static double Square(double input) {
        return input * input;
    }

    public static int Square(int input) {
        return input * input;
    }

    public static float SquareRoot(float value) {
        return (float)Math.sqrt(value);
    }

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
     */
    public static <T> T SelectRandomFromListUnsafe(List<T> elements, RandomSource rs)
    {
        return elements.get(rs.nextIntBetweenInclusive(0 , elements.size()-1));
    }

    /**
     * Gets a random item from the specified list, and returns that item.
     * Additionally, it ensures that the specified item is not selected in any way.
     * @param list The list of items to get a random item from.
     * @param item_to_exclude The item instance to exclude from the possible outcomes.
     * @param source The random source to use for getting the random item.
     * @param comparer The equality comparer to use for testing the objects for equality.
     * @return The random item, ensuring that is not the instance specified in {@code item_to_exclude}.
     * @param <T> The type of the items to select from. An item of such type is returned.
     */
    public static <T> T SelectRandomFromListWithExclusionUnsafe(List<T> list , @MaybeNull T item_to_exclude , IEqualityComparer<T> comparer, RandomSource source)
    {
        T item;
        int size = list.size() - 1;
        if (size == 0) {
            return list.get(0);
        }
        do {
            item = list.get(source.nextIntBetweenInclusive(0, size));
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
     * @throws ArgumentNullException {@code iterable} was {@code null}.
     * @throws AggregateException One or more exceptions occurred while calling {@linkplain T#Dispose()}.
     */
    public static <T extends IDisposable> void DisposeAll(Iterable<T> iterable)
            throws ArgumentNullException, AggregateException
    {
        ArgumentNullException.ThrowIfNull(iterable , "iterable");
        var exceptions = new com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List<com.github.mdcdi1315.DotNetLayer.System.Exception>();
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

    public static int RandomBetweenInclusive(RandomSource rs , int min_inclusive , int max_inclusive)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(rs , "rs");
        return RandomBetweenInclusiveUnsafe(rs , min_inclusive , max_inclusive);
    }

    public static int RandomBetweenInclusiveUnsafe(RandomSource rs , int min_inclusive , int max_inclusive)
    {
        return rs.nextInt(max_inclusive - min_inclusive + 1) + min_inclusive;
    }

    public static float RandomBetween(RandomSource rs, float min_inclusive, float max_exclusive)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(rs , "rs");
        return RandomBetweenUnsafe(rs , min_inclusive , max_exclusive);
    }

    public static double RandomBetween(RandomSource rs, double min_inclusive, double max_exclusive)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(rs , "rs");
        return RandomBetweenUnsafe(rs , min_inclusive , max_exclusive);
    }

    public static float RandomBetweenUnsafe(RandomSource random, float min_inclusive, float max_exclusive) {
        return random.nextFloat() * (max_exclusive - min_inclusive) + min_inclusive;
    }

    public static double RandomBetweenUnsafe(RandomSource random, double min_inclusive, double max_exclusive) {
        return random.nextDouble() * (max_exclusive - min_inclusive) + min_inclusive;
    }

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

    public static double NumberMap(double input , double inputbase , double outputbase)
    {
        return ((input / inputbase) * outputbase);
    }

    public static double Clamp(double value , double minimum , double maximum)
    {
        return value < minimum ? minimum : Math.min(value, maximum);
    }

    public static float Clamp(float value , float minimum , float maximum)
    {
        return value < minimum ? minimum : Math.min(value, maximum);
    }

    public static int Clamp(int value , int minimum , int maximum)
    {
        return value < minimum ? minimum : Math.min(value, maximum);
    }

    public static float ToNormalRange(float v, float min, float max) { return Math.abs(v - min) / Math.abs(min - max); }

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
