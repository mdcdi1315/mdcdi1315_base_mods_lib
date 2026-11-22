package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.mods.information.*;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;

import java.net.URI;
import java.util.Optional;
import java.net.URISyntaxException;

public final class FabricModModInfo
    extends BaseModInfo
{
    private final ModContainer mod;
    private final CompositeFileSystem fs;

    private static void RegisterURIAttribute(String name, String metadata_info, FabricModModInfo info)
    {
        Optional<String> m;
        try {
            m = info.mod.getMetadata().getContact().get(metadata_info);
            if (m.isPresent()) { info.RegisterAttribute(name, new URI(m.get())); }
        } catch (URISyntaxException e) {
            BaseModsLib.LOGGER.warn("[BML]: Failure converting URL to a URI.", e);
        }
    }

    private static ModDependency ConvertDependency(net.fabricmc.loader.api.metadata.ModDependency loader_dep)
    {
        return new ModDependency(
                loader_dep.getModId(),
                DependencyLoadTime.BEFORE_PARENT,
                switch (loader_dep.getKind()) {
                    case DEPENDS -> DependencyType.REQUIRED;
                    case RECOMMENDS, SUGGESTS -> DependencyType.OPTIONAL;
                    case CONFLICTS, BREAKS -> DependencyType.INCOMPATIBLE;
                    default -> DependencyType.UNMAPPABLE;
                }
        );
    }

    public FabricModModInfo(ModContainer mod)
    {
        this.mod = mod;
        var metadata = mod.getMetadata();

        RegisterAttribute(FRIENDLY_NAME, metadata.getName());
        RegisterAttribute(DESCRIPTION, metadata.getDescription());
        RegisterAttribute("Fabric Type", metadata.getType());
        RegisterURIAttribute(MOD_URL, "homepage", this);
        RegisterURIAttribute(UPDATE_URL, "update_url", this);
        RegisterAttribute(VERSION, metadata.getVersion().getFriendlyString());
        RegisterAttribute("License", CollectionManipulations.AsCollection(metadata.getLicense()));
        RegisterAttribute(AUTHORS, CollectionManipulations.MapTo(CollectionManipulations.AsCollection(metadata.getAuthors()), Person::getName));
        RegisterAttribute(DEPENDENCIES, CollectionManipulations.MapTo(CollectionManipulations.AsCollection(metadata.getDependencies()), FabricModModInfo::ConvertDependency));
        var f = metadata.getIconPath(64);
        if (f.isPresent()) { RegisterAttribute(LOGO_FILE, f.get()); }

        RegisterAttribute(LOAD_ENVIRONMENT, switch (metadata.getEnvironment()) {
            case CLIENT -> new ModdingEnvironment[] { ModdingEnvironment.CLIENT };
            case SERVER -> new ModdingEnvironment[] { ModdingEnvironment.SERVER };
            case UNIVERSAL -> new ModdingEnvironment[] { ModdingEnvironment.CLIENT, ModdingEnvironment.SERVER };
            default -> new ModdingEnvironment[0];
        });

        FinalizeAttributes();

        fs = new CompositeFileSystem(
                CollectionManipulations.MapTo(
                        CollectionManipulations.AsCollection(mod.getRootPaths()),
                        NIOPathBasedFileSystem::new
                )
        );
    }

    @Override
    public IModFileSystem GetFileSystem() { return fs; }

    @Override
    public String GetModID() { return mod.getMetadata().getId(); }
}
