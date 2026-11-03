package com.github.mdcdi1315.basemodslib.neoforge.resource;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModResourceLookup;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;

import java.nio.file.Path;
import java.nio.file.Files;

public final class ResourceLookupFlows
{
    private ResourceLookupFlows() { }

    private static IServerModInstance GetModInstance(String mod_id)
    {
        if (BaseModsLib.MOD_ID.equals(mod_id)) {
            return null;
        } else {
            return BaseModsLib.GetBMLModInstance(mod_id);
        }
    }

    @StackTraceHidden
    public static IModResourceLookup GetModResourceLookup(String mod_id)
    {
        ArgumentNullException.ThrowIfNull(mod_id, "mod_id");
        BaseModsLib.LOGGER.debug("[BML]: Attempting resource lookup for {}", mod_id);
        var mc = ModList.get().getModContainerById(mod_id);
        if (mc.isPresent()) {
            IModFile fe = mc.get().getModInfo().getOwningFile().getFile();
            Path root_path = fe.getFilePath();
            BaseModsLib.LOGGER.debug("[BML]: ResourceLookupFlows: Code base path for {}: {}", mod_id, root_path);
            if (root_path.toString().endsWith(".jar") && Files.exists(root_path)) {
                BaseModsLib.LOGGER.debug("[BML]: ResourceLookupFlows: Code base for {} is a .jar file, loading", mod_id);
                return new JarFileResourceLookup(fe, GetModInstance(mod_id));
            } else if (Files.isDirectory(root_path)) {
                BaseModsLib.LOGGER.debug("[BML]: ResourceLookupFlows: Code base for {} is a directory, loading", mod_id);
                return new FolderBasedResourceLookup(fe, GetModInstance(mod_id));
            } else {
                BaseModsLib.LOGGER.warn("[BML]: ResourceLookupFlows: Resource lookup for {} failed because the path {} cannot be matched to a valid corresponding case.", mod_id, root_path);
                return null;
            }
        } else {
            BaseModsLib.LOGGER.warn("[BML]: ResourceLookupFlows: Resource lookup for {} failed.", mod_id);
            return null;
        }
    }
}
