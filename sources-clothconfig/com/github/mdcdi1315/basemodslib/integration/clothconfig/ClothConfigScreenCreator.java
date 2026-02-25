package com.github.mdcdi1315.basemodslib.integration.clothconfig;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigGuiUtils;
import com.github.mdcdi1315.basemodslib.config.ConfigSaveException;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigRecord;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields.*;
import com.github.mdcdi1315.basemodslib.client.gui.InformationalDialogScreen;
import com.github.mdcdi1315.basemodslib.config.gui.IClothConfigScreenCreator;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers.*;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.valueconsumers.*;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

import java.util.Optional;

@ClientOnlyEnvironment
public class ClothConfigScreenCreator
    implements IClothConfigScreenCreator
{
    private final IModConfig config;

    public ClothConfigScreenCreator(IModConfig config)
    {
        ArgumentNullException.ThrowIfNull(config, "config");
        this.config = config;
    }

    @Override
    public Screen Create(Screen parent) {
        ConfigBuilder b = ConfigBuilder
                .create()
                .transparentBackground()
                .setParentScreen(parent)
                .setSavingRunnable(new OnSaveAndExit(parent, config));

        String temp = config.GetComment();
        b.setTitle((temp == null || temp.isEmpty()) ? Component.translatable("mdcdi1315_base_mods_lib.config.cfg_tweaker_screen.default_title", config.GetName()) : ConfigGuiUtils.ConstructConfigTranslatableString(temp));

        AppendFields(b, config, null);

        b.setSavingRunnable(new OnSaveAndExit(parent, config));

        return b.build();
    }

    private static void AppendFields(ConfigBuilder builder, IModConfig config, Component name)
    {
        ConfigCategory ccr = builder.getOrCreateCategory(name == null ? Component.literal("Root") : name);

        ccr.setDescription(ConfigGuiUtils.ConstructCommentComponentLines(config.GetComment()).orElse(new Component[0]));

        ConfigEntryBuilder ceb = ConfigEntryBuilder.create();

        ConfigRecord rec = new ConfigRecord(config);

        for (IConfigField<?> ccf : rec)
        {
            Component setting_name = ConfigGuiUtils.ConstructRootConfigFieldTranslation(config.GetName(), ccf.GetName());
            Optional<Component[]> comment_lines = ConfigGuiUtils.ConstructCommentComponentLines(ccf.GetComment());

            AbstractConfigListEntry<?> ent = CreateConfigListEntry(ccf, setting_name, comment_lines, rec, ceb);
            if (ent != null) { ccr.addEntry(ent); }
            else if (ccf instanceof NestedConfigField ncf) {
                AppendFields(builder, ncf.GetValue(), ConfigGuiUtils.ConstructConfigTranslatableString(ncf.GetName()));
            }
        }
    }

    private static AbstractConfigListEntry<?> CreateConfigListEntry(IConfigField<?> field, Component setting_name, Optional<Component[]> comment_lines, ConfigRecord record, ConfigEntryBuilder builder)
    {
        if (field instanceof StringConfigField scf) {
            return builder
                    .startStrField(setting_name, scf.GetValue())
                    .setTooltip(comment_lines)
                    .setErrorSupplier(new FieldConstraintErrorSupplier<>(scf))
                    .setSaveConsumer(new AssignValueConsumer<>(record, field))
                    .build();
        } else if (field instanceof FloatConfigField fcf) {
            return builder
                    .startFloatField(setting_name, fcf.GetValue())
                    .setTooltip(comment_lines)
                    .setErrorSupplier(new FieldConstraintErrorSupplier<>(fcf))
                    .setSaveConsumer(new AssignValueConsumer<>(record, field))
                    .build();
        } else if (field instanceof DoubleConfigField dcf) {
            return builder
                    .startDoubleField(setting_name, dcf.GetValue())
                    .setTooltip(comment_lines)
                    .setErrorSupplier(new FieldConstraintErrorSupplier<>(dcf))
                    .setSaveConsumer(new AssignValueConsumer<>(record, field))
                    .build();
        } else if (field instanceof ByteConfigField bcf) {
            return builder
                    .startIntField(setting_name, bcf.GetValue())
                    .setTooltip(comment_lines)
                    .setSaveConsumer(new AssignValueConsumer_Byte(record, field))
                    .build();
        } else if (field instanceof ShortConfigField scf) {
            return builder
                    .startIntField(setting_name, scf.GetValue())
                    .setTooltip(comment_lines)
                    .setSaveConsumer(new AssignValueConsumer_Short(record, field))
                    .build();
        } else if (field instanceof IntConfigField icf) {
            return builder
                    .startIntField(setting_name, icf.GetValue())
                    .setTooltip(comment_lines)
                    .setErrorSupplier(new FieldConstraintErrorSupplier<>(icf))
                    .setSaveConsumer(new AssignValueConsumer<>(record, field))
                    .build();
        } else if (field instanceof LongConfigField lcf) {
            return builder
                    .startLongField(setting_name, lcf.GetValue())
                    .setTooltip(comment_lines)
                    .setErrorSupplier(new FieldConstraintErrorSupplier<>(lcf))
                    .setSaveConsumer(new AssignValueConsumer<>(record, field))
                    .build();
        } else if (field instanceof EnumConfigField<?> ecf) {
            return CreateEnumField(ecf, setting_name, comment_lines, record, builder);
        } else if (field instanceof BooleanConfigField bcf) {
            return builder
                    .startBooleanToggle(setting_name, bcf.GetValue())
                    .setTooltip(comment_lines)
                    .setErrorSupplier(new FieldConstraintErrorSupplier<>(bcf))
                    .setSaveConsumer(new AssignValueConsumer<>(record, field))
                    .build();
        } else if (field instanceof ResourceLocationConfigField rcf) {
            return builder
                    .startStrField(setting_name, rcf.GetValue().toString())
                    .setTooltip(comment_lines)
                    .setErrorSupplier(new ResourceLocationErrorSupplier(rcf))
                    .setSaveConsumer(new AssignValueConsumer_ResourceLocation(record, field))
                    .build();
        } else if (field instanceof ListConfigField lcf) {
            Class<?> element = lcf.GetElementClass();
            if (element == String.class) {
                return builder
                        .startStrList(setting_name, lcf.GetValue().AsImmutableList(String.class))
                        .setSaveConsumer(new AssignValueConsumer_List<>(record, field))
                        .setTooltip(comment_lines)
                        .build();
            } else if (element == Byte.class) {
                return builder
                        .startIntList(setting_name, new DirectlyMappedList<>(lcf.GetValue().AsImmutableList(Byte.class), Byte::intValue))
                        .setSaveConsumer(new AssignValueConsumer_ByteList(record, field))
                        .setTooltip(comment_lines)
                        .build();
            } else if (element == Short.class) {
                return builder
                        .startIntList(setting_name, new DirectlyMappedList<>(lcf.GetValue().AsImmutableList(Short.class), Short::intValue))
                        .setSaveConsumer(new AssignValueConsumer_ShortList(record, field))
                        .setTooltip(comment_lines)
                        .build();
            } else if (element == Integer.class) {
                return builder
                        .startIntList(setting_name, lcf.GetValue().AsImmutableList(Integer.class))
                        .setSaveConsumer(new AssignValueConsumer<>(record, field))
                        .setTooltip(comment_lines)
                        .build();
            } else if (element == Long.class) {
                return builder
                        .startLongList(setting_name, lcf.GetValue().AsImmutableList(Long.class))
                        .setSaveConsumer(new AssignValueConsumer<>(record, field))
                        .setTooltip(comment_lines)
                        .build();
            } else if (element == Float.class) {
                return builder
                        .startFloatList(setting_name, lcf.GetValue().AsImmutableList(Float.class))
                        .setSaveConsumer(new AssignValueConsumer<>(record, field))
                        .setTooltip(comment_lines)
                        .build();
            } else if (element == Double.class) {
                return builder
                        .startDoubleList(setting_name, lcf.GetValue().AsImmutableList(Double.class))
                        .setSaveConsumer(new AssignValueConsumer<>(record, field))
                        .setTooltip(comment_lines)
                        .build();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static <T extends Enum<T>> AbstractConfigListEntry<?> CreateEnumField(EnumConfigField<T> ecf, Component setting_name, Optional<Component[]> comment_lines, ConfigRecord record, ConfigEntryBuilder builder)
    {
        return builder
                .startEnumSelector(setting_name, (Class<T>)ecf.GetValue().getClass(), ecf.GetValue())
                .setTooltip(comment_lines)
                .setErrorSupplier(new FieldConstraintErrorSupplier<>(ecf))
                .setSaveConsumer(new AssignValueConsumer<>(record, ecf))
                .build();
    }

    private record OnSaveAndExit(Screen parent, IModConfig config)
        implements Runnable
    {
        public void run() {
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
