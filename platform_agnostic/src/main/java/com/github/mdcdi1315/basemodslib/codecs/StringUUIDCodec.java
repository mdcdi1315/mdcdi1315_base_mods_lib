package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.UUID;

/**
 * Provides a {@link Codec} that can store {@link UUID} instances as stringified values.
 * @since 1.0.26
 */
public final class StringUUIDCodec
    implements Codec<UUID>
{
    @Override
    public <T> DataResult<Pair<UUID, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<String> str = ops.getStringValue(input);
        if (str.isSuccess()) {
            UUID u;
            try {
                u = UUID.fromString(str.result().get());
            } catch (IllegalArgumentException iae) {
                return CodecUtils.CreateDotNetFormattedErrorDataResult(
                        "Could not parse value \"{0}\" due to an exception: \n{1}",
                        str.result().get(),
                        iae
                );
            }
            return DataResult.success(new Pair<>(u, input));
        } else {
            return DataResult.error(str.error().get().messageSupplier());
        }
    }

    @Override
    public <T> DataResult<T> encode(UUID input, DynamicOps<T> ops, T prefix) { return DataResult.success(ops.createString(input.toString())); }
}
