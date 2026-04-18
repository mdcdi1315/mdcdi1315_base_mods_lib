package com.github.mdcdi1315.basemodslib.integration.clothconfig.customsupport;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;
import com.github.mdcdi1315.basemodslib.config.gui.IClothConfigTransformableValue;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers.CustomFieldErrorSupplier;

import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public final class TextTransformableListEntry<T>
    extends TextFieldListEntry<T>
{
    private final IModConfig config;
    private final ReflectedConfigFieldData data;
    private final IClothConfigTransformableValue<T> handler;

    public TextTransformableListEntry(IModConfig config, ReflectedConfigFieldData data, IClothConfigTransformableValue<T> handler, Component fieldName, T original, Component resetButtonKey, Optional<Component[]> tooltip)
    {
        super(fieldName, original, resetButtonKey, null, new ElementSupplier<>(tooltip), true);

        this.data = data;
        this.config = config;
        this.handler = handler;
        textFieldWidget.setValue(handler.AsString(original));
    }

    @Override
    public Optional<Component> getError()
    {
        return new CustomFieldErrorSupplier<>(data, handler).function(textFieldWidget.getValue());
    }

    @Override
    public void save()
    {
        try {
            data.SetValue(config, handler.Parse(textFieldWidget.getValue()));
        } catch (Exception any) {
            BaseModsLib.LOGGER.warn("Cannot change the value of field {}: {}", data.GetFieldName(), any);
        }
    }

    @Override
    public T getValue()
    {
        try {
            return handler.Parse(textFieldWidget.getValue());
        } catch (FormatException fe) {
            return null;
        }
    }
}
