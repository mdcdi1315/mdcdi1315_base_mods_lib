package com.github.mdcdi1315.basemodslib.config;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.io.InputStream;
import java.io.OutputStream;

public interface IConfigFileFormat<TFormat>
{
    DynamicOps<TFormat> GetFileFormatConverter();

    TFormat ReadFromStream(InputStream is) throws java.io.IOException;

    void SaveToStream(OutputStream os, TFormat format) throws java.io.IOException;

    default <TC extends IModConfig> TC LoadConfig(InputStream is, Codec<TC> codec)
            throws java.io.IOException, ConfigLoadException
    {
        DataResult<Pair<TC , TFormat>> dr = codec.decode(GetFileFormatConverter() , ReadFromStream(is));
        if (dr.error().isPresent()) {
            throw new ConfigLoadException(dr.error().get().message());
        }
        return dr.result().get().getFirst();
    }

    default <TC extends IModConfig> void SaveConfig(OutputStream os, Codec<TC> codec , TC in_config)
            throws java.io.IOException, ConfigSaveException
    {
        DynamicOps<TFormat> ops = GetFileFormatConverter();
        DataResult<TFormat> dr = codec.encode(in_config, ops, ops.empty());
        if (dr.error().isPresent()) {
            throw new ConfigSaveException(dr.error().get().message());
        }
        SaveToStream(os, dr.result().get());
    }
}

