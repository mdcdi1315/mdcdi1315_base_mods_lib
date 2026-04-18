package com.github.mdcdi1315.bml_lang_loader;

import net.minecraftforge.fml.loading.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.forgespi.language.*;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

// Special record that gets us the consumer method to execute for all mod file scan data.
public record BMLFileVisitor()
        implements Consumer<ModFileScanData>
{
    @Override
    public void accept(ModFileScanData modFileScanData)
    {
        String bml_mod_name = GetBMLModuleName();
        // Create the language loader map.
        HashMap<String , FMLBMLConstructedMod> data_map = new HashMap<>(14);
        // Scan all the valid mods.
        for (IModFileInfo mi : modFileScanData.getIModInfoData())
        {
            if (!HasBMLLanguageLoader(mi.requiredLanguageLoaders())) { continue; }
            for (IModInfo mod_inf : mi.getMods()) { AddConstructedModIfNeeded(mod_inf , bml_mod_name , data_map); }
        }
        // Add the detected stuff to the stuff to be processed later
        modFileScanData.addLanguageLoader(data_map);
    }

    private static void AddConstructedModIfNeeded(IModInfo mod_inf, String bml_module_name, Map<String , FMLBMLConstructedMod> map)
    {
        String server = GetPropertyAsString(mod_inf, FMLBMLLanguageProvider.MOD_INFO_SERVER_INSTANCE_CLASS_NAME),
                client = GetPropertyAsString(mod_inf, FMLBMLLanguageProvider.MOD_INFO_CLIENT_INSTANCE_CLASS_NAME),
                id = mod_inf.getModId();
        if (server == null) {
            // Client-side only workflow
            if (client == null) {
                // No server or client, dispatch a warning message.
                FMLBMLLanguageProvider.LOGGER.warn("Mod with ID {} did not registered any client or server instance. This may be invalid. Ignoring this entry.", id);
            } else if (FMLEnvironment.dist == Dist.CLIENT) {
                map.put(id , new FMLBMLConstructedMod(null, client, bml_module_name));
            } else {
                // Attempted to run a client-side mod on the server! This is of course invalid.
                FMLBMLLanguageProvider.LOGGER.warn("Attempted to run client-side only mod with ID {} on a server instance! The BML will skip this mod, and you should remove it from your server mods list.", id);
            }
        } else {
            // Put our mod to be constructed any way if it's server mod instance is valid.
            // Handling for both parameters against null is anyway done.
            map.put(id , new FMLBMLConstructedMod(server, client, bml_module_name));
        }
    }

    private static boolean HasBMLLanguageLoader(List<IModFileInfo.LanguageSpec> specifications)
    {
        for (IModFileInfo.LanguageSpec specification : specifications)
        {
            if (FMLBMLLanguageProvider.LANG_PROVIDER_NAME.equalsIgnoreCase(specification.languageName())) {
                return true;
            }
        }
        return false;
    }

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
