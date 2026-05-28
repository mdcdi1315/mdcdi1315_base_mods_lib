package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Provides utilities around block and fluid handling in a world.
 * @since 1.0.34
 */
@SuppressWarnings("unused")
public final class BlockUtils
{
    private BlockUtils() {}

    /**
     * Gets a value whether the specified {@link Block} is a solid block.
     * @param b The {@link Block} to test.
     * @apiNote This is the negation of {@link #IsAirBlock(Block)} method.
     * @return The test result.
     */
    @Pure
    public static boolean IsSolidBlock(Block b) { return !IsAirBlock(b); }

    /**
     * Gets a value whether the specified {@link Block} is an air block.
     * @param b The {@link Block} to test.
     * @return The test result.
     */
    @Pure
    public static boolean IsAirBlock(Block b) { return b instanceof AirBlock; }

    /**
     * Gets a value whether the specified {@link BlockState} is an air block.
     * @param b The {@link BlockState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsAirBlock(BlockState b) { return IsAirBlock(b.getBlock()); }

    /**
     * Gets a value whether the specified {@link BlockState} is a solid block.
     * @param b The {@link BlockState} to test.
     * @apiNote This is the negation of {@link #ReferentIsAirBlock(BlockState)} method.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsSolidBlock(BlockState b) { return IsSolidBlock(b.getBlock()); }

    /**
     * Gets a value whether the specified {@link FluidState} is empty.
     * @param state The {@link FluidState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsEmpty(FluidState state) { return state.is(Fluids.EMPTY); }

    /**
     * Gets a value whether the specified {@link BlockState} is lava.
     * @param state The {@link BlockState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsLava(BlockState state) { return ReferentIsLava(state.getFluidState()); }

    /**
     * Gets a value whether the specified {@link FluidState} is lava.
     * @param state The {@link FluidState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsLava(FluidState state) { return state.is(FluidTags.LAVA); }

    /**
     * Gets a value whether the specified {@link BlockState} is water.
     * @param state The {@link BlockState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsWater(BlockState state) { return ReferentIsWater(state.getFluidState()); }

    /**
     * Gets a value whether the specified {@link FluidState} is water.
     * @param state The {@link FluidState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsWater(FluidState state) { return state.is(FluidTags.WATER); }

    /**
     * Gets a value whether the specified {@link BlockState} is empty or water.
     * @param state The {@link BlockState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsEmptyOrWater(BlockState state) { return ReferentIsEmptyOrWater(state.getFluidState()); }

    /**
     * Gets a value whether the specified {@link FluidState} is empty or water.
     * @param state The {@link FluidState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsEmptyOrWater(FluidState state) { return ReferentIsEmpty(state) || ReferentIsWater(state); }

    /**
     * Gets a value whether the specified {@link BlockState} is empty or water or lava.
     * @param state The {@link BlockState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsEmptyOrWaterOrLava(BlockState state) { return ReferentIsEmptyOrWaterOrLava(state.getFluidState()); }

    /**
     * Gets a value whether the specified {@link FluidState} is empty or water or lava.
     * @param state The {@link FluidState} to test.
     * @return The test result.
     */
    @Pure
    public static boolean ReferentIsEmptyOrWaterOrLava(FluidState state) { return ReferentIsEmpty(state) || ReferentIsLava(state) || ReferentIsWater(state); }

    /**
     * Gets a value whether the block at the specified level and position is empty or water.
     * @param level The level to get the block from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean BlockIsEmptyOrWater(BlockGetter level, BlockPos position)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        return ReferentIsEmptyOrWater(level.getBlockState(position));
    }

    /**
     * Gets a value whether the block at the specified level and position is empty or water or lava.
     * @param level The level to get the block from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean BlockIsEmptyOrWaterOrLava(BlockGetter level, BlockPos position)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        return ReferentIsEmptyOrWaterOrLava(level.getBlockState(position));
    }

    /**
     * Gets a value whether the block at the specified level and position is any solid block.
     * @param level The level to get the block from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean BlockIsSolid(BlockGetter level, BlockPos position)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        return ReferentIsSolidBlock(level.getBlockState(position));
    }

    /**
     * Gets a value whether the block at the specified level and position is air, and the above block is solid.
     * @param level The level to get the blocks from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean BlockIsAirAndAboveSolid(BlockGetter level, BlockPos position)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        return ReferentIsAirBlock(level.getBlockState(position)) &&
               ReferentIsSolidBlock(level.getBlockState(position.above()));
    }

    /**
     * Gets a value whether the block at the specified level and position is solid, and the above block is air.
     * @param level The level to get the blocks from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean BlockIsSolidAndBelowAir(BlockGetter level, BlockPos position)
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        return ReferentIsSolidBlock(level.getBlockState(position)) &&
                ReferentIsAirBlock(level.getBlockState(position.below()));
    }

    /**
     * Gets a value whether the block at the specified level and position is solid, and the above block is air.
     * @param level The level to get the blocks from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean BlockIsSolidAndAboveAir(BlockGetter level, BlockPos position)
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        return ReferentIsSolidBlock(level.getBlockState(position)) &&
                ReferentIsAirBlock(level.getBlockState(position.above()));
    }

    /**
     * Gets a value whether the block at the specified level is not a fluid.
     * @param level The level to get the block from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean IsEmptyFluid(BlockGetter level, BlockPos position)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        return ReferentIsEmpty(level.getFluidState(position));
    }

    /**
     * Gets a value whether the block at the specified level is any fluid.
     * @param level The level to get the block from.
     * @param position The position of the block inside {@code level}.
     * @return The test result.
     * @throws ArgumentNullException {@code position} is {@code null}.
     * @apiNote This method is the negation of the {@link #IsEmptyFluid(BlockGetter, BlockPos)} method.
     */
    public static boolean HasAnyFluid(BlockGetter level, BlockPos position) throws ArgumentNullException { return !IsEmptyFluid(level, position); }

    /**
     * If there is a fluid at the specified level and position, schedule it for ticking.
     * @param level The level to get the fluid from.
     * @param position The position of the fluid inside {@code level}.
     * @return If there is any fluid at the specified position and has successfully scheduled for ticking.
     * @throws ArgumentNullException {@code position} is {@code null}.
     */
    public static boolean IfHasAnyFluidScheduleTick(LevelAccessor level, BlockPos position)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(position , "position");
        FluidState fs = level.getFluidState(position);
        if (ReferentIsEmpty(fs)) {
            return false;
        } else {
            level.scheduleTick(position, fs.getType(), 0);
            return true;
        }
    }
}
