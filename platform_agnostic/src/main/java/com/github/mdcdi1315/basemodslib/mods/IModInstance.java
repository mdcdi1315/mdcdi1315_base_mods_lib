package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.DotNetLayer.System.IDisposable;

/**
 * Defines the base interface for mod instances. <br />
 * Every mod instance is expected to be initialized and finally destroyed by using the {@link #Dispose()} method.
 */
public interface IModInstance
    extends IDisposable
{
    /**
     * Initializes the mod instance.
     * Runs initial data not specific somehow to Minecraft itself.
     */
    default void Initialize() {

    }

    /**
     * Called when mod's initialization has been completed.
     */
    default void OnInitializeEnd() {

    }

    /**
     * Gets the current mod id for this mod instance. <br />
     * Required for the base services to register data.
     * @return The mod ID that this instance is currently attributed to.
     */
    String GetModId();
}
