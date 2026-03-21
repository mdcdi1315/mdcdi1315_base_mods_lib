package com.github.mdcdi1315.basemodslib.fastbinaryformat;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.io.WrappedOutputStream;
import com.github.mdcdi1315.basemodslib.utils.io.PushbackWrappedInputStream;

import java.io.IOException;

/**
 * Provides the layout and typical characteristics of a Fast Binary Format entry.
 */
public interface BinaryFormatEntry
{
    /**
     * Gets the type of the currently loaded Binary Format entry.
     * @return The type that is currently loaded.
     */
    @NotNull
    BinaryFormatEntryType GetType();

    /**
     * Writes the entry to the specified stream.
     * @param stream The data stream to write the data to.
     * @throws IOException An I/O exception was occurred.
     */
    void WriteTo(@DisallowNull WrappedOutputStream stream) throws IOException;

    /**
     * Loads the entry from the specified stream.
     * @param stream The data stream to load the entry from.
     * @throws IOException An I/O exception was occurred.
     */
    void ReadFrom(@DisallowNull PushbackWrappedInputStream stream) throws IOException;
}
