package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public final class ConfigCodec<T extends IModConfig>
    implements Codec<T>
{
    private final Func1<T> constructor;

    public ConfigCodec(Func1<T> ctor)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(ctor, "ctor");
        constructor = ctor;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        T config = constructor.function();

        DataResult<IModConfig> drc;

        if ((drc = new ConfigRecord(config).ReadConfig(ops, input)).isError()) {
            return DataResult.error(new StringSupplier(drc.error().get().message()));
        } else {
            return DataResult.success(Pair.of(config, input));
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return new ConfigRecord(input).ApplyChanges(ops, prefix);
    }
}
