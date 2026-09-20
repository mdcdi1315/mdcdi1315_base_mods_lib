package com.github.mdcdi1315.basemodslib.utils.random;

/*
        Written in 2015 by Sebastiano Vigna (vigna@acm.org)

        To the extent possible under law, the author has dedicated all copyright
        and related and neighboring rights to this software to the public domain
        worldwide.

        Permission to use, copy, modify, and/or distribute this software for any
        purpose with or without fee is hereby granted.

        THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
        WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
        MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
        ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
        WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
        ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
        IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

        mdcdi1315: Ported to Java for the BML library in 2026.
*/

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * This is a fixed-increment version of Java 8's SplittableRandom generator <br />
 * See <a href="http://dx.doi.org/10.1145/2714064.2660195">this</a> and
 * <a href="http://docs.oracle.com/javase/8/docs/api/java/util/SplittableRandom.html">this</a> for more information. <br /> <br />
 *
 * It is a very fast generator passing BigCrush, and it can be useful if
 * for some reason you absolutely want 64 bits of state.
 */
// This is not implementing IRandomSource deliberately since it is not an actual PRNG,
// it is just a state generator
public final class SplitMix64
{
    private long x;

    /**
     * Creates a new instance of the {@link SplitMix64} generator.
     * @param initial_state The initially desired seed value.
     */
    @Pure
    public SplitMix64(long initial_state) { x = initial_state; }

    /**
     * Produces a new random number.
     * @return The newly created random number.
     */
    @Pure
    public long Next()
    {
        long z = (x += 0x9e3779b97f4a7c15L);
        z = (z ^ (z >> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >> 31);
    }
}