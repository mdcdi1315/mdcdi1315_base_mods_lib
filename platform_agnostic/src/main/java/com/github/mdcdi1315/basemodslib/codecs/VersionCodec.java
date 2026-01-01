package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Defines a {@link Codec} that can de/encode {@link Version} objects. <br />
 * De/encoding is done through an integer array that specifies the components of the version itself. <br />
 * I.e. a version of 10.14.185.10 would be encoded in JSON as [ 10, 14, 185, 10 ].
 * @since 1.0.14
 */
public final class VersionCodec
    implements Codec<Version>
{
    @Override
    public <T> DataResult<Pair<Version, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<IntStream> d = ops.getIntStream(input);

        Optional<IntStream> result = d.result();

        if (result.isPresent()) {
            int[] ret = new int[4];
            Iterator<Integer> it = result.get().iterator();
            int index = 0;
            while (index < 4 && it.hasNext()) { ret[index++] = it.next(); }
            if (index == 2) {
                return DataResult.success(new Pair<>(new Version(ret[0], ret[1]), input));
            } else if (index == 3) {
                return DataResult.success(new Pair<>(new Version(ret[0], ret[1], ret[2]), input));
            } else if (index == 4) {
                return DataResult.success(new Pair<>(new Version(ret[0], ret[1], ret[2], ret[3]), input));
            } else {
                return DataResult.error(new StringSupplier("Not enough elements to decode the Version object."));
            }
        } else {
            return DataResult.error(new StringSupplier("Cannot decode the specified version because the underlying data source is not an array of integers."));
        }
    }

    @Override
    public <T> DataResult<T> encode(Version version, DynamicOps<T> ops, T prefix) {
        IntStream.Builder b = IntStream.builder();
        b.add(version.Major());
        b.add(version.Minor());
        int temp;
        if ((temp = version.Build()) > -1) { b.add(temp); }
        if ((temp = version.Revision()) > -1) { b.add(temp); }
        return DataResult.success(ops.createIntList(b.build()));
    }
}
