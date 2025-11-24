package com.github.mdcdi1315.basemodslib.config;

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
}

