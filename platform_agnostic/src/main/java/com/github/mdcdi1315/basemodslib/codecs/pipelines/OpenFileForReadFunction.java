package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides the factory method for opening files for the decoder pipelines.
 */
@FunctionalInterface
public interface OpenFileForReadFunction
{
    /**
     * Opens an {@link InputStream} for reading data.
     * @return The opened {@link InputStream}. Must be closed by the callers.
     * @throws IOException If any I/O error occurs during opening the file.
     */
    @NotNull
    InputStream Open() throws IOException;
}
