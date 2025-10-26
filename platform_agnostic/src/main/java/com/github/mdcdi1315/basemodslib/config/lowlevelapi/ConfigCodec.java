package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Iterator;
import java.util.Optional;

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
        var dr = ops.getMapValues(input);
        if (dr.error().isPresent()) {
            return DataResult.error(new StringSupplier(dr.error().get().message()));
        } else {
            // Three fields must exist on the map, one named 'name', the other named 'comment' and the value containing the rest fields 'values'.
            Iterator<Pair<T1 , T1>> pi = dr.result().get().iterator();
            Pair<T1 , T1> temp_pair;
            T1 fields_value = null;
            Optional<String> st;
            while (pi.hasNext()) {
                temp_pair = pi.next();
                st = ops.getStringValue(temp_pair.getFirst()).result();
                if (st.isPresent()) {
                    String s = st.get();
                    if (s.equals("name") || s.equals("comment")) {
                        continue;
                    } else if (s.equals("values")) {
                        fields_value = temp_pair.getSecond();
                    } else {
                        return DataResult.error(new StringSupplier(String.format("Unknown field: %s" , s)));
                    }
                } else {
                    return DataResult.error(new StringSupplier("All the keys in the map must be string keys."));
                }
            }
            if (fields_value == null) {
                return DataResult.error(new StringSupplier("Cannot find the values field! Decoding error occurred."));
            }

            var dr2 = Record.GetCodec().decode(ops , fields_value);
            if (dr2.error().isPresent()) {
                return DataResult.error(new StringSupplier(dr2.error().get().message()));
            } else {
                T final_value = constructor.function();
                try {
                    ConfigSerializationHelpers.ApplyConfigData(final_value, dr2.result().get().getFirst());
                } catch (Exception e) {
                    return DataResult.error(new StringSupplier(String.format("Cannot decode due to an exception: %s" , e)));
                }
                return DataResult.success(Pair.of(final_value , input));
            }
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        var mbuilder = ops.mapBuilder();
        mbuilder.add("name" , ops.createString(input.GetName()));
        mbuilder.add("comment" , ops.createString(input.GetComment()));
        try {
            mbuilder.add("values", Record.GetCodec().encode(ConfigSerializationHelpers.GetConfigData(input), ops, prefix));
        } catch (Exception e) {
            return DataResult.error(StringSupplier.FromFormatted("Cannot encode the specified configuration class due to an exception: %s" , e));
        }
        return mbuilder.build(prefix);
    }
}
