package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Thrown when a mod managed by the Base Mods Library has failed to be initialized. <br />
 * The exception returns the actual exception thrown as well.
 */
public class ModInitializationException
        extends BaseModsLibraryException
{
    private final Exception ex;
    private final String offending_mod_id;

    /**
     * Constructs a new instance of the {@link ModInitializationException} class
     * with the ID of the mod failed loading and the exception occurred by that mod.
     * @param mod_id The ID of the mod that failed mod loading.
     * @param e The exception occurred during initialization of that mod.
     */
    public ModInitializationException(String mod_id, Exception e)
    {
        ArgumentNullException.ThrowIfNull(mod_id);
        ArgumentNullException.ThrowIfNull(e);
        offending_mod_id = mod_id;
        ex = e;
    }

    @NotNull
    public Exception GetCausedException() {
        return ex;
    }

    @NotNull
    public String GetOffendingMod() {
        return offending_mod_id;
    }

    @Override
    public String getMessage() {
        return String.format("Cannot initialize the mod %s!\nUnderlying exception: %s" , offending_mod_id , ex);
    }
}
