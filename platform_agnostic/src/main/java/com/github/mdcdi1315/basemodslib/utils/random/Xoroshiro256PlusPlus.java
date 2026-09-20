package com.github.mdcdi1315.basemodslib.utils.random;

/*
 * Written in 2018 by David Blackman and Sebastiano Vigna (vigna@acm.org)
 *
 * To the extent possible under law, the author has dedicated all copyright
 * and related and neighboring rights to this software to the public domain
 * worldwide. This software is distributed without any warranty.
 *
 *	See <http://creativecommons.org/publicdomain/zero/1.0/>.
 *
 * mdcdi1315: Ported to Java for the BML library in 2026.
 */

import com.github.mdcdi1315.DotNetLayer.System.Numerics.BitOperations;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * A default implementation of the {@link IRandomSource} interface. <br />
 * The generator used is the Xoroshiro 256++ RNG. <br />
 * You can find more information about it in <a href="http://prng.di.unimi.it">http://prng.di.unimi.it</a>.
 */
@Pure
@SuppressWarnings("SpellCheckingInspection")
public final class Xoroshiro256PlusPlus
    implements IRandomSource
{
    private final long seed;
    private long state_0, state_1, state_2, state_3;

    /**
     * Creates a new instance of the {@link Xoroshiro256PlusPlus} class, with the specified seed, initializing the random number generator.
     * @param seed The seed to use so that to initialize the Xoroshiro random number generator.
     */
    @Pure
    public Xoroshiro256PlusPlus(long seed)
    {
        this.seed = seed;
        SplitMix64 sm64 = new SplitMix64(seed);
        state_0 = sm64.Next();
        state_1 = sm64.Next();
        state_2 = sm64.Next();
        state_3 = sm64.Next();
    }

    @Pure
    @Override
    public long NextLong()
    {
        long result = BitOperations.RotateLeft(state_0 + state_3, 23) + state_0;
        long t = state_1 << 17;

        state_2 ^= state_0;
        state_3 ^= state_1;
        state_1 ^= state_2;
        state_0 ^= state_3;

        state_2 ^= t;

        state_3 = BitOperations.RotateLeft(state_3, 45);

        return result;
    }

    @Pure
    @Override
    public long GetSeed() { return seed; }
}
