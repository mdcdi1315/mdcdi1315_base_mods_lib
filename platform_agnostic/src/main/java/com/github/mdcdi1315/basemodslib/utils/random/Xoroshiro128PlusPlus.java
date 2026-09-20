package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Numerics.BitOperations;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * A default implementation of the {@link IRandomSource} interface. <br />
 * The generator used is the Xoroshiro 128++ RNG. <br />
 * You can find more information about it in <a href="http://prng.di.unimi.it">http://prng.di.unimi.it</a>.
 */
@Pure
@SuppressWarnings("SpellCheckingInspection")
public final class Xoroshiro128PlusPlus
    implements IRandomSource
{
    private final long seed;
    private long state_0, state_1;

    /**
     * Creates a new instance of the {@link Xoroshiro128PlusPlus} class, with the specified seed, initializing the random number generator.
     * @param seed The seed to use so that to initialize the Xoroshiro random number generator.
     */
    @Pure
    public Xoroshiro128PlusPlus(long seed)
    {
        this.seed = seed;
        SplitMix64 sm64 = new SplitMix64(seed);
        state_0 = sm64.Next();
        state_1 = sm64.Next();
    }

    @Pure
    @Override
    public long NextLong()
    {
        long result = BitOperations.RotateLeft(state_0 + state_1, 17) + state_0;

        state_1 ^= state_0;
        state_0 = BitOperations.RotateLeft(state_0, 49) ^ state_1 ^ (state_1 << 21); // a, b
        state_1 = BitOperations.RotateLeft(state_1, 28); // c

        return result;
    }

    @Pure
    @Override
    public long GetSeed() { return seed; }
}
