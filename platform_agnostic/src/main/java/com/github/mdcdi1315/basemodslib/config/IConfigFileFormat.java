package com.github.mdcdi1315.basemodslib.config;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines an interface for providing diverse configuration file formats.
 * @param <TFormat> The type of the file-specific format to read or write to.
 */
public interface IConfigFileFormat<TFormat>
{
    /**
     * Gets the {@link DynamicOps} object that can convert an object of type {@link TFormat} to any desirable Java class through the Codec subsystem.
     * @return The {@link DynamicOps} object described above.
     */
    DynamicOps<TFormat> GetFileFormatConverter();

    /**
     * Reads from an input stream the specified data format.
     * @param is The stream to read data from.
     * @return A new instance of the file-specific format object to be later consumed.
     * @throws java.io.IOException An I/O exception occurred while reading the stream.
     */
    TFormat ReadFromStream(InputStream is) throws java.io.IOException;

    /**
     * Writes the specified file-specific format object to the specified data stream.
     * @param os The data stream to write data to.
     * @param format An instance of a file-specific format object to be saved on the stream.
     * @throws java.io.IOException An I/O exception occurred while writing to the stream.
     */
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

