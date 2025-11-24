package com.github.mdcdi1315.bml_lang_loader;

import net.minecraftforge.fml.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.forgespi.language.*;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class FMLBMLLanguageProvider
    implements IModLanguageProvider
{
    public static final Logger LOGGER;

    private static final String LANG_PROVIDER_NAME = "bml_java_fml";
    private static final String MOD_INFO_SERVER_INSTANCE_CLASS_NAME = "server_mod_instance_class_name";
    private static final String MOD_INFO_CLIENT_INSTANCE_CLASS_NAME = "client_mod_instance_class_name";

    static {
        LOGGER = LogManager.getLogger();
        LOGGER.info("FMLBMLLanguageProvider is loaded and now active!");
    }

    @Override
    public String name() { return LANG_PROVIDER_NAME; }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() { return new BMLScanner(); }

    private static final class FMLBMLConstructedMod
        implements IModLanguageLoader
    {
        private String server, client , bml_module_name;

        public FMLBMLConstructedMod(String server, String client, String bml_module_name)
        {
            this.server = server;
            this.client = client;
            this.bml_module_name = bml_module_name;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T loadMod(IModInfo info, ModFileScanData modFileScanResults, ModuleLayer layer)
        {
            // All the language loaders are not instantiated on the game class loader, they are loaded into the system's one -
            // so we need to do reflection to bind to the appropriate class loader to instantiate things.
            try {
                Class<?> BML_mod_container = Class.forName("com.github.mdcdi1315.bml_lang_loader.FMLBMLModContainer", true , Thread.currentThread().getContextClassLoader());
                Constructor<?> constructor = BML_mod_container.getConstructor(IModInfo.class, String.class, String.class , ModuleLayer.class, Module.class);
                return (T) constructor.newInstance(info, server, client, layer, layer.findModule(bml_module_name).get());
            } catch (ClassNotFoundException e) {
                LOGGER.fatal(Logging.LOADING,"Encountered an unexpected condition: The FMLBMLModContainer class cannot be instantiated, because it does not exist.", e);
                throw new ModCreationException("Cannot construct the mod container due to a class loading issue." , e);
            } catch (NoSuchMethodException e) {
                LOGGER.fatal(Logging.LOADING,"Encountered an unexpected condition: Cannot find the constructor in the FMLBMLModContainer class, because it does not exist.", e);
                throw new ModCreationException("Cannot construct the mod container because the mod container constructor could not be found." , e);
            } catch (InvocationTargetException e) {
                LOGGER.fatal(Logging.LOADING, "Failed to build BML mod", e);
                if (e.getTargetException() instanceof ModLoadingException mle) {
                    throw mle;
                } else {
                    // Keep in sync with Forge's FMLJavaModLanguageProvider class.
                    throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e);
                }
            } catch (InstantiationException e) {
                LOGGER.fatal(Logging.LOADING, "Cannot instantiate the class FMLBMLModContainer." , e);
                throw new ModCreationException("Cannot construct the mod container because the mod container constructor could not be instantiated." , e);
            } catch (IllegalAccessException e) {
                LOGGER.fatal(Logging.LOADING, "Cannot access the FMLBMLModContainer class.", e);
                throw new ModCreationException("Cannot construct the mod container because the mod container class could not be accessed." , e);
            } catch (NoSuchElementException e) {
                LOGGER.fatal(Logging.LOADING, "Cannot find the BML library module.", e);
                throw new ModCreationException("Cannot construct the mod container because the BML library is possibly unavailable." , e);
            }
        }
    }

    // Special record that gets us the consumer method to execute for all mod file scan data.
    private record BMLScanner()
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
            String server = GetPropertyAsString(mod_inf, MOD_INFO_SERVER_INSTANCE_CLASS_NAME),
                   client = GetPropertyAsString(mod_inf, MOD_INFO_CLIENT_INSTANCE_CLASS_NAME),
                   id = mod_inf.getModId();
            if (server == null) {
                // Client-side only workflow
                if (client == null) {
                    // No server or client, dispatch a warning message.
                    LOGGER.warn("Mod with ID {} did not registered any client or server instance. This may be invalid. Ignoring this entry.", id);
                } else if (FMLEnvironment.dist == Dist.CLIENT) {
                    map.put(id , new FMLBMLConstructedMod(null, client, bml_module_name));
                } else {
                    // Attempted to run a client-side mod on the server! This is of course invalid.
                    LOGGER.warn("Attempted to run client-side only mod with ID {} on a server instance! The BML will skip this mod, and you should remove it from your server mods list.", id);
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
                if (LANG_PROVIDER_NAME.equalsIgnoreCase(specification.languageName())) {
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

    @Override
    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(Supplier<R> consumeEvent) {}
}
