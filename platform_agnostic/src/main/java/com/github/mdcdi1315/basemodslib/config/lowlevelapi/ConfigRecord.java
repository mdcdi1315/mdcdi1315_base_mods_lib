package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.ValueType;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.ConfigField;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields.*;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.listsupport.ConfigListCodec;

import com.google.common.collect.ImmutableMap;

import com.mojang.serialization.*;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * A record structure that retains type and data information for its configuration fields. <br />
 * Objects of this can be iterated to find out the supported configuration fields each time.
 * @since 1.0.15
 */
public final class ConfigRecord
    extends ValueType
    implements Iterable<IConfigField<?>>
{
    private final IModConfig config;
    private final ImmutableMap<String, Pair<IConfigField<?> , Field>> fields;

    private record ConfigFieldIterator(Iterator<Pair<IConfigField<?> , Field>> fs)
        implements Iterator<IConfigField<?>>
    {
        @Override
        public boolean hasNext() { return fs.hasNext(); }

        @Override
        public IConfigField<?> next() { return fs.next().getFirst(); }
    }

    /**
     * Constructs a new instance of the {@link ConfigRecord} structure.
     * @param config The mod config to parse.
     * @throws ArgumentNullException {@code config} is {@code null}.
     */
    public ConfigRecord(IModConfig config)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config, "config");
        Field[] fs = (this.config = config).getClass().getFields();
        IConfigField<?> temp;
        ImmutableMap.Builder<String, Pair<IConfigField<?> , Field>> fd = ImmutableMap.builderWithExpectedSize(fs.length);
        for (Field f : fs) {
            temp = ConfigFieldRegistry.CreateConfigField(this.config, f);
            if (temp != null) { fd.put(f.getName(), Pair.of(temp, f)); }
        }
        fields = fd.build();
    }

    @NotNull
    public IModConfig GetConfig() { return config; }

    /**
     * Gets the number of fields that are configuration fields.
     * @return The number of configuration fields that will be returned through {@link #iterator()}.
     */
    public int GetCount() { return fields.size(); }

    /**
     * Gets all the fields that are part of this mod configuration record.
     * @return An iterator iterating though all the fields that are part of this record.
     */
    @NotNull
    @Override
    public Iterator<IConfigField<?>> iterator() { return new ConfigFieldIterator(fields.values().iterator()); }

    public <TDataElement> DataResult<TDataElement> ApplyChanges(DynamicOps<TDataElement> ops, TDataElement prefix)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(ops, "ops");
        ArgumentNullException.ThrowIfNull(prefix, "prefix");

        RecordBuilder<TDataElement> config_data = ops.mapBuilder();
        ListBuilder<TDataElement> list = ops.listBuilder();
        RecordBuilder<TDataElement> entry_builder;
        String constraint_error;
        IConfigField<?> cfd;
        for (Pair<IConfigField<?>, Field> candfield : fields.values()) {
            cfd = candfield.getFirst();
            entry_builder = ops.mapBuilder();

            entry_builder.add("name", ops.createString(cfd.GetName()));
            entry_builder.add("comment", ops.createString(cfd.GetComment()));

            constraint_error = VerifyConstraints(cfd);

            if (constraint_error != null) {
                return DataResult.error(new StringSupplier(constraint_error));
            }

            entry_builder.add("value", ConstructValue(ops, cfd));

            list.add(entry_builder.build(ops.empty()));
        }

        config_data.add("name", ops.createString(config.GetName()));
        config_data.add("comment", ops.createString(config.GetComment()));
        config_data.add("values", list.build(ops.empty()));

        return config_data.build(prefix);
    }

    public <TDataElement> DataResult<IModConfig> ReadConfig(DynamicOps<TDataElement> ops, TDataElement input)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(ops, "ops");
        ArgumentNullException.ThrowIfNull(input, "input");

        DataResult<TDataElement> v = ops.get(input, "values");
        Optional<TDataElement> d = v.result();
        if (d.isEmpty()) {
            return DataResult.error(new StringSupplier(v.error().get().message()));
        } else {
            // OK, we have now the list of data. Let's process it into a map for access convenience.
            Map<String, TDataElement> data = new HashMap<>(fields.size());

            DataResult<Stream<TDataElement>> dsr = ops.getStream(d.get());
            Optional<Stream<TDataElement>> dv = dsr.result();

            if (dv.isEmpty()) {
                return DataResult.error(new StringSupplier(dsr.error().get().message()));
            } else {
                Iterator<TDataElement> i = dv.get().iterator();
                DataResult<MapLike<TDataElement>> element_map;
                Optional<MapLike<TDataElement>> m;
                while (i.hasNext()) {
                    element_map = ops.getMap(i.next());
                    m = element_map.result();
                    if (m.isEmpty()) {
                        return DataResult.error(new StringSupplier(element_map.error().get().message()));
                    } else {
                        String err = ProcessDataStage2(data, ops, m.get());
                        if (err != null) {
                            return DataResult.error(new StringSupplier(err));
                        }
                    }
                }

                // Nice! now we do have everything we need to start decoding fields.

                Pair<IConfigField<?>, Field> cfg_field;
                for (Map.Entry<String, TDataElement> dg : data.entrySet())
                {
                    cfg_field = GetFieldInstancePair(dg.getKey());
                    if (cfg_field == null) { continue; }
                    DataResult<?> unknown = ReadValue(ops, dg.getValue(), cfg_field.getFirst(), dg.getKey());
                    Optional<?> o = unknown.result();
                    if (o.isPresent()) {
                        try {
                            cfg_field.getSecond().set(config, o.get());
                        } catch (IllegalAccessException e) {
                            return DataResult.error(new StringSupplier(e.getMessage()));
                        }
                    } else {
                        BaseModsLib.LOGGER.warn("ConfigManager: Cannot assign the field named as {} in configuration file {} because it cannot be mapped to an appropriate and recognizable value.", dg.getKey(), config.GetName());
                    }
                }
            }
        }
        return DataResult.success(config);
    }

    @MaybeNull
    public Field GetFieldInstance(String name)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        Field fg;
        Pair<IConfigField<?>, Field> f;
        for (var ent : fields.entrySet())
        {
            f = ent.getValue();
            fg = ent.getValue().getSecond();
            if (name.equals(f.getFirst().GetName()) || fg.getName().equals(name)) {
                return fg;
            }
        }
        return null;
    }

    // PRIVATE IMPLEMENTATION DETAILS

    @MaybeNull
    private Pair<IConfigField<?>, Field> GetFieldInstancePair(String name)
    {
        if (name == null) { return null; }
        Field fg;
        Pair<IConfigField<?>, Field> f;
        for (var ent : fields.entrySet())
        {
            f = ent.getValue();
            fg = ent.getValue().getSecond();
            if (name.equals(f.getFirst().GetName()) || fg.getName().equals(name)) { return f; }
        }
        return null;
    }

    private static <T> String ProcessDataStage2(Map<String, T> m, DynamicOps<T> ops, MapLike<T> entry)
    {
        DataResult<String> s = ops.getStringValue(entry.get("name"));
        Optional<String> sd = s.result();

        if (sd.isEmpty()) {
            return s.error().get().message();
        } else {
            if (m.putIfAbsent(sd.get(), entry.get("value")) != null) {
                return StringUtils.Format("An entry with the name {0} has already been appended to the config data.", sd.get());
            } else {
                return null;
            }
        }
    }

    private static <T> String VerifyConstraints(IConfigField<T> field)
    {
        for (IConfigFieldConstraint<T> c : field.GetConstraints()) {
            if (!c.IsSatisfied(field)) {
                return StringUtils.Format("CONSTRAINT_FAILURE: Constraint of type {0} failed to be satisfied.", c.getClass().getName());
            }
        }
        return null;
    }

    private static <TDataElement> DataResult<TDataElement> ConstructValue(DynamicOps<TDataElement> ops, IConfigField<?> field)
    {
        if (field instanceof StringConfigField scf) {
            return DataResult.success(ops.createString(scf.GetValue()));
        } else if (field instanceof ResourceLocationConfigField rcf) {
            return ResourceLocation.CODEC.encode(rcf.GetValue(), ops, ops.empty());
        } else if (field instanceof BooleanConfigField bcf) {
            return DataResult.success(ops.createBoolean(bcf.GetValue()));
        } else if (field instanceof ByteConfigField bcf) {
            return DataResult.success(ops.createByte(bcf.GetValue()));
        } else if (field instanceof ShortConfigField scf) {
            return DataResult.success(ops.createShort(scf.GetValue()));
        } else if (field instanceof IntConfigField icf) {
            return DataResult.success(ops.createInt(icf.GetValue()));
        } else if (field instanceof LongConfigField lcf) {
            return DataResult.success(ops.createLong(lcf.GetValue()));
        } else if (field instanceof FloatConfigField fcf) {
            return DataResult.success(ops.createFloat(fcf.GetValue()));
        } else if (field instanceof DoubleConfigField dcf) {
            return DataResult.success(ops.createDouble(dcf.GetValue()));
        } else if (field instanceof NestedConfigField ncf) {
            return new ConfigRecord(ncf.GetValue()).ApplyChanges(ops, ops.empty());
        } else if (field instanceof EnumConfigField<?> ecf) {
            return DataResult.success(ops.createString(ecf.GetValue().name()));
        } else if (field instanceof ListConfigField lcf) {
            return new ConfigListCodec(lcf.GetElementClass()).encode(lcf.GetValue(), ops, ops.empty());
        } else if (field instanceof CustomConfigField<?> ccf) {
            return ccf.Encode(ops, ops.empty());
        } else {
            return DataResult.error(StringSupplier.FromDotNetFormatted("Cannot map field {0} to the specified type." , field.GetName()));
        }
    }

    private static <TDataElement> DataResult<?> ReadValue(DynamicOps<TDataElement> ops, TDataElement data, IConfigField<?> metadata, String f_name)
    {
        if (metadata == null) {
            return DataResult.error(StringSupplier.FromDotNetFormatted("The field {0} does not exist.", f_name));
        } else if (metadata instanceof StringConfigField) {
            return ops.getStringValue(data);
        } else if (metadata instanceof ResourceLocationConfigField) {
            return ResourceLocation.CODEC.decode(ops, data).map(Pair::getFirst);
        } else if (metadata instanceof BooleanConfigField) {
            return ops.getBooleanValue(data);
        } else if (metadata instanceof ByteConfigField) {
            return ops.getNumberValue(data).map(Number::byteValue);
        } else if (metadata instanceof ShortConfigField) {
            return ops.getNumberValue(data).map(Number::shortValue);
        } else if (metadata instanceof IntConfigField) {
            return ops.getNumberValue(data).map(Number::intValue);
        } else if (metadata instanceof LongConfigField) {
            return ops.getNumberValue(data).map(Number::longValue);
        } else if (metadata instanceof FloatConfigField) {
            return ops.getNumberValue(data).map(Number::floatValue);
        } else if (metadata instanceof DoubleConfigField) {
            return ops.getNumberValue(data).map(Number::doubleValue);
        } else if (metadata instanceof NestedConfigField ncf) {
            return new ConfigRecord(ncf.GetValue()).ReadConfig(ops, ops.empty());
        } else if (metadata instanceof ListConfigField lcf) {
            return new ConfigListCodec(lcf.GetElementClass()).decode(ops, ops.empty());
        } else if (metadata instanceof CustomConfigField<?> ccf) {
            return ccf.Decode(ops, data).map(Pair::getFirst);
        } else if (metadata instanceof EnumConfigField<?> ecf) {
            DataResult<String> ds = ops.getStringValue(data);
            if (ds.isError()) {
                return DataResult.error(new StringSupplier(ds.error().get().message()));
            } else {
                String constant = ds.result().get();
                Class<? extends Enum> enum_class = ecf.GetValue().getClass();
                for (Enum e : enum_class.getEnumConstants()) {
                    if (e.name().equals(constant)) {
                        return DataResult.success(e);
                    }
                }
                return DataResult.error(StringSupplier.FromDotNetFormatted("Cannot find enumeration constant {0} in type {1}.", constant, enum_class.getName()));
            }
        } else {
            return DataResult.error(new StringSupplier("Cannot decode value: " + data));
        }
    }
}
