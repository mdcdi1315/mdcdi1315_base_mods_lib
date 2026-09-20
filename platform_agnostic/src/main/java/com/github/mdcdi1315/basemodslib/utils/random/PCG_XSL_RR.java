package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Numerics.BitOperations;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/*
 * The number generation code was largely adapted from the PCG XSL_RR output function, adapted to 64 bits state:
 *
 * PCG Random Number Generation for C++
 *
 * Copyright 2014-2022 Melissa O'Neill <oneill@pcg-random.org>,
 *                     and the PCG Project contributors.
 *
 * SPDX-License-Identifier: (Apache-2.0 OR MIT)
 *
 * Licensed under the Apache License, Version 2.0 (provided in
 * LICENSE-APACHE.txt and at http://www.apache.org/licenses/LICENSE-2.0)
 * or under the MIT license (provided in LICENSE-MIT.txt and at
 * http://opensource.org/licenses/MIT), at your option. This file may not
 * be copied, modified, or distributed except according to those terms.
 *
 * Distributed on an "AS IS" BASIS, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied.  See your chosen license for details.
 *
 * For additional information about the PCG random number generation scheme,
 * visit http://www.pcg-random.org/.
 *
 * mdcdi1315: Adapted to Java in 2026 for use with the BML library.
 */

/**
 * Another default implementation of the {@link IRandomSource} interface. <br />
 * The generator used is the PCG XSL RR RNG (with 64 bits of state). <br />
 * You can find more information about it in <a href="http://www.pcg-random.org/">http://www.pcg-random.org/</a>. <br /> <br />
 *
 * NOTE: There are better and faster implementations of the {@link IRandomSource} interface.
 * The Xoroshiro generators are a prime example.
 * Use those wherever CPU performance is critical.
 */
@Pure
@SuppressWarnings("SpellCheckingInspection")
public final class PCG_XSL_RR
    implements IRandomSource
{
    private long state;
    private final long seed;
    private static final long MASK = (1 << 7) - 1;
    private static final long INCREMENT = 1442695040888963407L;
    private static final long MULTIPLIER = 6364136223846793005L;

    @Pure
    public PCG_XSL_RR(long seed)
    {
        this.seed = seed;
        SplitMix64 sm64 = new SplitMix64(seed);
        state = sm64.Next();
    }

    @Pure
    @Override
    public long NextLong()
    {
        state = (state * MULTIPLIER) + INCREMENT;
        int rot = (int)(state & MASK);
        state ^= state >> 32;
        return BitOperations.RotateRight(state, rot);
    }

    @Pure
    @Override
    public long GetSeed() { return seed; }
}
