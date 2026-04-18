package com.github.mdcdi1315.basemodslib.integration.clothconfig.saveconsumers;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;

import java.util.function.Consumer;

public class FieldSaveConsumer<T>
    implements Consumer<T>
{
    protected final IModConfig config;
    protected final ReflectedConfigFieldData data;

    public FieldSaveConsumer(IModConfig config, ReflectedConfigFieldData data)
    {
        this.data = data;
        this.config = config;
    }

    protected void TryAccept(T value)
    {
        data.SetValue(config, value);
    }

    @Override
    public final void accept(T new_value)
    {
        try {
            TryAccept(new_value);
        } catch (Exception any) {
            BaseModsLib.LOGGER.warn("Cannot change the value of field {}: {}", data.GetFieldName(), any);
        }
    }
}
