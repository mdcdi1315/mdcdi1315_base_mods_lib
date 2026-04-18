package com.github.mdcdi1315.basemodslib.integration.clothconfig.customsupport;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;
import com.github.mdcdi1315.basemodslib.registries.ResourceLocationConstructionException;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers.ResourceLocationErrorSupplier;

import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public final class ResourceLocationListEntry
    extends TextFieldListEntry<ResourceLocation>
{
    private final IModConfig config;
    private final ReflectedConfigFieldData data;

    @SuppressWarnings("deprecation")
    public ResourceLocationListEntry(IModConfig config, ReflectedConfigFieldData field, Component fieldName, ResourceLocation original, Component resetButtonKey) {
        super(fieldName, original, resetButtonKey, null);

        this.data = field;
        this.config = config;
    }

    @SuppressWarnings("deprecation")
    public ResourceLocationListEntry(IModConfig config, ReflectedConfigFieldData field, Component fieldName, ResourceLocation original, Component resetButtonKey, Optional<Component[]> tooltip) {
        this(config, field, fieldName, original, resetButtonKey, new ElementSupplier<>(tooltip));
    }

    @SuppressWarnings("deprecation")
    public ResourceLocationListEntry(IModConfig config, ReflectedConfigFieldData field, Component fieldName, ResourceLocation original, Component resetButtonKey, Supplier<Optional<Component[]>> tooltipSupplier)
    {
        super(fieldName, original, resetButtonKey, null, tooltipSupplier);

        this.data = field;
        this.config = config;
    }

    @SuppressWarnings("deprecation")
    public ResourceLocationListEntry(IModConfig config, ReflectedConfigFieldData field, Component fieldName, ResourceLocation original, Component resetButtonKey, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart)
    {
        super(fieldName, original, resetButtonKey, null, tooltipSupplier, requiresRestart);

        this.data = field;
        this.config = config;
    }

    @Override
    public Optional<Component> getError() {
        return new ResourceLocationErrorSupplier(data).function(textFieldWidget.getValue());
    }

    @Override
    public Optional<ResourceLocation> getDefaultValue() { return Optional.empty(); }

    @Override
    public ResourceLocation getValue()
    {
        try {
            return RegistryUtils.ParseResourceLocation(textFieldWidget.getValue());
        } catch (ResourceLocationConstructionException e) {
            return null;
        }
    }

    @Override
    public void save()
    {
        try {
            data.SetValue(config, RegistryUtils.ParseResourceLocation(textFieldWidget.getValue()));
        } catch (Exception any) {
            BaseModsLib.LOGGER.warn("Cannot change the value of field {}: {}", data.GetFieldName(), any);
        }
    }
}
