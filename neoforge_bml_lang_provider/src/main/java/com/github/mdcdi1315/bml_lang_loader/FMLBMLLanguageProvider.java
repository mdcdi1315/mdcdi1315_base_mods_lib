package com.github.mdcdi1315.bml_lang_loader;

import net.neoforged.fml.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforgespi.language.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.LoadingModList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;

public final class FMLBMLLanguageProvider
    implements IModLanguageLoader
{
    public static final Logger LOGGER;
    private static String bml_module_name;

    private static final String LANG_PROVIDER_NAME = "bml_java_fml";
    private static final String MOD_INFO_SERVER_INSTANCE_CLASS_NAME = "server_mod_instance_class_name";
    private static final String MOD_INFO_CLIENT_INSTANCE_CLASS_NAME = "client_mod_instance_class_name";

    static {
        LOGGER = LogManager.getLogger("Base Mods Library NFG Language Provider");
        LOGGER.info("FMLBMLLanguageProvider is loaded and now active!");
    }

    @Override
    public String version() { return "1.0.2"; }

    @Override
    public String name() { return LANG_PROVIDER_NAME; }

    @Override
    public ModContainer loadMod(IModInfo info, ModFileScanData modFileScanResults, ModuleLayer layer)
            throws ModLoadingException
    {
        synchronized (this) {
            if (bml_module_name == null) {
                bml_module_name = GetBMLModuleName();
                LOGGER.info("Identified BML library module: {}", bml_module_name);
            }
        }
        // It seems that in NeoForge we can access the mod container class without transitioning.
        // Clever enough, I have to say.

        // Additionally, we do not need to do additional things done in Forge, such as checking whether the mod uses this loader, whether we are OK to load and some other things.

        String server = GetPropertyAsString(info, MOD_INFO_SERVER_INSTANCE_CLASS_NAME),
                client = GetPropertyAsString(info, MOD_INFO_CLIENT_INSTANCE_CLASS_NAME),
                id = info.getModId();
        if (server == null) {
            // Client-side only workflow
            if (client == null) {
                // No server or client, dispatch a warning message.
                LOGGER.warn("FMLBMLLanguageProvider: Mod with ID {} did not registered any client or server instance. This may be invalid. Ignoring this entry.", id);
            } else if (FMLEnvironment.dist == Dist.CLIENT) {
                return new FMLBMLModContainer(info , null , client , layer , layer.findModule(bml_module_name).get());
            } else {
                // Attempted to run a client-side mod on the server! This is of course invalid.
                LOGGER.warn("FMLBMLLanguageProvider: Attempted to run client-side only mod with ID {} on a server instance! The BML will skip this mod, and you should remove it from your server mods list.", id);
            }
        } else {
            // Put our mod to be constructed any way if it's server mod instance is valid.
            // Handling for both parameters against null is anyway done.
            return new FMLBMLModContainer(info , server , client , layer , layer.findModule(bml_module_name).get());
        }
        return null;
    }

    // Helper methods

    private static String GetBMLModuleName()
    {
        String bml_mod_name = null;
        // We need to get into the loading mod list to get the Base Mods Library and return its module name.
        for (IModFileInfo mi : LoadingModList.get().getModFiles())
        {
            IModInfo lib_mod_info = null;
            for (IModInfo inf : mi.getMods())
            {
                if ("mdcdi1315_base_mods_lib".equals(inf.getModId())) {
                    lib_mod_info = inf;
                    break;
                }
            }
            if (lib_mod_info != null) {
                bml_mod_name = mi.moduleName();
                break;
            }
        }
        if (bml_mod_name == null) {
            throw new IllegalStateException("Cannot find the BML library on the loaded mods! This indicates a mod-loader bug.");
        }
        return bml_mod_name;
    }

    private static String GetPropertyAsString(IModInfo info, String prop_name)
    {
        Optional<String> os = info.getConfig().getConfigElement(prop_name);
        return (os.isPresent()) ? os.get() : null;
    }
}
