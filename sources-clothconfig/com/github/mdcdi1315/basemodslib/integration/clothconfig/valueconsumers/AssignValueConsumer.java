package com.github.mdcdi1315.basemodslib.integration.clothconfig.valueconsumers;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigRecord;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class AssignValueConsumer<T>
        implements Consumer<T>
{
    protected final IModConfig config;
    protected final Field field_to_set;

    public AssignValueConsumer(ConfigRecord record, IConfigField<?> field)
    {
        config = record.GetConfig();
        field_to_set = record.GetFieldInstance(field.GetName());
    }

    protected final void ReportSetWarning(IllegalAccessException iae) {
        BaseModsLib.LOGGER.warn("Cannot change the value of field {}: {}", field_to_set.getName(), iae);
    }

    @Override
    public void accept(T value) {
        try {
            field_to_set.set(config, value);
        } catch (IllegalAccessException e) {
            ReportSetWarning(e);
        }
    }
}
