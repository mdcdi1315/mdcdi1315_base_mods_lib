package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.mods.information.*;
import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;
import com.github.mdcdi1315.basemodslib.utils.collections.YieldingEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.fml.jarcontents.JarResource;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.fml.jarcontents.JarResourceVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public final class NeoForgeModModInfo
    extends BaseModInfo
{
    private final ModContainer mod;
    private final NeoForgeFileSystem fs;

    private static ModDependency ConstructDependency(IModInfo.ModVersion dependency)
    {
        return new ModDependency(
                dependency.getModId(),
                switch (dependency.getOrdering()) {
                    case BEFORE -> DependencyLoadTime.BEFORE_PARENT;
                    case AFTER -> DependencyLoadTime.AFTER_PARENT;
                    default -> DependencyLoadTime.NONE;
                },
                switch (dependency.getType()) {
                    case REQUIRED -> DependencyType.REQUIRED;
                    case OPTIONAL -> DependencyType.OPTIONAL;
                    case INCOMPATIBLE -> DependencyType.INCOMPATIBLE;
                    default -> DependencyType.UNMAPPABLE;
                }
        );
    }

    private record NeoForgeFileSystem(JarContents contents)
            implements IModFileSystem
    {
        private NeoForgeFileSystem(IModInfo contents) { this(contents.getOwningFile().getFile().getContents()); }

        private static final class JarFileBasedFile
            extends DeferredFile
        {
            private final JarResource resource;

            public JarFileBasedFile(IModFileSystem fs, String path, JarResource resource)
                    throws ArgumentNullException
            {
                super(fs, path);
                ArgumentNullException.ThrowIfNull(resource, "resource");
                this.resource = resource;
            }

            @Override
            public InputStream Open() throws IOException { return resource.open(); }
        }

        private record JarFileVisitor(NeoForgeFileSystem fs, Action1<DeferredFile> action)
            implements JarResourceVisitor
        {
            @Override
            public void visit(String relativePath, JarResource resource)
            {
                action.action(new JarFileBasedFile(fs, relativePath, resource.retain()));
            }
        }

        private static void EnumerateFiles(YieldingEnumerable.IYieldActionData<DeferredFile, NeoForgeFileSystem> data)
        {
            var fs = data.GetState();
            fs.contents.visitContent(new JarFileVisitor(fs, data::Yield));
        }

        @Override
        public DeferredFile TryGetFile(String path)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(path, "path");
            var jr = contents.get(path);
            return (jr == null) ? null : new JarFileBasedFile(this, path, jr);
        }

        @Override
        public IEnumerable<DeferredFile> GetFiles() { return new YieldingEnumerable<>(this, NeoForgeFileSystem::EnumerateFiles); }
    }

    public NeoForgeModModInfo(ModContainer mod)
    {
        this.mod = mod;
        var mod_data = mod.getModInfo();
        this.fs = new NeoForgeFileSystem(mod_data);

        RegisterAttribute(FRIENDLY_NAME, mod_data.getDisplayName());

        var url = mod_data.getModURL();

        if (url.isPresent())
        {
            try {
                RegisterAttribute(MOD_URL, url.get().toURI());
            } catch (URISyntaxException use) {
                BaseModsLib.LOGGER.warn("[BML]: Failure converting mod URL to a URI.", use);
            }
        }

        url = mod_data.getUpdateURL();

        if (url.isPresent())
        {
            try {
                RegisterAttribute(UPDATE_URL, url.get().toURI());
            } catch (URISyntaxException use) {
                BaseModsLib.LOGGER.warn("[BML]: Failure converting update URL to a URI.", use);
            }
        }

        var lg = mod_data.getLogoFile();
        if (lg.isPresent()) { RegisterAttribute(LOGO_FILE, lg.get()); }

        RegisterAttribute(DESCRIPTION, mod_data.getDescription());
        RegisterAttribute(VERSION, mod_data.getVersion().toString());
        RegisterAttribute(LOAD_ENVIRONMENT, new ModdingEnvironment[] { BaseModsLib.GetEnvironment() });
        RegisterAttribute(DEPENDENCIES, CollectionManipulations.AsList(new DirectlyMappedList<>(mod_data.getDependencies(), NeoForgeModModInfo::ConstructDependency)));

        FinalizeAttributes();
    }

    @Override
    public String GetModID() { return mod.getModId(); }

    @Override
    public IModFileSystem GetFileSystem() { return fs; }
}
