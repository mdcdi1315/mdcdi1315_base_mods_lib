package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.IDisposable;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import java.nio.file.Path;

/**
 * Provides the mechanism for loading arbitrary resources from mod files. <br />
 * Irrelevant to Minecraft's logic of preparable resource reload listeners, this is for accessing any file in mod .jar files. <br />
 * Note: This object may hold I/O resources while this exists, make sure that you are calling {@link IDisposable#Dispose()} once you do not need the lookup!
 * @since 1.0.31
 */
public interface IModResourceLookup
    extends IDisposable, ISynchronized
{
    /**
     * Gets the root path where the logical mod data begin.
     * @return The {@link Path} where the logical mod data begin.
     */
    @NotNull
    Path GetRootPath();

    /**
     * Gets a {@link Path} to a resource (file or directory)
     * @param path The path.
     * @return A {@link Path} instance pointing to a virtual or physical file system object.
     * @throws ArgumentNullException {@code path} is {@code null}.
     */
    @NotNull
    Path GetResource(String path) throws ArgumentNullException;

    /**
     * Gets the mod instance that this resource lookup is referring to.
     * @return The {@link IServerModInstance} associated with this resource lookup. <br />
     * Can be {@code null} if the {@link IModResourceLookup} was created by a non-BML mod.
     */
    @MaybeNull
    IServerModInstance GetMod();
}
