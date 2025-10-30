package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.ConfigField;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.lang.reflect.Field;

public final class ConfigSerializationHelpers
{
    private ConfigSerializationHelpers() {}

    public static <TCFG extends IModConfig> Record GetConfigData(TCFG config)
    {
        Field[] fields = config.getClass().getFields();
        Record.Builder builder = new Record.Builder(fields.length);
        ConfigField cfd;
        String name;
        try {
            for (var fd : fields)
            {
                cfd = fd.getAnnotation(ConfigField.class);
                if (cfd == null) {
                    continue;
                }
                name = cfd.field_name().isEmpty() ? fd.getName() : cfd.field_name();
                builder.Add(new SerializedField(
                        name,
                        EncodeFieldValue(fd.get(config)),
                        cfd.comment()
                ));
            }
        } catch (IllegalAccessException iae) {
            throw new InvalidOperationException("Cannot serialize a non-public-access field!");
        }
        return builder.Build();
    }

    private static Object EncodeFieldValue(Object obj)
    {
        if (obj == null) {
            throw new InvalidOperationException("Attempted to encode a null value!");
        } else if (obj instanceof List<?> list) {
            return ArrayValue.FromListDirectly(list);
        } else if (
                obj instanceof Number ||
                obj instanceof ResourceLocation ||
                obj instanceof String ||
                obj instanceof Boolean
        ) {
            return obj;
        } else {
            throw new NotSupportedException(String.format("Encoding this value is not supported: %s" , obj.getClass().getName()));
        }
    }

    private static Object DecodeFieldValue(Object obj)
    {
        if (obj == null) {
            throw new InvalidOperationException("Attempted to decode a null value!");
        } else if (obj instanceof ArrayValue av) {
            if (av.GetCount() > 0) {
                ArrayList<Object> list = new ArrayList<>(av.GetCount());
                for (Object o : av) { list.add(o); }
                return list;
            } else {
                return List.of(); // Weirdly it is OK since Java can accept a list object of different type??
            }
        } else if (
                obj instanceof Number ||
                        obj instanceof ResourceLocation ||
                        obj instanceof String ||
                        obj instanceof Boolean
        ) {
            // All these pass and are OK
            return obj;
        } else if (obj instanceof List<?> lt) {
            ArrayList<Object> list = new ArrayList<>(lt.size());
            list.addAll(lt);
            return list;
        } else {
            throw new NotSupportedException(String.format("Decoding this value is not supported: %s" , obj.getClass().getName()));
        }
    }

    private static Map<String, Field> BuildFieldMappings(Class<?> cls)
    {
        Field[] fields = cls.getFields();
        Map<String, Field> ret = new HashMap<>(fields.length);
        ConfigField cfd;
        for (var fd : fields)
        {
            cfd = fd.getAnnotation(ConfigField.class);
            if (cfd == null) { continue; }
            ret.put(cfd.field_name().isEmpty() ? fd.getName() : cfd.field_name() , fd);
        }
        return ret;
    }

    public static <TCFG extends IModConfig> void ApplyConfigData(TCFG config_instance, Record serialized_data)
    {
        Map<String, Field> mappings = BuildFieldMappings(config_instance.getClass());
        Field fd;
        try {
            for (SerializedField ser_field : serialized_data) {
                fd = mappings.get(ser_field.GetName());
                if (fd == null) {
                    continue;
                } // Means that the field is removed from the config

                fd.set(config_instance, DecodeFieldValue(ser_field.GetValue()));
            }
        } catch (IllegalAccessException iae) {
            throw new InvalidOperationException("Cannot deserialize a non-public-access field!");
        }
    }
}
