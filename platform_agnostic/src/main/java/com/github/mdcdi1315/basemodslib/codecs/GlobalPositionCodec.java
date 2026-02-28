package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.world.GlobalPosition;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Provides a {@link Codec} implementation for the {@link GlobalPosition} class.
 * @since 1.0.19
 */
public final class GlobalPositionCodec
    implements Codec<GlobalPosition>
{
    /**
     * Gets the single and only instance of the {@link GlobalPosition} codec class.
     */
    public static final Codec<GlobalPosition> INSTANCE = new GlobalPositionCodec();

    private GlobalPositionCodec() {}

    @Override
    public <T> DataResult<Pair<GlobalPosition, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<MapLike<T>> d = ops.getMap(input);

        if (d.isSuccess()) {
            MapLike<T> like = d.result().get();
            T pos = like.get("position");
            T dim = like.get("dimension");

            if (pos == null || dim == null) {
                return DataResult.error(new ElementSupplier<>("Position or dimension fields are missing"));
            } else {
                return DataResultBuilder.Of(
                        Level.RESOURCE_KEY_CODEC.parse(ops, dim),
                        BlockPos.CODEC.parse(ops, pos),
                        GlobalPosition::new
                ).WithOpsInput(input).Build();
            }
        } else {
            return DataResult.error(d.error().get().messageSupplier());
        }
    }

    @Override
    public <T> DataResult<T> encode(GlobalPosition input, DynamicOps<T> ops, T prefix)
    {
        RecordBuilder<T> b = ops.mapBuilder();

        b.add("dimension", Level.RESOURCE_KEY_CODEC.encodeStart(ops, input.level()));
        b.add("position", BlockPos.CODEC.encodeStart(ops, input.position()));

        return b.build(prefix);
    }
}
