package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Numerics.BitOperations;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * A default implementation of the {@link IRandomSource} interface. <br />
 * The generator used is the Xoroshiro 128** RNG. <br />
 * You can find more information about it in <a href="http://prng.di.unimi.it">http://prng.di.unimi.it</a>.
 */
@Pure
@SuppressWarnings("SpellCheckingInspection")
public final class Xoroshiro128StarStar
    implements IRandomSource
{
    private final long seed;
    private long state_0, state_1;

    /**
     * Creates a new instance of the {@link Xoroshiro128StarStar} class, with the specified seed, initializing the random number generator.
     * @param seed The seed to use so that to initialize the Xoroshiro random number generator.
     */
    public Xoroshiro128StarStar(long seed)
    {
        this.seed = seed;
        SplitMix64 sm64 = new SplitMix64(seed);
        this.state_0 = sm64.Next();
        this.state_1 = sm64.Next();
    }

    @Pure
    @Override
    public long NextLong()
    {
	    long result = BitOperations.RotateLeft(state_0 * 5, 7) * 9;

        state_1 ^= state_0;
        state_0 = BitOperations.RotateLeft(state_0, 24) ^ state_1 ^ (state_1 << 16); // a, b
        state_1 = BitOperations.RotateLeft(state_1, 37); // c

        return result;
    }

    @Pure
    @Override
    public long GetSeed() { return seed; }
}
