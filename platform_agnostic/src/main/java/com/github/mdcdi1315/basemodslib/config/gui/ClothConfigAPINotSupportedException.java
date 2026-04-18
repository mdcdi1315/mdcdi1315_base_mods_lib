package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

/**
 * Provides an exception class that specifies that Cloth Config API is
 * unavailable or not installed to the instance.
 * @since 1.0.26
 */
public final class ClothConfigAPINotSupportedException
    extends BaseModsLibraryException
{
    /**
     *
     */
    public ClothConfigAPINotSupportedException()
    {
        super("Cloth Config API has not been detected in your environment. No Cloth Config API features are available.");
    }
}
