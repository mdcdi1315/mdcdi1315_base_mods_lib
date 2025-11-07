package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.ValueType;
import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.System.Numerics.BitOperations;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.IsReadOnly;

import java.util.Objects;

/**
 * Provides a simple structure that stores Boolean values and small integers in 32 bits of memory.
 */
@ClassIsDotNetStruct
public final class BitVector32
    extends ValueType
{
    private int data;

    /**
     * Initializes a new instance of the {@link BitVector32} structure.
     */
    // As required by ValueType class
    public BitVector32() {
        this.data = 0;
    }

    /**
     * Initializes a new instance of the {@link BitVector32} structure containing the data represented in an integer. <br />
     * This constructor is an O(1) operation.
     * @param data An integer representing the data of the new {@link BitVector32}.
     */
    public BitVector32(int data) {
        this.data = data;
    }

    /**
     * Initializes a new instance of the {@link BitVector32} structure containing the data represented in an existing BitVector32 structure.
     * @param value A {@link BitVector32} structure that contains the data to copy.
     */
    public BitVector32(BitVector32 value) {
        data = value.data;
    }

    /**
     * Gets the state of the bit flag indicated by the specified mask.
     * @param bit A mask that indicates the bit to get.
     * @return {@code true} if the specified bit flag is on (1); otherwise, {@code false}.
     */
    public boolean GetItem(int bit) {
        return (data & bit) == bit;
    }

    /**
     * Sets the state of the bit flag indicated by the specified mask.
     * @param bit A mask that indicates the bit to set.
     * @param value The value to set at {@code bit} mask.
     */
    public void SetItem(int bit, boolean value) {
        if (value) {
            data |= bit;
        } else {
            data &= ~bit;
        }
    }

    /**
     * Gets the value stored in the specified {@link Section}.
     * @param section A {@link Section} that contains the value to get.
     * @return The value stored in the specified {@link Section}.
     */
    public int GetItem(Section section) {
        return (data & (section.mask << section.offset)) >> section.offset;
    }

    /**
     * Sets the value stored in the specified {@link Section}.
     * @param section A {@link Section} that contains the value to set.
     * @param value The actual value.
     */
    public void SetItem(Section section, int value)
    {
        // The code should really have originally validated "(value & section.Mask) == value" with
        // an exception (it instead validated it with a Debug.Assert, which does little good in a
        // public method when in a Release build).  We don't include such a check now as it would
        // likely break things and for little benefit.

        value <<= section.offset;
        int offsetMask = (0xFFFF & (int)section.mask) << section.offset;
        data = (data & ~offsetMask) | (value & offsetMask);
    }

    /**
     * Gets the value of the {@link BitVector32} as an integer.
     * @return The value of the {@link BitVector32} as an integer.
     */
    public int GetData() {
        return data;
    }

    /**
     * Creates a series of masks that can be used to retrieve individual bits in a {@link BitVector32} that is set up as bit flags.
     * @return A mask that isolates the first bit flag in the {@link BitVector32}.
     */
    public static int CreateMask()
    {
        return CreateMask(0);
    }

    /**
     * Creates an additional mask following the specified mask in a series of masks that can be used to retrieve individual bits in a {@link BitVector32} that is set up as bit flags.
     * @param previous The mask that indicates the previous bit flag.
     * @return A mask that isolates the bit flag following the one that {@code previous} points to in {@link BitVector32}.
     * @throws InvalidOperationException {@code previous} indicates the last bit flag in the {@link BitVector32}.
     */
    public static int CreateMask(int previous)
            throws InvalidOperationException
    {
        if (previous == 0)
        {
            return 1;
        }

        if (previous == 0x80000000) {
            throw new InvalidOperationException("This bit vector is full!");
        }

        return previous << 1;
    }

    /**
     * Determines whether the specified object is equal to the {@link BitVector32}.
     * @param obj The object to compare with the current {@link BitVector32}.
     * @return {@code true} if the specified object is equal to the {@link BitVector32}; otherwise, {@code false}.
     */
    public boolean Equals(Object obj) {
        return obj instanceof BitVector32 other && Equals(other);
    }

    /**
     * Indicates whether the current instance is equal to another instance of the same type.
     * @param other An instance to compare with this instance.
     * @return {@code true} if the current instance is equal to the {@code other} instance; otherwise, {@code false}.
     */
    public boolean Equals(BitVector32 other) {
        return other != null && data == other.data;
    }

    /**
     * Creates the first section in a series, with the specified maximum value.
     * @param maxValue The maximum value of the section.
     * @return The created section.
     */
    public static Section CreateSection(short maxValue) {
        return CreateSectionHelper(maxValue, (short) 0, (short) 0);
    }

    /**
     * Creates the first section in a series, with the specified maximum value.
     * @param maxValue The maximum value of the section.
     * @param previous The previous section.
     * @return The created section.
     */
    public static Section CreateSection(short maxValue, Section previous) {
        return CreateSectionHelper(maxValue, previous.mask, previous.offset);
    }

    private static Section CreateSectionHelper(short maxValue, short priorMask, short priorOffset)
    {
        if (maxValue < 1) {
            throw new ArgumentOutOfRangeException("maxValue", "The maximum value must not be negative or zero.");
        }

        short offset = (short)(priorOffset + BitOperations.PopCount(priorMask));
        if (offset > 31) {
            throw new InvalidOperationException("The bit vector is full!!");
        }

        return new Section((short)(BitOperations.RoundUpToPowerOf2(maxValue) - 1), offset);
    }

    /**
     * Returns a string that represents the specified {@link BitVector32}.
     * @param value The {@link BitVector32} to represent.
     * @return A string that represents the specified {@link BitVector32}.
     */
    public static String ToString(BitVector32 value)
    {
        int locdata = value.data;
        StringBuilder sb = new StringBuilder("BitVector32{");
        for (int i = 0; i < 32; i++)
        {
            sb.append((locdata & 0x80000000) != 0 ? '1' : '0');
            locdata <<= 1;
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Returns a string that represents the current {@link BitVector32}.
     * @return A string that represents the current {@link BitVector32}.
     */
    public String ToString() {
        return ToString(this);
    }

    /**
     * Represents a section of the vector that can contain an integer number.
     */
    @IsReadOnly
    @ClassIsDotNetStruct
    public static final class Section
        extends ValueType
    {
        private final short mask ,offset;

        /**
         * Initializes a new instance of the {@link Section} structure.
         */
        // As required by ValueType class
        public Section() {
            mask = offset = 0;
        }

        private Section(short mask , short offset) {
            this.mask = mask;
            this.offset = offset;
        }

        /**
         * Gets a mask that isolates this section within the {@link BitVector32}.
         * @return A mask that isolates this section within the {@link BitVector32}.
         */
        public short GetMask() {
            return mask;
        }

        /**
         * Gets the offset of this section from the start of the {@link BitVector32}.
         * @return The offset of this section from the start of the {@link BitVector32}.
         */
        public short GetOffset() {
            return offset;
        }

        public boolean Equals(Object obj) {
            return obj instanceof Section sec && Equals(sec);
        }

        /**
         * Determines whether the specified {@link Section} object is the same as the current {@link Section} object.
         * @param obj The {@link Section} object to compare with the current {@link Section} object.
         * @return {@code true} if the {@code obj} parameter is the same as the current {@link Section} object; otherwise {@code false}.
         */
        public boolean Equals(Section obj) {
            return obj != null && obj.mask == mask && obj.offset == offset;
        }

        /**
         * Serves as a hash function for the current {@link Section}, suitable for hashing algorithms and data structures, such as a hash table.
         * @return A hash code for the current {@link Section}.
         */
        @Override
        public int GetHashCode() {
            return Objects.hash(mask , offset);
        }

        /**
         * Returns a string that represents the current {@link Section}.
         * @return A string that represents the current {@link Section}.
         */
        @Override
        public String ToString() {
            return ToString(this);
        }

        /**
         * Returns a string that represents the specified {@link Section}.
         * @param value The {@link Section} to represent.
         * @return A string that represents the specified {@link Section}.
         */
        public static String ToString(Section value) {
            // $"Section{{0x{value.Mask:x}, 0x{value.Offset:x}}}"
            return String.format("Section{0x%h, 0x%h}" , value.mask , value.offset);
        }
    }
}
