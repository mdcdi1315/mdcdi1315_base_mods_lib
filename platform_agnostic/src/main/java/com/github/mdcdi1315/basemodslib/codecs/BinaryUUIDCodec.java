package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;

import java.util.UUID;
import java.util.Iterator;
import java.util.stream.LongStream;

/**
 * Provides a {@link Codec} that can store {@link UUID} instances as binary arrays with two elements.
 * @since 1.0.26
 */
public final class BinaryUUIDCodec
    implements Codec<UUID>
{
    @Override
    public <T> DataResult<Pair<UUID, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<LongStream> dsr = ops.getLongStream(input);
        if (dsr.isSuccess())
        {
            long least, most;
            Iterator<Long> it = dsr.result().get().limit(2).iterator();
            if (it.hasNext()) {
                least = it.next();
                if (it.hasNext()) {
                    most = it.next();
                    return DataResult.success(
                        new Pair<>(
                                new UUID(most, least),
                                input
                        )
                    );
                } else {
                    return CodecUtils.CreateErrorDataResult("Could not find the most significant bits in the binary array!");
                }
            } else {
                return CodecUtils.CreateErrorDataResult("Could not find the least significant bits in the binary array!");
            }
        } else {
            return DataResult.error(dsr.error().get().messageSupplier());
        }
    }

    @Override
    public <T> DataResult<T> encode(UUID input, DynamicOps<T> ops, T prefix)
    {
        ListBuilder<T> builder = ops.listBuilder();

        builder.add(ops.createLong(input.getLeastSignificantBits()));
        builder.add(ops.createLong(input.getMostSignificantBits()));

        return builder.build(prefix);
    }
}
