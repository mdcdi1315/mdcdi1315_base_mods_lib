package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ConfigCodec;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.FileSystems;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages, loads, and saves configuration files for mods.
 */
public final class ConfigManager
    implements ISynchronized
{
    private final JsonConfigFileFormat json_file_format;
    private final Map<Class<?> , AssociatedConfigInfo<?>> configuration_files;

    /**
     * Gets the single and only object of the configuration manager class.
     */
    public static final ConfigManager INSTANCE = new ConfigManager();

    private ConfigManager() {
        json_file_format = new JsonConfigFileFormat();
        configuration_files = new ConcurrentHashMap<>();
    }

    /**
     * Instructs the configuration manager to track the specified configuration file by the specified parameters.
     * @param config_class The class providing the mod's configuration data.
     * @param config_constructor A function providing the configuration class constructor.
     * @param file_format An instance of the {@link IConfigFileFormat} specifying the file format to use for saving and reading the configuration file.
     * @param file_name_suffix An additional suffix in the configuration file name. This is the file's extension, such as 'json', without the dot.
     * @param <T> The type of the configuration class to track. Must be a class implementing the {@link IModConfig} interface.
     * @throws ArgumentNullException {@code config_class}, and/or {@code config_constructor}, and/or {@code file_format}, and/or {@code file_name_suffix} are {@code null}.
     */
    public <T extends IModConfig> void TrackConfigurationFile(Class<T> config_class, Func1<T> config_constructor, IConfigFileFormat<?> file_format, String file_name_suffix)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        ArgumentNullException.ThrowIfNull(config_constructor, "config_constructor");
        ArgumentNullException.ThrowIfNull(file_format, "file_format");
        ArgumentNullException.ThrowIfNull(file_name_suffix, "file_name_suffix");
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

    /**
     * Instructs the configuration manager to track the specified configuration file by the specified parameters. <br />
     * The file format used is a JSON format provided by the configuration manager.
     * @param cfg_class The class providing the mod's configuration data.
     * @param config_constructor A function providing the configuration class constructor.
     * @param <T> The type of the configuration class to track. Must be a class implementing the {@link IModConfig} interface.
     * @throws ArgumentNullException {@code cfg_class}, and/or {@code config_constructor} are {@code null}.
     */
    public <T extends IModConfig> void TrackJsonConfigurationFile(Class<T> cfg_class, Func1<T> config_constructor)
            throws ArgumentNullException
    {
        TrackConfigurationFile(cfg_class, config_constructor , json_file_format , "json");
    }

    /**
     * Loads the configuration file, and returns it. <br />
     * If the file cannot be loaded, the exception will be reported to the error log and the default instance will be instead loaded.
     * @param config_class The type of the configuration class to load.
     * @return The loaded mod configuration data.
     * @param <T> The type of the mod configuration to load and return.
     * @throws ArgumentNullException {@code config_class} is {@code null}.
     */
    public <T extends IModConfig> T LoadConfigurationFile(Class<T> config_class)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        AssociatedConfigInfo<T> cfg_info = (AssociatedConfigInfo<T>) configuration_files.get(config_class);
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        }

        Path constructed = FileSystems.getDefault().getPath(BaseModsLib.GetModConfigurationDirectory().toString(), cfg_info.file_name());

        try (FileInputStream fis = new FileInputStream(constructed.toFile())) {
            return LoadConfig(cfg_info.file_format(), fis, cfg_info.cfg_codec);
        } catch (Exception ex) {
            BaseModsLib.LOGGER.error("ConfigManager: Cannot read config file due to an exception.\nReturning the empty configuration instance." , ex);
        }
        return cfg_info.default_config;
    }

    private <T extends IModConfig> void SaveConfigFileInternal(AssociatedConfigInfo<T> cfg_info, Path constructed, T conf)
            throws ConfigSaveException
    {
        try (FileOutputStream fos = new FileOutputStream(constructed.toFile())) {
            SaveConfig(cfg_info.file_format, fos, cfg_info.cfg_codec() , conf);
        } catch (Exception ex) {
            throw new ConfigSaveException(ex);
        }
    }

    /**
     * Loads the configuration file, and returns it. <br />
     * If the file cannot be loaded, the exception will be reported to the error log and the default instance will be instead loaded. <br />
     * If the file does not exist, a new one will be created from the configuration data defaults.
     * @param config_class The type of the configuration class to load.
     * @return The loaded mod configuration data.
     * @param <T> The type of the mod configuration to load and return.
     * @throws ArgumentNullException {@code config_class} is {@code null}.
     * @throws ConfigSaveException An exception was occurred while attempting to save the configuration file.
     */
    public <T extends IModConfig> T LoadOrCreateConfigurationFile(Class<T> config_class)
            throws ConfigSaveException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        AssociatedConfigInfo<T> cfg_info = (AssociatedConfigInfo<T>) configuration_files.get(config_class);
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        }

        Path constructed = FileSystems.getDefault().getPath(BaseModsLib.GetModConfigurationDirectory().toString(), cfg_info.file_name());

        try (FileInputStream fis = new FileInputStream(constructed.toFile())) {
            return LoadConfig(cfg_info.file_format(), fis, cfg_info.cfg_codec);
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

    /**
     * Saves the specified configuration class.
     * @param config_data The configuration class to save.
     * @param <T> The configuration class type to look up before saving it.
     * @throws ArgumentNullException {@code config_data} is {@code null}.
     * @throws InvalidOperationException The class associated with {@code config_data} is not tracked by this configuration manager.
     * @throws ConfigSaveException An exception was occurred while attempting to save the configuration file.
     */
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

    // PRIVATE IMPLEMENTATION DETAILS

    private <TC extends IModConfig, TF> TC LoadConfig(IConfigFileFormat<TF> cfg, InputStream is, Codec<TC> codec)
            throws IOException, ConfigLoadException
    {
        DataResult<Pair<TC , TF>> dr = codec.decode(cfg.GetFileFormatConverter() , cfg.ReadFromStream(is));
        if (dr.error().isPresent()) {
            throw new ConfigLoadException(dr.error().get().message());
        }
        return dr.result().get().getFirst();
    }

    private <TC extends IModConfig, TFormat> void SaveConfig(IConfigFileFormat<TFormat> cfg, OutputStream os, Codec<TC> codec , TC in_config)
            throws IOException, ConfigSaveException
    {
        DynamicOps<TFormat> ops = cfg.GetFileFormatConverter();
        DataResult<TFormat> dr = codec.encode(in_config, ops, ops.empty());
        if (dr.error().isPresent()) {
            throw new ConfigSaveException(dr.error().get().message());
        }
        cfg.SaveToStream(os, dr.result().get());
    }

    private record AssociatedConfigInfo<T extends IModConfig>(String file_name, ConfigCodec<T> cfg_codec, IConfigFileFormat<?> file_format, T default_config) {}

    private static class JsonConfigFileFormat
            implements IConfigFileFormat<JsonElement>
    {
        @Override
        public DynamicOps<JsonElement> GetFileFormatConverter() {
            return JsonOps.INSTANCE;
        }

        @Override
        public JsonElement ReadFromStream(InputStream is)
                throws IOException
        {
            try (InputStreamReader isr = new InputStreamReader(is)) {
                return JsonParser.parseReader(isr);
            }
        }

        @Override
        public void SaveToStream(OutputStream os, JsonElement jsonElement) throws IOException
        {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .create();
            try (OutputStreamWriter osw = new OutputStreamWriter(os)) {
                gson.toJson(jsonElement, gson.newJsonWriter(osw));
            }
        }
    }
}
