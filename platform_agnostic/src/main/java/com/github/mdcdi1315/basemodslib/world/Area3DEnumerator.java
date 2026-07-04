package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

import net.minecraft.core.BlockPos;

/**
 * An improved version of the Minecraft's Cursor3D class. <br />
 * It implements the {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator} interface. <br />
 * @implNote This class solves these issues that the Cursor3D currently has (as of 1.21.1):
 * <ol>
 *     <li>It fails to process large areas. If {@code width * height * depth} overflows {@link Integer#MAX_VALUE}, the entire area won't be enumerated.</li>
 *     <li>It does not produce directly {@link BlockPos} instances, you have to create them yourself.</li>
 *     <li>It does not implement any iterator/iterable interface.</li>
 * </ol>
 * @since 1.0.34
 */
public final class Area3DEnumerator
    extends BaseEnumerator<BlockPos>
{
    private int x, y, z;

    private final int end_x, end_y, end_z;
    private final int origin_x, origin_y, origin_z;

    /**
     * Constructs a new instance of the {@link Area3DEnumerator} class by specifying the origin X,Y,Z and last block X,Y,Z values.
     * @param origin_x The X-coordinate of the origin position.
     * @param origin_y The Y-coordinate of the origin position.
     * @param origin_z The Z-coordinate of the origin position.
     * @param end_x The X-coordinate of the end position.
     * @param end_y The Y-coordinate of the end position.
     * @param end_z The Z-coordinate of the end position.
     * @throws ArgumentOutOfRangeException
     *      {@code end_x} is less than {@code origin_x} and/or
     *      {@code end_y} is less than {@code origin_y} and/or
     *      {@code end_z} is less than {@code origin_z}.
     */
    public Area3DEnumerator(int origin_x, int origin_y, int origin_z, int end_x, int end_y, int end_z)
            throws ArgumentOutOfRangeException
    {
        if (end_x < origin_x) {
            throw new ArgumentOutOfRangeException("end_x", "Ending X point cannot be less than X origin point.");
        } else if (end_y < origin_y) {
            throw new ArgumentOutOfRangeException("end_y", "Ending Y point cannot be less than Y origin point.");
        } else if (end_z < origin_z) {
            throw new ArgumentOutOfRangeException("end_z", "Ending Z point cannot be less than Z origin point.");
        } else {
            this.origin_x = origin_x;
            this.origin_y = origin_y;
            this.origin_z = origin_z;
            this.end_x = end_x;
            this.end_y = end_y;
            this.end_z = end_z;
            InitCursor();
        }
    }

    /**
     * Constructs a new instance of the {@link Area3DEnumerator} class by specifying the origin X,Y,Z and last block X,Y,Z values.
     * @param origin The origin position.
     * @param end The end position.
     * @throws ArgumentNullException {@code origin} and/or {@code end} are {@code null}.
     * @throws ArgumentOutOfRangeException {@code end} represents a position that is before {@code origin}.
     * See notes of this exception on {@link #Area3DEnumerator(int, int, int, int, int, int)} constructor.
     */
    public Area3DEnumerator(BlockPos origin, BlockPos end)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(end, "end");
        ArgumentNullException.ThrowIfNull(origin, "origin");
        this.origin_x = origin.getX();
        this.origin_y = origin.getY();
        this.origin_z = origin.getZ();
        this.end_x = end.getX();
        this.end_y = end.getY();
        this.end_z = end.getZ();
        if (end_x < origin_x) {
            throw new ArgumentOutOfRangeException("end_x", "Ending X point cannot be less than X origin point.");
        } else if (end_y < origin_y) {
            throw new ArgumentOutOfRangeException("end_y", "Ending Y point cannot be less than Y origin point.");
        } else if (end_z < origin_z) {
            throw new ArgumentOutOfRangeException("end_z", "Ending Z point cannot be less than Z origin point.");
        } else {
            InitCursor();
        }
    }

    /**
     * Constructs a new instance of the {@link Area3DEnumerator} class by specifying the origin X,Y,Z and the desired width/height/depth.
     * @param origin_x The X-coordinate of the origin position.
     * @param origin_y The Y-coordinate of the origin position.
     * @param origin_z The Z-coordinate of the origin position.
     * @param width The width of the area to be enumerated.
     * @param height The height of the area to be enumerated.
     * @param depth The depth of the area to be enumerated.
     * @return A new {@link Area3DEnumerator} instance.
     * @throws ArgumentOutOfRangeException {@code width} and/or {@code height} and/or {@code depth} are less than 0.
     */
    @NotNull
    public static Area3DEnumerator FromWidthHeightDepth(int origin_x, int origin_y, int origin_z, int width, int height, int depth)
            throws ArgumentOutOfRangeException
    {
        if (width < 0) {
            throw new ArgumentOutOfRangeException("width", "Width must be greater than or equal to 0");
        } else if (height < 0) {
            throw new ArgumentOutOfRangeException("height", "Height must be greater than or equal to 0");
        } else if (depth < 0) {
            throw new ArgumentOutOfRangeException("depth", "Depth must be greater than or equal to 0");
        } else {
            return new Area3DEnumerator(
                    origin_x, origin_y, origin_z,
                    origin_x + width,
                    origin_y + height,
                    origin_z + depth
            );
        }
    }

    /**
     * Constructs a new instance of the {@link Area3DEnumerator} class by specifying the origin X,Y,Z and the desired width/height.
     * @param origin_x The X-coordinate of the origin position.
     * @param origin_y The Y-coordinate of the origin position.
     * @param origin_z The Z-coordinate of the origin position.
     * @param width The width of the area to be enumerated.
     * @param height The height of the area to be enumerated.
     * @return A new {@link Area3DEnumerator} instance.
     * @apiNote The depth value is directly assigned to the same value as {@code width}.
     * @throws ArgumentOutOfRangeException {@code width} and/or {@code height} are less than 0.
     */
    @NotNull
    public static Area3DEnumerator FromWidthAndHeight(int origin_x, int origin_y, int origin_z, int width, int height)
            throws ArgumentOutOfRangeException
    {
        return FromWidthHeightDepth(origin_x, origin_y, origin_z, width, height, width);
    }

    /**
     * Constructs a new instance of the {@link Area3DEnumerator} class by specifying the origin X,Y,Z and the desired width/height/depth.
     * @param origin The origin position.
     * @param width The width of the area to be enumerated.
     * @param height The height of the area to be enumerated.
     * @param depth The depth of the area to be enumerated.
     * @return A new {@link Area3DEnumerator} instance.
     * @throws ArgumentNullException {@code origin} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code width} and/or {@code height} and/or {@code depth} are less than 0.
     */
    @NotNull
    public static Area3DEnumerator FromWidthHeightDepth(BlockPos origin, int width, int height, int depth)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(origin, "origin");
        return FromWidthHeightDepth(origin.getX(), origin.getY(), origin.getZ(), width, height, depth);
    }

    /**
     * Constructs a new instance of the {@link Area3DEnumerator} class by specifying the origin X,Y,Z and the desired width/height.
     * @param origin The origin position.
     * @param width The width of the area to be enumerated.
     * @param height The height of the area to be enumerated.
     * @return A new {@link Area3DEnumerator} instance.
     * @apiNote The depth value is directly assigned to the same value as {@code width}.
     * @throws ArgumentNullException {@code origin} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code width} and/or {@code height} are less than 0.
     */
    @NotNull
    public static Area3DEnumerator FromWidthAndHeight(BlockPos origin, int width, int height) throws ArgumentNullException, ArgumentOutOfRangeException { return FromWidthHeightDepth(origin, width, height, width); }

    /**
     * Provides constants around the kind/type of the current cursor position.
     */
    public enum PositionType
        implements ISynchronized
    {
        /**
         * Position is inside the 3D area.
         */
        INSIDE,
        /**
         * Position is a face of the 3D area.
         */
        FACE,
        /**
         * Position is an edge of the 3D area.
         */
        EDGE,
        /**
         * Position is one of the four corners of the 3D area.
         */
        CORNER
    }

    @Pure
    private void InitCursor()
    {
        this.x = origin_x - 1;
        this.y = origin_y;
        this.z = origin_z;
    }

    /**
     * Gets the width of the enumerating 3D area.
     * @return The width.
     */
    @Pure
    public int GetWidth() { return end_x - origin_x; }

    /**
     * Gets the depth of the enumerating 3D area.
     * @return The depth.
     */
    @Pure
    public int GetDepth() { return end_z - origin_z; }

    /**
     * Gets the height of the enumerating 3D area.
     * @return The height.
     */
    @Pure
    public int GetHeight() { return end_y - origin_y; }

    /**
     * Gets the total number of blocks contained in the currently enumerating area.
     * @return The total number of blocks inside the current area.
     * @apiNote The return value is {@code long} because
     * {@code width * height * depth} could overflow if was defined as {@code int}.
     */
    @Pure
    public long GetTotalBlocks()
    {
        long width = GetWidth();
        long depth = GetDepth();
        long height = GetHeight();

        // There is the possibility that any of the three X,Y,Z components are zero.
        // So, appropriately handle them with the below code.
        if (height == 0L) {
            return (depth == 0L) ? width : (((width == 0L) ? 1L : width) * depth);
        } else if (width == 0L) {
            return (depth == 0L) ? height : height * depth;
        } else if (depth == 0L) {
            return height * width;
        } else {
            return width * height * depth;
        }
    }

    /**
     * Gets the total number of blocks contained in the currently enumerating area.
     * @return The total number of blocks inside the current area.
     * @apiNote If the total number of blocks exceed the {@link Integer#MAX_VALUE}
     * value, this method returns {@link Integer#MAX_VALUE}.
     */
    @Pure
    public int GetTotalBlocksAsInt() { return (int)Extensions.Min(GetTotalBlocks(), Integer.MAX_VALUE); }

    /**
     * Gets the last block position in the 3D area.
     * @return The last block position.
     */
    @Pure
    @NotNull
    public BlockPos GetEnd() { return new BlockPos(end_x, end_y, end_z); }

    /**
     * Gets the first block position in the 3D area.
     * @return The first block position.
     */
    @Pure
    @NotNull
    public BlockPos GetOrigin() { return new BlockPos(origin_x, origin_y, origin_z); }

    /**
     * Gets the type of the current position.
     * @return The type of the current position.
     */
    @Pure
    @NotNull
    public PositionType GetPositionType()
    {
        int type = 0;

        if (x == origin_x || x == end_x) { type++; }

        if (y == origin_y || y == end_y) { type++; }

        if (z == origin_z || z == end_z) { type++; }

        return PositionType.values()[type];
    }

    /**
     * Gets the current position of the cursor.
     * @return A {@link BlockPos} representing the cursor's position.
     */
    @NotNull
    @Override
    public BlockPos getCurrent() { return new BlockPos(x, y, z); }

    @Pure
    @Override
    protected void ResetImpl() { InitCursor(); }

    @Pure
    @Override
    protected boolean MoveNextImpl()
    {
        if (++x <= end_x && x >= origin_x) {
            return true;
        } else if (z >= origin_z && ++z <= end_z) {
            x = origin_x;
            return true;
        } else if (y >= origin_y && ++y <= end_y) {
            x = origin_x;
            z = origin_z;
            return true;
        } else {
            return false;
        }
    }
}
