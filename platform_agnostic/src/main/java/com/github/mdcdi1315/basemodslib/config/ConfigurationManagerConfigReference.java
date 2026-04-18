package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// Retains information about a stored configuration file in
// the BML ConfigManager class.
// Not exposed outside the package, and it is not used anywhere else than the ConfigManager class itself.
record ConfigurationManagerConfigReference<TCFG extends IModConfig>(
        @NotNull String file_name,
        @NotNull Codec<TCFG> cfg_codec,
        @NotNull  IConfigFileFormat<?> file_format,
        // Default configuration data instance.
        // Can be retrieved from 1.0.26 by using a special method.
        @NotNull TCFG default_config
) {
    public TCFG LoadConfig(InputStream stream)
            throws IOException, ConfigLoadException
    {
        DataResult<TCFG> dr = DecodeConfig(file_format, cfg_codec, stream);
        if (dr.isError()) {
            throw new ConfigLoadException(dr.error().get().message());
        } else {
            return dr.result().get();
        }
    }

    private static <T, TC extends IModConfig> DataResult<TC> DecodeConfig(IConfigFileFormat<T> format, Codec<TC> codec, InputStream stream)
            throws IOException
    {
        return codec.decode(format.GetFileFormatConverter(), format.ReadFromStream(stream)).map(Pair::getFirst);
    }

    private static <T, TC extends IModConfig> void EncodeConfig(TC instance, IConfigFileFormat<T> format, Codec<TC> codec, OutputStream stream)
            throws IOException, ConfigSaveException
    {
        DynamicOps<T> ops = format.GetFileFormatConverter();
        DataResult<T> result = codec.encode(instance, ops, ops.empty());
        if (result.isError()) {
            throw new ConfigSaveException(result.error().get().message());
        } else {
            format.SaveToStream(stream, result.result().get());
        }
    }

    public void SaveConfig(TCFG config_instance, OutputStream os) throws IOException, ConfigSaveException { EncodeConfig(config_instance, file_format, cfg_codec, os); }
}
