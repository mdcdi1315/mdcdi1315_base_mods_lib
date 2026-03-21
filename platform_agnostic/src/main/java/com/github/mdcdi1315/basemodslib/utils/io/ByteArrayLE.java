package com.github.mdcdi1315.basemodslib.utils.io;

import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles;

import java.nio.ByteOrder;

/**
 * Utility methods for packing/unpacking primitive values in/out of byte arrays using {@linkplain ByteOrder#LITTLE_ENDIAN little endian order}. <br />
 * All methods in this class will throw an {@linkplain NullPointerException} if {@code null} is passed in as a method parameter for a byte array. <br />
 * Note: This class has been ported from {@code jdk.internal.util.ByteArrayLittleEndian}, update this as appropriate.
 */
public final class ByteArrayLE
{
    // Do not let anyone be able to instantiate this class.
    private ByteArrayLE() {}

    private static final VarHandle INT, CHAR, LONG, SHORT, FLOAT, DOUBLE;

    private static VarHandle create(Class<?> viewArrayClass) { return MethodHandles.byteArrayViewVarHandle(viewArrayClass, ByteOrder.LITTLE_ENDIAN); }

    static {
        INT = create(int[].class);
        CHAR = create(char[].class);
        LONG = create(long[].class);
        SHORT = create(short[].class);
        FLOAT = create(float[].class);
        DOUBLE = create(double[].class);
    }

    /*
     * Methods for unpacking primitive values from byte arrays starting at
     * a given offset.
     */

    /**
     * {@return a {@code boolean} from the provided {@code array} at the given {@code offset}}.
     *
     * @param array  to read a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 1]
     * @see #SetBoolean(byte[], int, boolean)
     */
    public static boolean GetBoolean(byte[] array, int offset) { return array[offset] != 0; }

    /**
     * {@return a {@code char} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #SetChar(byte[], int, char)
     */
    public static char GetChar(byte[] array, int offset) { return (char) CHAR.get(array, offset); }

    /**
     * {@return a {@code short} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @return a {@code short} from the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #SetShort(byte[], int, short)
     */
    public static short GetShort(byte[] array, int offset) { return (short) SHORT.get(array, offset); }

    /**
     * {@return an {@code unsigned short} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @return an {@code int} representing an unsigned short from the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #SetUnsignedShort(byte[], int, int)
     */
    public static int GetUnsignedShort(byte[] array, int offset) { return Short.toUnsignedInt((short) SHORT.get(array, offset)); }

    /**
     * {@return an {@code int} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 4]
     * @see #SetInt(byte[], int, int)
     */
    public static int GetInt(byte[] array, int offset) { return (int) INT.get(array, offset); }

    /**
     * {@return a {@code float} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 4]
     * @see #SetFloat(byte[], int, float)
     */
    public static float GetFloat(byte[] array, int offset) {
        // Using Float.intBitsToFloat collapses NaN values to a single
        // "canonical" NaN value
        return Float.intBitsToFloat((int) INT.get(array, offset));
    }

    /**
     * {@return a {@code float} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are silently read according
     * to their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 4]
     * @see #SetFloatRaw(byte[], int, float)
     */
    public static float GetFloatRaw(byte[] array, int offset) {
        // Just gets the bits as they are
        return (float) FLOAT.get(array, offset);
    }

    /**
     * {@return a {@code long} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 8]
     * @see #SetLong(byte[], int, long)
     */
    public static long GetLong(byte[] array, int offset) { return (long) LONG.get(array, offset); }

    /**
     * {@return a {@code double} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 8]
     * @see #SetDouble(byte[], int, double)
     */
    public static double GetDouble(byte[] array, int offset) {
        // Using Double.longBitsToDouble collapses NaN values to a single
        // "canonical" NaN value
        return Double.longBitsToDouble((long) LONG.get(array, offset));
    }

    /**
     * {@return a {@code double} from the provided {@code array} at the given {@code offset}
     * using little endian order}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are silently read according to
     * their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to get a value from.
     * @param offset where extraction in the array should begin
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 8]
     * @see #SetDoubleRaw(byte[], int, double)
     */
    public static double GetDoubleRaw(byte[] array, int offset) {
        // Just gets the bits as they are
        return (double) DOUBLE.get(array, offset);
    }

    /*
     * Methods for packing primitive values into byte arrays starting at a given
     * offset.
     */

    /**
     * Sets (writes) the provided {@code value} into
     * the provided {@code array} beginning at the given {@code offset}.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length]
     * @see #GetBoolean(byte[], int)
     */
    public static void SetBoolean(byte[] array, int offset, boolean value) { array[offset] = (byte) (value ? 1 : 0); }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #GetChar(byte[], int)
     */
    public static void SetChar(byte[] array, int offset, char value) { CHAR.set(array, offset, value); }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #GetShort(byte[], int)
     */
    public static void SetShort(byte[] array, int offset, short value) { SHORT.set(array, offset, value); }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #GetUnsignedShort(byte[], int)
     */
    public static void SetUnsignedShort(byte[] array, int offset, int value) { SHORT.set(array, offset, (short) (char) value); }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 4]
     * @see #GetInt(byte[], int)
     */
    public static void SetInt(byte[] array, int offset, int value) { INT.set(array, offset, value); }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #GetFloat(byte[], int)
     */
    public static void SetFloat(byte[] array, int offset, float value) {
        // Using Float.floatToIntBits collapses NaN values to a single
        // "canonical" NaN value
        INT.set(array, offset, Float.floatToIntBits(value));
    }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are silently written according to
     * their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #GetFloatRaw(byte[], int)
     */
    public static void SetFloatRaw(byte[] array, int offset, float value) {
        // Just sets the bits as they are
        FLOAT.set(array, offset, value);
    }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 4]
     * @see #GetLong(byte[], int)
     */
    public static void SetLong(byte[] array, int offset, long value) { LONG.set(array, offset, value); }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #GetDouble(byte[], int)
     */
    public static void SetDouble(byte[] array, int offset, double value) {
        // Using Double.doubleToLongBits collapses NaN values to a single
        // "canonical" NaN value
        LONG.set(array, offset, Double.doubleToLongBits(value));
    }

    /**
     * Sets (writes) the provided {@code value} using little endian order into
     * the provided {@code array} beginning at the given {@code offset}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are silently written according to
     * their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array  to set (write) a value into
     * @param offset where setting (writing) in the array should begin
     * @param value  value to set in the array
     * @throws IndexOutOfBoundsException if the provided {@code offset} is outside
     *                                   the range [0, array.length - 2]
     * @see #GetDoubleRaw(byte[], int)
     */
    public static void SetDoubleRaw(byte[] array, int offset, double value) {
        // Just sets the bits as they are
        DOUBLE.set(array, offset, value);
    }
}
