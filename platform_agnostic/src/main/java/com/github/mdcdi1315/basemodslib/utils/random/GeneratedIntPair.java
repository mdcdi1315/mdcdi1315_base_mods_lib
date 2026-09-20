package com.github.mdcdi1315.basemodslib.utils.random;

/**
 * Provides a pair of two integer values. <br />
 * Returned by the {@link RandomUtils#NextIntReturnBoth(IRandomSource)} method.
 * @param first The first integer value.
 * @param second The second integer value.
 */
public record GeneratedIntPair(int first, int second) {}
