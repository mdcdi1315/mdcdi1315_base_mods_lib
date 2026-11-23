package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides the factory method for opening files for the encoder pipelines.
 */
@FunctionalInterface
public interface OpenFileForWriteFunction
{
    /**
     * Opens an {@link OutputStream} for writing data.
     * @return The opened {@link OutputStream}. Must be closed by the callers.
     * @throws IOException If any I/O error occurs during opening the file.
     */
    @NotNull
    OutputStream Open() throws IOException;
}
