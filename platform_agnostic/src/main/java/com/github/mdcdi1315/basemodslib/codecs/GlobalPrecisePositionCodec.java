package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.world.GlobalPrecisePosition;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.*;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

/**
 * Provides a {@link Codec} implementation for the {@link GlobalPrecisePosition} class.
 * @since 1.0.19
 */
public final class GlobalPrecisePositionCodec
    implements Codec<GlobalPrecisePosition>
{
    /**
     * Gets the single and only instance of the {@link GlobalPrecisePosition} codec class.
     */
    public static final Codec<GlobalPrecisePosition> INSTANCE = new GlobalPrecisePositionCodec();

    private GlobalPrecisePositionCodec() {}

    @Override
    public <T> DataResult<Pair<GlobalPrecisePosition, T>> decode(DynamicOps<T> ops, T input)
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
                        Vec3.CODEC.parse(ops, pos),
                        GlobalPrecisePosition::new
                ).WithOpsInput(input).Build();
            }
        } else {
            return DataResult.error(d.error().get().messageSupplier());
        }
    }

    @Override
    public <T> DataResult<T> encode(GlobalPrecisePosition input, DynamicOps<T> ops, T prefix)
    {
        RecordBuilder<T> b = ops.mapBuilder();

        b.add("dimension", Level.RESOURCE_KEY_CODEC.encodeStart(ops, input.level()));
        b.add("position", Vec3.CODEC.encodeStart(ops, input.position()));

        return b.build(prefix);
    }
}
