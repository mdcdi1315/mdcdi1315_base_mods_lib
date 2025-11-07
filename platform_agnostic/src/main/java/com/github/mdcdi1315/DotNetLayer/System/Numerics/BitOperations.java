package com.github.mdcdi1315.DotNetLayer.System.Numerics;

import com.github.mdcdi1315.DotNetLayer.System.CLSCompliant;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

// Some routines inspired by the Stanford Bit Twiddling Hacks by Sean Eron Anderson:
// http://graphics.stanford.edu/~seander/bithacks.html

/**
 * Utility methods for intrinsic bit-twiddling operations.
 */
public final class BitOperations
{
    // Do not let anyone instantiate this class.
    private BitOperations() {}

    /**
     * Evaluate whether a given integral value is a power of 2.
     * @param value The value.
     * @return A value whether {@code value} is a power of 2.
     */
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static boolean IsPow2(int value) { return (value & (value - 1)) == 0 && value > 0; }

    /**
     * Evaluate whether a given integral value is a power of 2.
     * @param value The value.
     * @return A value whether {@code value} is a power of 2.
     */
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static boolean IsPow2(long value) { return (value & (value - 1)) == 0 && value > 0; }

    /**
     * Rotates the specified value left by the specified number of bits. <br />
     * Similar in behavior to the x86 instruction ROL.
     * @param value  The value to rotate.
     * @param offset The number of bits to rotate by. <br />
     * Any value outside the range [0..31] is treated as congruent mod 32.
     * @return The rotated value.
     */
    /*
        [Intrinsic]
    */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static int RotateLeft(int value, int offset) {
        return (value << offset) | (value >> (32 - offset));
    }

    /**
     * Rotates the specified value left by the specified number of bits. <br />
     * Similar in behavior to the x86 instruction ROL.
     * @param value  The value to rotate.
     * @param offset The number of bits to rotate by. <br />
     * Any value outside the range [0..63] is treated as congruent mod 64.
     * @return The rotated value.
     */
    /*
        [Intrinsic]
    */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static long RotateLeft(long value, int offset) {
        return (value << offset) | (value >> (64 - offset));
    }

    /**
     * Rotates the specified value right by the specified number of bits. <br />
     * Similar in behavior to the x86 instruction ROR.
     * @param value  The value to rotate.
     * @param offset The number of bits to rotate by. <br />
     * Any value outside the range [0..31] is treated as congruent mod 32.
     * @return The rotated value.
     */
    /*
     [Intrinsic]
     */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static int RotateRight(int value, int offset) {
        return (value >> offset) | (value << (32 - offset));
    }

    /**
     * Rotates the specified value right by the specified number of bits. <br />
     * Similar in behavior to the x86 instruction ROR.
     * @param value  The value to rotate.
     * @param offset The number of bits to rotate by. <br />
     * Any value outside the range [0..63] is treated as congruent mod 64.
     * @return The rotated value.
     */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static long RotateRight(long value, int offset) {
        return (value >> offset) | (value << (64 - offset));
    }

    /**
     * Round the given integral value up to a power of 2.
     * @param value The value.
     * @return The smallest power of 2 which is greater than or equal to {@code value}.
     * If {@code value} is 0 or the result overflows, returns 0.
     */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static int RoundUpToPowerOf2(int value)
    {
        --value;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    /**
     * Round the given integral value up to a power of 2.
     * @param value The value.
     * @return The smallest power of 2 which is greater than or equal to {@code value}.
     * If {@code value} is 0 or the result overflows, returns 0.
     */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static long RoundUpToPowerOf2(long value)
    {
        --value;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        value |= value >> 32;
        return value + 1;
    }

    /**
     * Returns the population count (number of bits set) of a mask. <br />
     * Similar in behavior to the x86 instruction POPCNT.
     * @param value The value.
     * @return The population count.
     */
    /* [Intrinsic] */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static int PopCount(int value)
    {
        final int c1 = 0x55555555,
                c2 = 0x33333333,
                c3 = 0x0F0F0F0F,
                c4 = 0x01010101;

        value -= (value >> 1) & c1;
        value = (value & c2) + ((value >> 2) & c2);
        value = (((value + (value >> 4)) & c3) * c4) >> 24;

        return value;
    }

    /**
     * Returns the population count (number of bits set) of a mask. <br />
     * Similar in behavior to the x86 instruction POPCNT.
     * @param value The value.
     * @return The population count.
     */
    /* [Intrinsic] */
    @CLSCompliant(IsCompliant = false)
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static int PopCount(long value)
    {
        final long c1 = 6148914691236517205L,
                c2 = 3689348814741910323L,
                c3 = 1085102592571150095L,
                c4 = 72340172838076673L;

        value -= (value >> 1) & c1;
        value = (value & c2) + ((value >> 2) & c2);
        value = (((value + (value >> 4)) & c3) * c4) >> 56;

        return (int)value;
    }
}
