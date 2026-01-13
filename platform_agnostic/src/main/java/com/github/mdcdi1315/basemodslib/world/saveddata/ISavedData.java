package com.github.mdcdi1315.basemodslib.world.saveddata;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

/**
 * Defines the base interface for classes that do implement per-dimension saved data logic. <br /> <br />
 * How to work with these instances <br /> <br />
 * By default, during a construction of an object implementing this interface, the internal data representation must be filled in that
 * way so that it is like saved data for it never existed (thus, it is like you never saved your Minecraft world). <br />
 * Any saved data, if any, will be provided later through the invocation of the {@link ISavedData#LoadFrom(SavedDataCommonHeader)} method. <br />
 * Additionally, both the load and save methods should throw on any exception. Error handling will be done by the attaching wrapper host class.
 */
public interface ISavedData
{
    /**
     * Loads existing data into this instance. <br />
     * The header passed provides the custom version of the data, as well as the data themselves.
     * @param header The header to read the saved data from.
     */
    void LoadFrom(@DisallowNull SavedDataCommonHeader header);

    /**
     * Prepares data to be saved into a saved data header.
     * @return The saved data header representing the serialized result of this instance.
     * Restoring it later through the {@link #LoadFrom(SavedDataCommonHeader)} method should contain the data before the object was serialized.
     */
    SavedDataCommonHeader Save();

    /**
     * Gets a value whether this instance has been tampered and the data it retains needs to be saved again. <br />
     * By default, this does always return {@code true} for backwards compatibility.
     * @return A value whether the current saved data should be saved to disk.
     * @since 1.0.15
     */
    default boolean ShouldSave() { return true; }
}
