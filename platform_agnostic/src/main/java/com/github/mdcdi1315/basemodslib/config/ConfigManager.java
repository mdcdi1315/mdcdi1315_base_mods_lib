package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.TypeDescriptor;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigCodec;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DynamicOps;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.FileSystems;

import java.util.Map;
import java.util.HashMap;

public final class ConfigManager
{
    private final JsonConfigFileFormat json_file_format;
    private final Map<Class<?> , AssociatedConfigInfo<?>> configuration_files;

    /**
     * Gets the single and only object of the configuration manager class.
     */
    public static final ConfigManager INSTANCE = new ConfigManager();

    private static class JsonConfigFileFormat
        implements IConfigFileFormat<JsonElement>
    {
        @Override
        public DynamicOps<JsonElement> GetFileFormatConverter() {
            return JsonOps.INSTANCE;
        }

        @Override
        public JsonElement ReadFromStream(InputStream is)
                throws java.io.IOException
        {
            try (InputStreamReader isr = new InputStreamReader(is)) {
                return JsonParser.parseReader(isr);
            }
        }

        @Override
        public void SaveToStream(OutputStream os, JsonElement jsonElement) throws IOException
        {
            Gson gso = new Gson().newBuilder().setPrettyPrinting().setLenient().create();
            try (OutputStreamWriter osw = new OutputStreamWriter(os)) {
                gso.toJson(jsonElement, gso.newJsonWriter(osw));
            }
        }
    }

    private ConfigManager() {
        json_file_format = new JsonConfigFileFormat();
        configuration_files = new HashMap<>();
    }

    private record AssociatedConfigInfo<T extends IModConfig>(
            String file_name,
            ConfigCodec<T> cfg_codec,
            IConfigFileFormat<?> file_format,
            T default_config
    ) {}

    public <T extends IModConfig> void TrackConfigurationFile(Func1<T> config_constructor, IConfigFileFormat<?> file_format, String file_name_suffix)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config_constructor, "config_constructor");
        ArgumentNullException.ThrowIfNull(file_format, "file_format");
        ArgumentNullException.ThrowIfNull(file_name_suffix, "file_name_suffix");
        Class<T> config_class = TypeDescriptor.DescribeTypeParameter();
        if (configuration_files.containsKey(config_class)) {
            throw new InvalidOperationException("The specified configuration file is already tracked!");
        }
        T cfg = config_constructor.function();
        configuration_files.put(config_class, new AssociatedConfigInfo<>(
                String.format("%s.%s" , cfg.GetName(), file_name_suffix),
                new ConfigCodec<>(config_constructor),
                file_format,
                cfg
        ));
    }

    public <T extends IModConfig> void TrackJsonConfigurationFile(Func1<T> config_constructor) {
        TrackConfigurationFile(config_constructor , json_file_format , "json");
    }

    public <T extends IModConfig> T LoadConfigurationFile()
    {
        AssociatedConfigInfo<T> cfg_info = (AssociatedConfigInfo<T>) configuration_files.get(TypeDescriptor.<T>DescribeTypeParameter());
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        }

        Path constructed = FileSystems.getDefault().getPath(BaseModsLib.GetModConfigurationDirectory().toString(), cfg_info.file_name());

        try (FileInputStream fis = new FileInputStream(constructed.toFile())) {
            return cfg_info.file_format().LoadConfig(fis, cfg_info.cfg_codec);
        } catch (Exception ex) {
            BaseModsLib.LOGGER.error("ConfigManager: Cannot read config file due to an exception.\nReturning the empty configuration instance." , ex);
        }
        return cfg_info.default_config;
    }

    private <T extends IModConfig> void SaveConfigFileInternal(AssociatedConfigInfo<T> cfg_info, Path constructed, T conf)
            throws ConfigSaveException
    {
        try (FileOutputStream fos = new FileOutputStream(constructed.toFile())) {
            cfg_info.file_format.SaveConfig(fos, cfg_info.cfg_codec() , conf);
        } catch (Exception ex) {
            throw new ConfigSaveException(ex);
        }
    }

    public <T extends IModConfig> T LoadOrCreateConfigurationFile()
            throws ConfigSaveException
    {
        AssociatedConfigInfo<T> cfg_info = (AssociatedConfigInfo<T>) configuration_files.get(TypeDescriptor.<T>DescribeTypeParameter());
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        }

        Path constructed = FileSystems.getDefault().getPath(BaseModsLib.GetModConfigurationDirectory().toString(), cfg_info.file_name());

        try (FileInputStream fis = new FileInputStream(constructed.toFile())) {
            return cfg_info.file_format().LoadConfig(fis, cfg_info.cfg_codec);
        } catch (Exception ex) {
            if (ex instanceof FileNotFoundException) {
                BaseModsLib.LOGGER.info("ConfigManager: Configuration file {} does not exist - creating it now." , cfg_info.file_name());
                try {
                    SaveConfigFileInternal(cfg_info, constructed, cfg_info.default_config);
                } catch (ConfigSaveException cse) {
                    BaseModsLib.LOGGER.error("ConfigManager: Cannot save config file due to an exception. The exception will be thrown back.", cse.GetCause());
                    throw cse;
                }
            } else {
                BaseModsLib.LOGGER.error("ConfigManager: Cannot read config file due to an exception.\nReturning the empty configuration instance." , ex);
            }
        }
        return cfg_info.default_config;
    }

    public <T extends IModConfig> void SaveConfigurationFile(T config_data)
            throws ArgumentNullException, InvalidOperationException, ConfigSaveException
    {
        ArgumentNullException.ThrowIfNull(config_data, "config_data");
        AssociatedConfigInfo<T> cfg_info = (AssociatedConfigInfo<T>) configuration_files.get(config_data.getClass());
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        }

        SaveConfigFileInternal(cfg_info , FileSystems.getDefault().getPath(BaseModsLib.GetModConfigurationDirectory().toString(), cfg_info.file_name()) , config_data);
    }


}
