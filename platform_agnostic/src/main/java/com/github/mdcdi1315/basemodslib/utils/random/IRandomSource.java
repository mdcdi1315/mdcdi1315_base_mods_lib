package com.github.mdcdi1315.basemodslib.utils.random;

/**
 * Provides a source that can produce random numbers.
 * @since 1.0.38
 */
public interface IRandomSource
{
    /**
     * Generates a new pseudo-random number.
     * @return The number produced from the source.
     */
    long NextLong();

    /**
     * Gets the seed used to initialize this random source.
     * @return The seed value used to initialize the random source.
     */
    long GetSeed();
}
