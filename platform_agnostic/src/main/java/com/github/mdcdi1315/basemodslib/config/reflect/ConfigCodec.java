package com.github.mdcdi1315.basemodslib.config.reflect;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.UnauthorizedAccessException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.config.ConfigList;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;
import com.github.mdcdi1315.basemodslib.codecs.StrictListCodec;

import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.*;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

public final class ConfigCodec<TCFG extends IModConfig>
    implements Codec<TCFG>
{
    private final Func1<TCFG> cfg_creator;
    private final HashMap<String, ReflectedConfigFieldData> data;

    public ConfigCodec(Func1<TCFG> cfg_creator, Class<TCFG> config_class)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        ArgumentNullException.ThrowIfNull(this.cfg_creator = cfg_creator, "cfg_creator");
        ITraversableCollection<ReflectedConfigFieldData> c = ConfigurationClassesOperations.GetConfigFields(config_class);
        data = new HashMap<>(c.GetCount());
        IEnumerator<ReflectedConfigFieldData> d = c.GetEnumerator();
        try {
            ReflectedConfigFieldData c_d;
            while (d.MoveNext())
            {
                c_d = d.getCurrent();
                data.put(c_d.GetSerializedFieldName(), c_d);
            }
        } finally {
            d.Dispose();
        }
    }

    @Override
    public <T> DataResult<Pair<TCFG, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<T> field_entry = ops.get(input, "values");
        if (field_entry.isSuccess())
        {
            TCFG config_inst = cfg_creator.function();
            DataResult<Stream<T>> value_entries = ops.getStream(field_entry.result().get());
            if (value_entries.isError()) {
                return DataResult.error(value_entries.error().get().messageSupplier());
            } else {
                Iterator<T> itr = value_entries.result().get().iterator();
                while (itr.hasNext())
                {
                    DataResult<MapLike<T>> dr_map = ops.getMap(itr.next());
                    if (dr_map.isError()) {
                        return DataResult.error(dr_map.error().get().messageSupplier());
                    } else {
                        DataResult<String> temp;
                        MapLike<T> map = dr_map.result().get();

                        T encoded_name = map.get("name");
                        if (encoded_name == null) {
                            return CodecUtils.CreateErrorDataResult(
                                    "Could not find field named as \"name\" in the config values table."
                            );
                        } else {
                            temp = ops.getStringValue(encoded_name);
                            if (temp.isError()) {
                                return CodecUtils.CreateDotNetFormattedErrorDataResult(
                                        "Could not decode field named as \"name\" because it's value is malformed: {0}",
                                        temp.error().get().message()
                                );
                            } else {
                                ReflectedConfigFieldData current = data.get(temp.result().get());
                                T encoded_value = map.get("value");
                                if (encoded_value == null) {
                                    return CodecUtils.CreateErrorDataResult(
                                            "The field with name \"value\" is malformed in the encoded data.",
                                            new Pair<>(config_inst, input)
                                    );
                                } else {
                                    temp = AssignField(config_inst, ops, current, encoded_value);
                                    if (temp.isError()) {
                                        return DataResult.error(temp.error().get().messageSupplier());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return DataResult.success(new Pair<>(config_inst, input));
        } else {
            return DataResult.error(field_entry.error().get().messageSupplier());
        }
    }

    @SuppressWarnings("deprecation")
    private static <T, TCFG extends IModConfig> DataResult<String> AssignField(TCFG cfg, DynamicOps<T> ops, ReflectedConfigFieldData data, T input)
    {
        Class<?> list_cls = data.GetListFieldClass();
        Codec<?> resolved_codec = ConfigFieldCodecRegistry.GetCodecMapping(
                (Class<?>) (list_cls == null ? data.GetFieldClass() : list_cls)
        );
        if (list_cls != null) {
            resolved_codec = new StrictListCodec<>(resolved_codec);
        }
        var value = resolved_codec.decode(ops, input);
        if (value.isSuccess()) {
            try {
                Object list_data = value.result().get().getFirst();
                if (data.GetFieldClass() == ConfigList.class)
                {
                    // Keep compat with the ConfigList class.
                    // (Although that a typeless List should be used from now on, we are aware of the class instance)
                    List<?> src_list = (List<?>)list_data;
                    ConfigList list = new ConfigList(src_list.size());
                    for (Object i : src_list) { list.Add(i); }
                    list_data = list;
                }
                data.SetValue(cfg, list_data);
                return DataResult.success(StringUtils.Empty);
            } catch (UnauthorizedAccessException uae) {
                return CodecUtils.CreateDotNetFormattedErrorDataResult(
                        "The field {0} is not accessible. Exception data:\n{1}",
                        data.GetSerializedFieldName(),
                        uae
                );
            }
        } else {
            return DataResult.error(value.error().get().messageSupplier());
        }
    }

    private static <TENC, TCFG extends IModConfig> DataResult<TENC> GetField(TCFG cfg, DynamicOps<TENC> ops, ReflectedConfigFieldData data)
    {
        Class<?> list_cls = data.GetListFieldClass();
        Codec<?> resolved_codec = ConfigFieldCodecRegistry.GetCodecMapping(
                (Class<?>) (list_cls == null ? data.GetFieldClass() : list_cls)
        );
        if (list_cls != null) {
            resolved_codec = new StrictListCodec<>(resolved_codec);
        }
        return GetField_Codec(cfg, resolved_codec, ops, data, list_cls);
    }

    @SuppressWarnings("deprecation")
    private static <TENC, TV, TCFG extends IModConfig> DataResult<TENC> GetField_Codec(TCFG cfg, Codec<TV> codec, DynamicOps<TENC> ops, ReflectedConfigFieldData data, Class<?> config_list_class)
    {
        try {
            Object value = data.GetValue(cfg);

            // Keep compat with the ConfigList class.
            // (Although that a typeless List should be used from now on, we are aware of the class instance)
            if (data.GetFieldClass() == ConfigList.class) {
                value = ((ConfigList)value).AsImmutableList(config_list_class);
            }
            // Verify constraints on the target.
            IEnumerator<IConfigFieldConstraint> constraints = data.GetConstraints().GetEnumerator();
            try {
                boolean is_valid = true;
                IConfigFieldConstraint current = null;
                while (constraints.MoveNext())
                {
                    current = constraints.getCurrent();
                    if (!current.IsSatisfied(value)) { is_valid = false; break; }
                }
                if (!is_valid)
                {
                    return CodecUtils.CreateDotNetFormattedErrorDataResult(
                            "A constraint was failed on the value of field \"{0}\"!\nConstraint class instance: {1}\nRejected value: {2}",
                            data.GetSerializedFieldName(),
                            current.getClass().getName(),
                            value
                    );
                }
            } finally {
                constraints.Dispose();
            }
            return codec.encode((TV)value, ops, ops.empty());
        } catch (UnauthorizedAccessException uae) {
            return CodecUtils.CreateDotNetFormattedErrorDataResult(
                    "The field {0} is not accessible. Exception data:\n{1}",
                    data.GetSerializedFieldName(),
                    uae
            );
        }
    }

    @Override
    public <T> DataResult<T> encode(TCFG config, DynamicOps<T> ops, T prefix)
    {
        RecordBuilder<T> config_data = ops.mapBuilder();

        ListBuilder<T> list = ops.listBuilder();
        RecordBuilder<T> entry_builder;

        ReflectedConfigFieldData d;
        for (Map.Entry<String, ReflectedConfigFieldData> e : data.entrySet())
        {
            d = e.getValue();
            entry_builder = ops.mapBuilder();

            entry_builder.add("name", ops.createString(d.GetSerializedFieldName()));
            entry_builder.add("comment", ops.createString(ConfigurationClassesOperations.ConstructComponentFromConfigString(d.GetComment()).getString()));
            entry_builder.add("value", GetField(config, ops, d));

            list.add(entry_builder.build(ops.empty()));
        }

        config_data.add("name", ops.createString(config.GetName()));
        config_data.add("comment", ops.createString(config.GetComment()));
        config_data.add("values", list.build(ops.empty()));
        return config_data.build(prefix);
    }
}
