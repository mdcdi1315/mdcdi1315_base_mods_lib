package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Func4;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import io.netty.buffer.ByteBuf;

import net.minecraft.core.Vec3i;
import net.minecraft.core.Position;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Networking helpers.
 */
public final class NetworkHelpers
{
    // Do not let anyone be able to instantiate this class.
    private NetworkHelpers() {}

    /**
     * Packs the specified version number into an integer. <br />
     * All components cannot be larger than 0xFF or 255; if they are the method will not throw an exception, but it will wrap them around.
     * @param v The {@link Version} to pack into an integer.
     * @return The packed version.
     * @throws ArgumentNullException {@code v} is {@code null}.
     */
    public static int PackVersion(Version v)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(v, "v");
        int build = v.Build();
        build = (build < 0) ? 0 : build & 0xFF;
        int revision = v.Revision();
        revision = (revision < 0) ? 0 : revision & 0xFF;
        return (v.Major() & 0xFF) << 24 | (v.Minor() & 0xFF) << 16 | build << 8 | revision;
    }

    /**
     * Unpacks a previously encoded {@link Version} value using the specified arbitrary integer value.
     * @param value The packed value to decode into a {@link Version} instance.
     * @return The unpacked version, previously packed with {@link #PackVersion(Version)}.
     */
    @NotNull
    public static Version UnpackVersion(int value) {
        return new Version((value >> 24) & 0xFF , (value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF);
    }

    /**
     * Writes a 7-bit encoded integer to the specified byte buffer.
     * @param buffer The byte buffer where to store the specified value as a 7-bit encoded integer.
     * @param value The value to encode as a 7-bit integer.
     * @apiNote This is the unsafe method variant of {@link #Write7BitEncodedInt(ByteBuf, int)} method.
     * @since 1.0.18
     */
    public static void Write7BitEncodedIntUnsafe(ByteBuf buffer, int value)
    {
        long num;
        for (num = (value & 0xFFFFFFFFL); num >= 0x7FL; num >>= 7L) {
            buffer.writeByte((int)((num | 0x80L) & 0xFFL));
        }
        buffer.writeByte((int) num);
    }

    /**
     * Writes a 7-bit encoded integer to the specified byte buffer.
     * @param buffer The byte buffer where to store the specified value as a 7-bit encoded integer.
     * @param value The value to encode as a 7-bit integer.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     * @since 1.0.18
     */
    public static void Write7BitEncodedInt(ByteBuf buffer, int value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        Write7BitEncodedIntUnsafe(buffer, value);
    }

    /**
     * Reads a 7-bit encoded integer from the specified byte buffer.
     * @param buffer The byte buffer where to read the stored 7-bit encoded integer from.
     * @return The read 7-bit encoded integer.
     * @throws FormatException Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @apiNote This is the unsafe method variant of {@link #Read7BitEncodedInt(ByteBuf)} method.
     * @since 1.0.18
     */
    public static int Read7BitEncodedIntUnsafe(ByteBuf buffer)
            throws FormatException
    {
        int num = 0, bits = 0;
        byte b;
        do {
            if (bits == 35) {
                throw new FormatException("Too many bytes of what should have been a 7-bit encoded Integer.");
            }
            b = buffer.readByte();
            num |= (b & 0x7F) << bits;
            bits += 7;
        } while ((b & 0x80) != 0);
        return num;
    }

    /**
     * Reads a 7-bit encoded integer from the specified byte buffer.
     * @param buffer The byte buffer where to read the stored 7-bit encoded integer from.
     * @return The read 7-bit encoded integer.
     * @throws FormatException Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     * @since 1.0.18
     */
    public static int Read7BitEncodedInt(ByteBuf buffer)
            throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        return Read7BitEncodedIntUnsafe(buffer);
    }

    /**
     * Unsafely writes a block position to the specified byte buffer.
     * @param buffer The byte buffer where to write the specified {@link Vec3i}.
     * @param pos The block position to be stored.
     * @apiNote This is the unsafe method variant of {@link #WriteVec3i(ByteBuf, Vec3i)} method.
     * @since 1.0.18
     */
    public static void WriteVec3iUnsafe(ByteBuf buffer, Vec3i pos)
    {
        Write7BitEncodedIntUnsafe(buffer, pos.getX());
        Write7BitEncodedIntUnsafe(buffer, pos.getY());
        Write7BitEncodedIntUnsafe(buffer, pos.getZ());
    }

    /**
     * Unsafely writes a block position to the specified byte buffer.
     * @param buffer The byte buffer where to write the specified block position.
     * @param x The x-coordinate of the block position to be stored.
     * @param y The y-coordinate of the block position to be stored.
     * @param z The z-coordinate of the block position to be stored.
     * @apiNote This is the unsafe method variant of {@link #WriteVec3i(ByteBuf, int, int, int)} method.
     * @since 1.0.18
     */
    public static void WriteVec3iUnsafe(ByteBuf buffer, int x, int y, int z)
    {
        Write7BitEncodedIntUnsafe(buffer, x);
        Write7BitEncodedIntUnsafe(buffer, y);
        Write7BitEncodedIntUnsafe(buffer, z);
    }

    /**
     * Writes a block position to the specified byte buffer.
     * @param buffer The byte buffer where to write the specified {@link Vec3i}.
     * @param pos The block position to be stored.
     * @throws ArgumentNullException {@code buffer} and/or {@code pos} are {@code null}.
     * @since 1.0.18
     */
    public static void WriteVec3i(ByteBuf buffer, Vec3i pos)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(pos, "pos");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        WriteVec3iUnsafe(buffer, pos);
    }

    /**
     * Writes a block position to the specified byte buffer.
     * @param buffer The byte buffer where to write the specified block position.
     * @param x The x-coordinate of the block position to be stored.
     * @param y The y-coordinate of the block position to be stored.
     * @param z The z-coordinate of the block position to be stored.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     * @since 1.0.18
     */
    public static void WriteVec3i(ByteBuf buffer, int x, int y, int z)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        WriteVec3iUnsafe(buffer, x, y, z);
    }

    /**
     * Reads an enumeration constant from a byte buffer.
     * @param buffer The byte buffer where to read the enumeration constant from.
     * @param enumClass The enumeration class that provides the constant values.
     * @return The read enumeration constant.
     * @param <T> The type of the enumeration class that its constant will be read.
     * @implNote For reading the stored enumeration constant, it's ordinal is stored via the {@link #WriteEnumUnsafe(ByteBuf, Enum)} method. <br />
     * As such, reordering enumeration constants can break this method. <br />
     * However, it does store the value very efficiently since it just stores over the ordinal into a packed 7-bit encoded integer.
     * @apiNote This is the unsafe method variant of {@link #ReadEnum(ByteBuf, Class)} method.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @since 1.0.18
     */
    public static <T extends Enum<T>> T ReadEnumUnsafe(ByteBuf buffer, Class<T> enumClass) throws FormatException { return enumClass.getEnumConstants()[Read7BitEncodedIntUnsafe(buffer)]; }

    /**
     * Reads an enumeration constant from a byte buffer.
     * @param buffer The byte buffer where to read the enumeration constant from.
     * @param enumClass The enumeration class that provides the constant values.
     * @return The read enumeration constant.
     * @param <T> The type of the enumeration class that its constant will be read.
     * @implNote For reading the stored enumeration constant, it's ordinal is stored via the {@link #WriteEnumUnsafe(ByteBuf, Enum)} method. <br />
     * As such, reordering enumeration constants can break this method. <br />
     * However, it does store the value very efficiently since it just stores over the ordinal into a packed 7-bit encoded integer.
     * @throws ArgumentNullException {@code buffer} and/or {@code enumClass} are {@code null}.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @since 1.0.18
     */
    public static <T extends Enum<T>> T ReadEnum(ByteBuf buffer, Class<T> enumClass)
            throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(enumClass, "enumClass");
        return ReadEnumUnsafe(buffer, enumClass);
    }

    /**
     * Writes an enumeration constant to a byte buffer.
     * @param buffer The byte buffer where to write the enumeration constant to.
     * @param value The enumeration constant to write.
     * @param <T> The type of the enumeration class that its constant will be written.
     * @implNote This method does store the ordinal of the enumeration constant that is requested to be stored. <br />
     * However, reordering enumeration constants can break this method, and reading with different versions of the enumeration class can cause unexpected issues. <br />
     * Typically, during a network protocol run the classes do not change, except of different versions of your mod where the class is changed. <br />
     * As such, use this only when you expect that the given enumeration type won't ever be changed. (Or you could guard it by the network protocol version)
     * @apiNote This is the unsafe method variant of {@link #WriteEnum(ByteBuf, Enum)} method.
     * @since 1.0.18
     */
    public static <T extends Enum<T>> void WriteEnumUnsafe(ByteBuf buffer, T value) { Write7BitEncodedIntUnsafe(buffer, value.ordinal()); }

    /**
     * Writes an enumeration constant to a byte buffer.
     * @param buffer The byte buffer where to write the enumeration constant to.
     * @param value The enumeration constant to write.
     * @param <T> The type of the enumeration class that its constant will be written.
     * @implNote This method does store the ordinal of the enumeration constant that is requested to be stored. <br />
     * However, reordering enumeration constants can break this method, and reading with different versions of the enumeration class can cause unexpected issues. <br />
     * Typically, during a network protocol run the classes do not change, except of different versions of your mod where the class is changed. <br />
     * As such, use this only when you expect that the given enumeration type won't ever be changed. (Or you could guard it by the network protocol version)
     * @throws ArgumentNullException {@code buffer} and/or {@code value} are {@code null}.
     * @since 1.0.18
     */
    public static <T extends Enum<T>> void WriteEnum(ByteBuf buffer, T value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        WriteEnumUnsafe(buffer, value);
    }

    /**
     * Unsafely reads a user-defined derived {@link Vec3i} class from the specified byte buffer. <br />
     * The function to specify is possibly the constructor of the derived {@link Vec3i} class.
     * @param buffer The byte buffer where to read from the specified derived {@link Vec3i} class.
     * @param func The function to read the derived {@link Vec3i} class.
     * @return The derived {@link Vec3i} object constructed by {@code func}.
     * @param <T> The type of the derived {@link Vec3i} class to read as.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @apiNote This is the unsafe method variant of {@link #ReadDerivedVec3i(ByteBuf, Func4)} method.
     * @since 1.0.18
     */
    public static <T extends Vec3i> T ReadDerivedVec3iUnsafe(ByteBuf buffer, Func4<Integer, Integer, Integer, T> func)
            throws FormatException
    {
        int x = Read7BitEncodedIntUnsafe(buffer);
        int y = Read7BitEncodedIntUnsafe(buffer);
        int z = Read7BitEncodedIntUnsafe(buffer);
        return func.function(x, y, z);
    }

    /**
     * Reads a user-defined derived {@link Vec3i} class from the specified byte buffer. <br />
     * The function to specify is possibly the constructor of the derived {@link Vec3i} class.
     * @param buffer The byte buffer where to read from the specified derived {@link Vec3i} class.
     * @param func The function to read the derived {@link Vec3i} class.
     * @return The derived {@link Vec3i} object constructed by {@code func}.
     * @param <T> The type of the derived {@link Vec3i} class to read as.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @throws ArgumentNullException {@code buffer} and/or {@code func} are {@code null}.
     * @since 1.0.18
     */
    public static <T extends Vec3i> T ReadDerivedVec3i(ByteBuf buffer, Func4<Integer, Integer, Integer, T> func)
    {
        ArgumentNullException.ThrowIfNull(func, "func");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        return ReadDerivedVec3iUnsafe(buffer, func);
    }

    /**
     * Specialization of the {@link #ReadDerivedVec3iUnsafe(ByteBuf, Func4)} method for the {@link BlockPos} class.
     * @param buffer The byte buffer where to read the {@link BlockPos} from.
     * @return The read {@link BlockPos}.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @apiNote This is the unsafe method variant of {@link #ReadBlockPos(ByteBuf)} method.
     * @since 1.0.18
     */
    public static BlockPos ReadBlockPosUnsafe(ByteBuf buffer)
            throws FormatException
    {
        int x = Read7BitEncodedIntUnsafe(buffer);
        int y = Read7BitEncodedIntUnsafe(buffer);
        int z = Read7BitEncodedIntUnsafe(buffer);
        return (x == 0 && y == 0 && z == 0) ? BlockPos.ZERO : new BlockPos(x, y, z);
    }

    /**
     * Specialization of the {@link #ReadDerivedVec3i(ByteBuf, Func4)} method for the {@link BlockPos} class.
     * @param buffer The byte buffer where to read the {@link BlockPos} from.
     * @return The read {@link BlockPos}.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @since 1.0.18
     */
    public static BlockPos ReadBlockPos(ByteBuf buffer)
            throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        return ReadBlockPosUnsafe(buffer);
    }

    /**
     * Reads a {@link Vec3i} class instance from the specified byte buffer.
     * @param buffer The byte buffer to read the {@link Vec3i} from.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @apiNote This is the unsafe method variant of {@link #ReadVec3i(ByteBuf)} method.
     * @return The read {@link Vec3i} instance.
     * @since 1.0.18
     */
    public static Vec3i ReadVec3iUnsafe(ByteBuf buffer)
            throws FormatException
    {
        int x = Read7BitEncodedIntUnsafe(buffer);
        int y = Read7BitEncodedIntUnsafe(buffer);
        int z = Read7BitEncodedIntUnsafe(buffer);
        return (x == 0 && y == 0 && z == 0) ? Vec3i.ZERO : new Vec3i(x, y, z);
    }

    /**
     * Reads a {@link Vec3i} class instance from the specified byte buffer.
     * @param buffer The byte buffer to read the {@link Vec3i} from.
     * @throws FormatException [Forwarded from {@link #Read7BitEncodedIntUnsafe(ByteBuf)}] Attempted to read more than 5 bytes from the {@link ByteBuf}.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     * @return The read {@link Vec3i} instance.
     * @since 1.0.18
     */
    public static Vec3i ReadVec3i(ByteBuf buffer)
            throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        return ReadVec3iUnsafe(buffer);
    }

    /**
     * Writes a {@link Position} to the specified byte buffer.
     * @param buffer The byte buffer to write the current {@link Position} to.
     * @param pos The position to write to {@code buffer}.
     * @apiNote This is the unsafe method variant of {@link #WritePosition(ByteBuf, Position)} method.
     * @since 1.0.18
     */
    public static void WritePositionUnsafe(ByteBuf buffer, Position pos)
    {
        buffer.writeDoubleLE(pos.x());
        buffer.writeDoubleLE(pos.y());
        buffer.writeDoubleLE(pos.z());
    }

    /**
     * Writes a {@link Position} to the specified byte buffer.
     * @param buffer The byte buffer to write the current {@link Position} to.
     * @param pos The position to write to {@code buffer}.
     * @throws ArgumentNullException {@code buffer} and/or {@code pos} are {@code null}.
     * @since 1.0.18
     */
    public static void WritePosition(ByteBuf buffer, Position pos)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(pos, "pos");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        WritePositionUnsafe(buffer, pos);
    }

    /**
     * Reads a user-defined derived {@link Position} class from the specified byte buffer. <br />
     * The function to specify is possibly the constructor of the derived {@link Position} class.
     * @param buffer The byte buffer where to read from the specified derived {@link Position} class.
     * @param func The function to read the derived {@link Position} class.
     * @return The derived {@link Position} object constructed by {@code func}.
     * @param <T> The type of the derived {@link Position} class to read as.
     * @apiNote This is the unsafe method variant of {@link #ReadDerivedPosition(ByteBuf, Func4)} method.
     * @since 1.0.18
     */
    public static <T extends Position> T ReadDerivedPositionUnsafe(ByteBuf buffer, Func4<Double, Double, Double, T> func)
    {
        double x = buffer.readDoubleLE();
        double y = buffer.readDoubleLE();
        double z = buffer.readDoubleLE();
        return func.function(x, y, z);
    }

    /**
     * Reads a user-defined derived {@link Position} class from the specified byte buffer. <br />
     * The function to specify is possibly the constructor of the derived {@link Position} class.
     * @param buffer The byte buffer where to read from the specified derived {@link Position} class.
     * @param func The function to read the derived {@link Position} class.
     * @return The derived {@link Position} object constructed by {@code func}.
     * @param <T> The type of the derived {@link Position} class to read as.
     * @throws ArgumentNullException {@code buffer} and/or {@code func} are {@code null}.
     * @since 1.0.18
     */
    public static <T extends Position> T ReadDerivedPosition(ByteBuf buffer, Func4<Double, Double, Double, T> func)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(func, "func");
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        return ReadDerivedPositionUnsafe(buffer, func);
    }

    /**
     * Reads a {@link Vec3} class instance from the specified byte buffer.
     * @param buffer The byte buffer to read the {@link Vec3} from.
     * @return The read {@link Vec3} instance.
     * @apiNote This is the unsafe method variant of {@link #ReadVec3(ByteBuf)} method.
     * @since 1.0.18
     */
    public static Vec3 ReadVec3Unsafe(ByteBuf buffer)
    {
        double x = buffer.readDoubleLE();
        double y = buffer.readDoubleLE();
        double z = buffer.readDoubleLE();
        return (x == 0.0d && y == 0.0d && z == 0.0d) ? Vec3.ZERO : new Vec3(x, y, z);
    }

    /**
     * Reads a {@link Vec3} class instance from the specified byte buffer.
     * @param buffer The byte buffer to read the {@link Vec3} from.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     * @return The read {@link Vec3} instance.
     * @since 1.0.18
     */
    public static Vec3 ReadVec3(ByteBuf buffer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        return ReadVec3Unsafe(buffer);
    }

    /**
     * Writes a {@link Vec2} to the specified byte buffer.
     * @param buffer The byte buffer to write the {@link Vec2} to.
     * @param vec2 The two-component vector to write to the specified byte buffer.
     * @apiNote This is the unsafe method variant of {@link #WriteVec2(ByteBuf, Vec2)} method.
     * @since 1.0.20
     */
    public static void WriteVec2Unsafe(ByteBuf buffer, Vec2 vec2)
    {
        buffer.writeFloatLE(vec2.x);
        buffer.writeFloatLE(vec2.y);
    }

    /**
     * Writes a {@link Vec2} to the specified byte buffer.
     * @param buffer The byte buffer to write the {@link Vec2} to.
     * @param vec2 The two-component vector to write to the specified byte buffer.
     * @throws ArgumentNullException {@code buffer} and/or {@code vec2} are {@code null}.
     * @since 1.0.20
     */
    public static void WriteVec2(ByteBuf buffer, Vec2 vec2)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        ArgumentNullException.ThrowIfNull(vec2, "vec2");
        WriteVec2Unsafe(buffer, vec2);
    }

    /**
     * Reads a {@link Vec2} class instance from the specified byte buffer.
     * @param buffer The byte buffer to read the {@link Vec2} from.
     * @return The read {@link Vec2} instance.
     * @apiNote This is the unsafe method variant of {@link #ReadVec2(ByteBuf)} method.
     * @since 1.0.20
     */
    public static Vec2 ReadVec2Unsafe(ByteBuf buffer)
    {
        float x = buffer.readFloatLE();
        float y = buffer.readFloatLE();
        if (x == 0.0f && y == 0.0f) {
            return Vec2.ZERO;
        } else if (x == 1.0f && y == 1.0f) {
            return Vec2.ONE;
        } else {
            return new Vec2(x, y);
        }
    }

    /**
     * Reads a {@link Vec2} class instance from the specified byte buffer.
     * @param buffer The byte buffer to read the {@link Vec2} from.
     * @return The read {@link Vec2} instance.
     * @throws ArgumentNullException {@code buffer} is {@code null}.
     * @since 1.0.20
     */
    public static Vec2 ReadVec2(ByteBuf buffer)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(buffer, "buffer");
        return ReadVec2Unsafe(buffer);
    }
}
