package com.github.mdcdi1315.basemodslib.integration.clothconfig;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigGuiUtils;
import com.github.mdcdi1315.basemodslib.config.ConfigSaveException;
import com.github.mdcdi1315.basemodslib.client.gui.InformationalDialogScreen;
import com.github.mdcdi1315.basemodslib.config.gui.IClothConfigScreenCreator;
import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.customsupport.*;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.saveconsumers.*;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers.*;
import com.github.mdcdi1315.basemodslib.config.gui.IClothConfigTransformableValue;
import com.github.mdcdi1315.basemodslib.config.reflect.ConfigurationClassesOperations;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;

@ApiStatus.Internal
@ClientOnlyEnvironment
public final class ClothConfigScreenCreator
    implements IClothConfigScreenCreator
{
    private final HashMap<Class<?> , IClothConfigTransformableValue<?>> custom_values_holder;

    public ClothConfigScreenCreator()
    {
        custom_values_holder = new HashMap<>(10);
    }

    @Override
    public Screen Create(Screen parent, IModConfig config)
    {
        ConfigBuilder b = ConfigBuilder
                .create()
                .transparentBackground()
                .setParentScreen(parent)
                .setSavingRunnable(new OnSaveAndExit(parent, config));

        String temp = config.GetComment();
        b.setTitle(StringUtils.IsNullOrEmpty(temp) ? Component.translatable("mdcdi1315_base_mods_lib.config.cfg_tweaker_screen.default_title", config.GetName()) : ConfigGuiUtils.ConstructConfigTranslatableString(temp));

        AppendFields(b, config, null, custom_values_holder);

        return b.build();
    }

    @Override
    public <T> void AddCustomConfigValueHandler(IClothConfigTransformableValue<T> handler)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(handler, "handler");

        IClothConfigTransformableValue<?> old;
        synchronized (this) {
            old = custom_values_holder.putIfAbsent(handler.GetTransformationClass(), handler);
        }
        if (old != null) {
            throw new InvalidOperationException("The class that this custom value handler supports has been already covered by another previously registered handler.");
        }
    }

    private static void AppendFields(ConfigBuilder builder, IModConfig config, Component name, Map<Class<?>, IClothConfigTransformableValue<?>> custom_values_transformers)
    {
        ConfigCategory ccr = builder.getOrCreateCategory(name == null ? Component.literal("Root") : name);

        ccr.setDescription(ConfigGuiUtils.ConstructCommentComponentLines(config.GetComment()).orElse(new Component[0]));

        ConfigEntryBuilder ceb = ConfigEntryBuilder.create();

        IEnumerator<ReflectedConfigFieldData> data = ConfigurationClassesOperations.GetConfigFields(config.getClass()).GetEnumerator();

        try {
            ReflectedConfigFieldData field;
            while (data.MoveNext())
            {
                field = data.getCurrent();
                Component setting_name = ConfigGuiUtils.ConstructRootConfigFieldTranslation(config.GetName(), field.GetSerializedFieldName());
                Optional<Component[]> comment_lines = ConfigGuiUtils.ConstructCommentComponentLines(field.GetComment());

                Class<?> fc = field.GetFieldClass();

                if (ReflectionUtils.IsBoolean(fc)) {
                    ccr.addEntry(
                            ceb
                                    .startBooleanToggle(setting_name, (boolean) field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new BaseErrorSupplier<>(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (ReflectionUtils.IsByte(fc)) {
                    ccr.addEntry(
                            ceb
                                    .startIntField(setting_name, (byte)field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new ByteErrorSupplier(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (ReflectionUtils.IsShort(fc)) {
                    ccr.addEntry(
                            ceb
                                    .startIntField(setting_name, (short)field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new ShortErrorSupplier(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (ReflectionUtils.IsInteger(fc)) {
                    ccr.addEntry(
                            ceb
                                    .startIntField(setting_name, (int)field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new BaseErrorSupplier<>(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (ReflectionUtils.IsLong(fc)) {
                    ccr.addEntry(
                            ceb
                                    .startLongField(setting_name, (long) field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new BaseErrorSupplier<>(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (ReflectionUtils.IsFloat(fc)) {
                    ccr.addEntry(
                            ceb
                                    .startFloatField(setting_name, (float) field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new BaseErrorSupplier<>(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (ReflectionUtils.IsDouble(fc)) {
                    ccr.addEntry(
                            ceb
                                    .startDoubleField(setting_name, (double) field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new BaseErrorSupplier<>(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (fc == String.class) {
                    ccr.addEntry(
                            ceb
                                    .startStrField(setting_name, (String) field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new BaseErrorSupplier<>(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (fc == ResourceLocation.class) {
                    ccr.addEntry(
                            new ResourceLocationListEntry(
                                    config,
                                    field,
                                    setting_name,
                                    (ResourceLocation) field.GetValue(config),
                                    ceb.getResetButtonKey(),
                                    comment_lines
                            )
                    );
                } else if (ReflectionUtils.ExtendsClass(fc, Enum.class)) {
                    ccr.addEntry(
                            ceb
                                    .startEnumSelector(setting_name, (Class<Enum<?>>) fc, (Enum<?>) field.GetValue(config))
                                    .setTooltip(comment_lines)
                                    .setErrorSupplier(new BaseErrorSupplier<>(field))
                                    .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                    .build()
                    );
                } else if (ReflectionUtils.ImplementsInterface(fc, IModConfig.class)) {
                    AppendFields(builder, (IModConfig) field.GetValue(config), ConfigGuiUtils.ConstructConfigTranslatableString(field.GetSerializedFieldName()), custom_values_transformers);
                } else if (ReflectionUtils.ImplementsInterface(fc, List.class)) {
                    Class<?> actual_field_class = field.GetListFieldClass();

                    if (ReflectionUtils.IsInteger(actual_field_class))
                    {
                        ccr.addEntry(
                                ceb
                                        .startIntList(setting_name, (List<Integer>) field.GetValue(config))
                                        .setTooltip(comment_lines)
                                        .setErrorSupplier(new BaseErrorSupplier<>(field))
                                        .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                        .build()
                        );
                    } else if (ReflectionUtils.IsLong(actual_field_class))
                    {
                        ccr.addEntry(
                                ceb
                                        .startLongList(setting_name, (List<Long>) field.GetValue(config))
                                        .setTooltip(comment_lines)
                                        .setErrorSupplier(new BaseErrorSupplier<>(field))
                                        .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                        .build()
                        );
                    } else if (ReflectionUtils.IsFloat(actual_field_class))
                    {
                        ccr.addEntry(
                                ceb
                                        .startFloatList(setting_name, (List<Float>) field.GetValue(config))
                                        .setTooltip(comment_lines)
                                        .setErrorSupplier(new BaseErrorSupplier<>(field))
                                        .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                        .build()
                        );
                    } else if (ReflectionUtils.IsDouble(actual_field_class))
                    {
                        ccr.addEntry(
                                ceb
                                        .startDoubleList(setting_name, (List<Double>) field.GetValue(config))
                                        .setTooltip(comment_lines)
                                        .setErrorSupplier(new BaseErrorSupplier<>(field))
                                        .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                        .build()
                        );
                    } else if (actual_field_class == String.class)
                    {
                        ccr.addEntry(
                                ceb
                                        .startStrList(setting_name, (List<String>) field.GetValue(config))
                                        .setTooltip(comment_lines)
                                        .setErrorSupplier(new BaseErrorSupplier<>(field))
                                        .setSaveConsumer(new FieldSaveConsumer<>(config, field))
                                        .build()
                        );
                    } else if (actual_field_class == ResourceLocation.class) {
                        ccr.addEntry(
                                new NestedListListEntry<>(
                                        setting_name,
                                        (List<ResourceLocation>) field.GetValue(config),
                                        false,
                                        new ElementSupplier<>(comment_lines),
                                        new FieldSaveConsumer<>(config, field),
                                        new ElementSupplier<>(List.of()),
                                        ceb.getResetButtonKey(),
                                        true,
                                        true,
                                        new HandleResourceLocationListElementCreation(ceb.getResetButtonKey(), setting_name, comment_lines)
                                )
                        );
                    } else {
                        BaseModsLib.LOGGER.warn("ClothConfigScreenCreator: Cannot handle field named as \"{}\" because a cloth config list adapter for \"{}\" was not found!", field.GetFieldName(), field.GetFieldClass().getName());
                    }
                } else {
                    IClothConfigTransformableValue<?> handler = custom_values_transformers.get(fc);
                    if (handler == null) {
                        BaseModsLib.LOGGER.warn("ClothConfigScreenCreator: Cannot handle field named as \"{}\" because a cloth config adapter for \"{}\" was not found!", field.GetFieldName(), field.GetFieldClass().getName());
                    } else {
                        HandleCustomValue(ceb, ccr, handler, field, setting_name, comment_lines, config);
                    }
                }
            }
        } finally {
            data.Dispose();
        }
    }

    private static <T> void HandleCustomValue(ConfigEntryBuilder ceb, ConfigCategory category, IClothConfigTransformableValue<T> handler, ReflectedConfigFieldData data, Component setting_name, Optional<Component[]> tooltip, IModConfig config)
    {
        category.addEntry(
                new TextTransformableListEntry<>(
                        config,
                        data,
                        handler,
                        setting_name,
                        (T)data.GetValue(config),
                        ceb.getResetButtonKey(),
                        tooltip
                )
        );
    }

    private record HandleResourceLocationListElementCreation(Component reset_button_key, Component setting_name, Optional<Component[]> tooltip)
        implements BiFunction<ResourceLocation, NestedListListEntry<ResourceLocation, ListOfResourceLocationEntry>, ListOfResourceLocationEntry>
    {
        @Override
        public ListOfResourceLocationEntry apply(ResourceLocation location, NestedListListEntry<ResourceLocation, ListOfResourceLocationEntry> resourceLocationListOfResourceLocationEntryNestedListListEntry)
        {
            if (location == null) {
                return new ListOfResourceLocationEntry(
                        setting_name,
                        reset_button_key,
                        tooltip
                );
            } else {
                return new ListOfResourceLocationEntry(
                        setting_name,
                        location,
                        reset_button_key,
                        tooltip
                );
            }
        }
    }

    private record OnSaveAndExit(Screen parent, IModConfig config)
        implements Runnable
    {
        public void run()
        {
            BaseModsLib.LOGGER.info("ConfigManager: Saving configuration file {}", config.GetName());
            try {
                ConfigManager.INSTANCE.SaveConfigurationFile(config);
                BaseModsLib.LOGGER.info("ConfigManager: Configuration file {} successfully saved!", config.GetName());
            } catch (ConfigSaveException cse) {
                InformationalDialogScreen ids = new InformationalDialogScreen(StringUtils.Concat(
                        "Cannot save config ",
                        config.GetName(),
                        " due to an exception: \n",
                        cse.GetCause()
                ), parent);
                Minecraft.getInstance().setScreen(ids);
            }
        }
    }
}
