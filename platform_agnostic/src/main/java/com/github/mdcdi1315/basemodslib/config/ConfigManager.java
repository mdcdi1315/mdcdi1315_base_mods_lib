package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.*;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.config.reflect.ConfigCodec;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DynamicOps;

import java.io.*;
import java.util.Map;
import java.nio.file.Path;
import java.lang.Exception;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages, loads, and saves configuration files for mods.
 */
public final class ConfigManager
    implements ISynchronized
{
    private final JsonConfigFileFormat json_file_format;
    private final Map<Class<?> , ConfigurationManagerConfigReference<?>> configuration_files;

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
     * @throws InvalidOperationException While the method tried to create a default configuration imprint instance, it's name was detected as invalid. <br /> <br />
     * -or- <br /> <br />
     * The configuration file of the specified class is already tracked.
     */
    public <T extends IModConfig> void TrackConfigurationFile(
            Class<T> config_class,
            Func1<T> config_constructor,
            IConfigFileFormat<?> file_format,
            String file_name_suffix
    ) throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config_constructor, "config_constructor");
        TrackConfigurationFile(
                config_class,
                config_constructor,
                new ConfigCodec<>(config_constructor, config_class),
                file_format,
                file_name_suffix
        );
    }

    /**
     * Instructs the configuration manager to track the specified configuration file by the specified parameters. <br />
     * This is a more specialized variant for those who want to completely control over the serialization process.
     * @param config_class The class providing the mod's configuration data.
     * @param config_constructor A function providing the configuration class constructor.
     * @param codec The {@link Codec} to use for de/serializing the configuration object tree.
     * @param file_format An instance of the {@link IConfigFileFormat} interface specifying the file format to use for saving and reading the configuration file.
     * @param file_name_suffix An additional suffix in the configuration file name. This is the file's extension, such as 'json', without the dot.
     * @param <T> The type of the configuration class to track. Must be a class implementing the {@link IModConfig} interface.
     * @throws ArgumentNullException {@code config_class}, and/or {@code config_constructor}, and/or {@code file_format}, and/or {@code file_name_suffix} are {@code null}.
     * @throws InvalidOperationException While the method tried to create a default configuration imprint instance, it's name was detected as invalid. <br /> <br />
     * -or- <br /> <br />
     * The configuration file of the specified class is already tracked.
     * @since 1.0.26
     */
    public <T extends IModConfig> void TrackConfigurationFile(
            Class<T> config_class,
            Func1<T> config_constructor,
            Codec<T> codec,
            IConfigFileFormat<?> file_format,
            String file_name_suffix
    ) throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(codec, "codec");
        ArgumentNullException.ThrowIfNull(file_format, "file_format");
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        ArgumentNullException.ThrowIfNull(file_name_suffix, "file_name_suffix");
        ArgumentNullException.ThrowIfNull(config_constructor, "config_constructor");
        if (file_name_suffix.isBlank()) { file_name_suffix = "cfg"; }
        T cfg = config_constructor.function();
        String name = cfg.GetName();
        if (StringUtils.IsNullOrEmpty(name)) {
            throw new InvalidOperationException("The specified configuration class did not specify a valid name!");
        } else {
            ConfigurationManagerConfigReference<T> constructed = new ConfigurationManagerConfigReference<>(
                    String.format("%s.%s" , name, file_name_suffix),
                    codec, file_format, cfg
            );
            ConfigurationManagerConfigReference<?> old_ref = configuration_files.putIfAbsent(config_class, constructed);
            if (old_ref == null) {
                BaseModsLib.LOGGER.info("ConfigManager: Tracking configuration file named as {}, with config class \"{}\".", constructed.file_name(), config_class.getName());
            } else {
                throw new InvalidOperationException("The specified configuration file is already tracked!");
            }
        }
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
        ConfigurationManagerConfigReference<T> cfg_info = (ConfigurationManagerConfigReference<T>) configuration_files.get(config_class);
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        } else {
            Path constructed = BaseModsLib.GetModConfigurationDirectory().resolve(cfg_info.file_name());

            try (InputStream is = Files.newInputStream(constructed, StandardOpenOption.READ)) {
                return cfg_info.LoadConfig(is);
            } catch (Exception ex) {
                BaseModsLib.LOGGER.error("ConfigManager: Cannot read config file due to an exception.\nReturning the empty configuration instance." , ex);
            }
            return cfg_info.default_config();
        }
    }

    private <T extends IModConfig> void SaveConfigFileInternal(ConfigurationManagerConfigReference<T> cfg_info, Path constructed, T conf)
            throws ConfigSaveException
    {
        try (OutputStream os = Files.newOutputStream(constructed, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            cfg_info.SaveConfig(conf, os);
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
        ConfigurationManagerConfigReference<T> cfg_info = (ConfigurationManagerConfigReference<T>) configuration_files.get(config_class);
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        } else {
            Path constructed = BaseModsLib.GetModConfigurationDirectory().resolve(cfg_info.file_name());

            try (InputStream is = Files.newInputStream(constructed, StandardOpenOption.READ)) {
                return cfg_info.LoadConfig(is);
            } catch (Exception ex) {
                if (ex instanceof NoSuchFileException) {
                    BaseModsLib.LOGGER.info("ConfigManager: Configuration file {} does not exist - creating it now." , cfg_info.file_name());
                    try {
                        SaveConfigFileInternal(cfg_info, constructed, cfg_info.default_config());
                    } catch (ConfigSaveException cse) {
                        BaseModsLib.LOGGER.error("ConfigManager: Cannot save config file due to an exception. The exception will be thrown back.", cse.GetCause());
                        throw cse;
                    }
                } else {
                    BaseModsLib.LOGGER.error("ConfigManager: Cannot read config file due to an exception.\nReturning the empty configuration instance." , ex);
                }
            }
            return cfg_info.default_config();
        }
    }

    /**
     * Gets a {@link Codec} that can de/encode the specified configuration class instance. <br />
     * This operation performs a lookup on the registered configuration classes, and if a match
     * is found, it is returned. Otherwise, it throws {@link InvalidOperationException}.
     * @param config_class The type of the configuration class to get its serialization codec.
     * @return The {@link Codec} associated with the specified configuration clas in {@code config_class}.
     * @param <T> The type of the mod configuration to return its serialization codec.
     * @throws ArgumentNullException {@code config_class} is {@code null}.
     * @throws InvalidOperationException The specified {@code config_class} is not tracked by the Configuration Manager.
     * @since 1.0.26
     */
    public <T extends IModConfig> Codec<T> GetConfigurationClassCodec(Class<T> config_class)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        ConfigurationManagerConfigReference<T> reference = (ConfigurationManagerConfigReference<T>) configuration_files.get(config_class);
        if (reference == null) {
            throw new InvalidOperationException("The specified configuration class is not tracked with the BML configuration manager.");
        } else {
            return reference.cfg_codec();
        }
    }

    /**
     * Gets the default configuration instance for the specified configuration class.
     * @param config_class The type of the configuration class to get its default instance.
     * @return The default instance of {@code config_class}.
     * @param <T> The type of the mod configuration class to be returned by this method.
     * @throws ArgumentNullException {@code config_class} is {@code null}.
     * @throws InvalidOperationException The specified {@code config_class} is not tracked by the Configuration Manager.
     * @since 1.0.26
     */
    public <T extends IModConfig> T GetDefaultConfigInstance(Class<T> config_class)
        throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        ConfigurationManagerConfigReference<T> reference = (ConfigurationManagerConfigReference<T>) configuration_files.get(config_class);
        if (reference == null) {
            throw new InvalidOperationException("The specified configuration class is not tracked with the BML configuration manager.");
        } else {
            return reference.default_config();
        }
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
        ConfigurationManagerConfigReference<T> cfg_info = (ConfigurationManagerConfigReference<T>) configuration_files.get(config_data.getClass());
        if (cfg_info == null) {
            throw new InvalidOperationException("This configuration class is not tracked. Track it first, then attempt to read it.");
        } else {
            SaveConfigFileInternal(cfg_info , BaseModsLib.GetModConfigurationDirectory().resolve(cfg_info.file_name()) , config_data);
        }
    }

    // PRIVATE IMPLEMENTATION DETAILS

    private static final class JsonConfigFileFormat
            implements IConfigFileFormat<JsonElement>
    {
        @Override
        public DynamicOps<JsonElement> GetFileFormatConverter() { return JsonOps.INSTANCE; }

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
            try (
                 OutputStreamWriter osw = new OutputStreamWriter(os);
                 JsonWriter writer = new JsonWriter(osw)
            ) {
                writer.setIndent("\t");
                writer.setLenient(true);
                Streams.write(jsonElement, writer);
            }
        }
    }
}
