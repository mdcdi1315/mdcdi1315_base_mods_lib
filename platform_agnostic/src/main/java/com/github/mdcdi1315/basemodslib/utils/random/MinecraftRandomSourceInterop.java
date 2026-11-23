package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;
import com.github.mdcdi1315.basemodslib.utils.annotations.MinecraftDependentFeature;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

/**
 * Provides methods to convert between {@link IRandomSource} and Minecraft's {@link RandomSource}.
 * @since 1.0.42-beta9
 */
@MinecraftDependentFeature
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.42-beta9")
public final class MinecraftRandomSourceInterop
{
    private MinecraftRandomSourceInterop() { }

    private static final class MinecraftToIRandomSource
        implements IRandomSource
    {
        private final RandomSource random;

        public MinecraftToIRandomSource(RandomSource random) { this.random = random; }

        @Override
        public long GetSeed() { return 0L; }

        @Override
        public long NextLong() { return random.nextLong(); }
    }

    @SuppressWarnings("NullableProblems")
    private static final class IRandomSourceToMinecraft
        implements RandomSource
    {
        private final IRandomSource random;
        private final MarsagliaPolarGaussian gaussian_generation;

        public IRandomSourceToMinecraft(IRandomSource random)
        {
            this.random = random;
            gaussian_generation = new MarsagliaPolarGaussian(this);
        }

        @Override
        public void setSeed(long l)
        {
            int iterations = 128;
            while (l != 0L && iterations-- > 0) { l -= random.NextLong(); }
        }

        @Override
        public long nextLong() { return random.NextLong(); }

        @Override
        public int nextInt() { return RandomUtils.NextInt(random); }

        @Override
        public float nextFloat() { return RandomUtils.NextFloat(random); }

        @Override
        public double nextDouble() { return RandomUtils.NextDouble(random); }

        @Override
        public boolean nextBoolean() { return RandomUtils.NextBoolean(random); }

        @Override
        public double nextGaussian() { return gaussian_generation.nextGaussian(); }

        @Override
        public int nextInt(int i) { return RandomUtils.NextPositiveIntInRangeExclusive(random, i); }

        @Override
        public RandomSource fork() { return new XoroshiroRandomSource(random.NextLong(), random.NextLong()); }

        @Override
        public PositionalRandomFactory forkPositional() { return new XoroshiroRandomSource.XoroshiroPositionalRandomFactory(random.NextLong(), random.NextLong()); }
    }

    /**
     * Converts a Minecraft random source to an instance of the {@link IRandomSource} interface.
     * @param random The Minecraft random source.
     * @return The BML {@link IRandomSource} wrapping the specified {@link RandomSource},
     * @throws ArgumentNullException {@code random} is {@code null}.
     */
    @NotNull
    public static IRandomSource ToIRandomSource(RandomSource random)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(random, "random");
        return new MinecraftToIRandomSource(random);
    }

    /**
     * Converts an instance of the {@link IRandomSource} interface to a Minecraft random source.
     * @param random The BML {@link IRandomSource} to convert.
     * @return The Minecraft {@link RandomSource} wrapping the specified {@link IRandomSource}.
     * @throws ArgumentNullException {@code random} is {@code null}.
     */
    @NotNull
    public static RandomSource ToMinecraftRandomSource(IRandomSource random)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(random, "random");
        return new IRandomSourceToMinecraft(random);
    }
}
