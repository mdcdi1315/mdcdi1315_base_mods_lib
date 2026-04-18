package com.github.mdcdi1315.bml_lang_loader;

import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.IModLanguageProvider;

import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;
import java.lang.reflect.InvocationTargetException;

public final class FMLBMLConstructedMod
        implements IModLanguageProvider.IModLanguageLoader
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
            FMLBMLLanguageProvider.LOGGER.fatal(Logging.LOADING,"Encountered an unexpected condition: The FMLBMLModContainer class cannot be instantiated, because it does not exist.", e);
            throw new ModConstructionException(info, ModLoadingStage.CONSTRUCT, "Cannot construct the mod container due to a class loading issue.", "Class loading issue", e);
        } catch (NoSuchMethodException e) {
            FMLBMLLanguageProvider.LOGGER.fatal(Logging.LOADING,"Encountered an unexpected condition: Cannot find the constructor in the FMLBMLModContainer class, because it does not exist.", e);
            throw new ModConstructionException(info, ModLoadingStage.CONSTRUCT, "Cannot construct the mod container because the mod container constructor could not be found.", "Mod container invalid layout", e);
        } catch (InvocationTargetException e) {
            FMLBMLLanguageProvider.LOGGER.fatal(Logging.LOADING, "Failed to build BML mod", e);
            if (e.getTargetException() instanceof ModLoadingException mle) {
                throw mle;
            } else {
                // Keep in sync with Forge's FMLJavaModLanguageProvider class.
                throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e);
            }
        } catch (InstantiationException e) {
            FMLBMLLanguageProvider.LOGGER.fatal(Logging.LOADING, "Cannot instantiate the class FMLBMLModContainer." , e);
            throw new ModConstructionException(info, ModLoadingStage.CONSTRUCT, "Cannot construct the mod container because the mod container constructor could not be instantiated.", "Mod container instantiation error", e);
        } catch (IllegalAccessException e) {
            FMLBMLLanguageProvider.LOGGER.fatal(Logging.LOADING, "Cannot access the FMLBMLModContainer class.", e);
            throw new ModConstructionException(info, ModLoadingStage.CONSTRUCT, "Cannot construct the mod container because the mod container class could not be accessed.", "Mod container access error", e);
        } catch (NoSuchElementException e) {
            FMLBMLLanguageProvider.LOGGER.fatal(Logging.LOADING, "Cannot find the BML library module.", e);
            throw new ModConstructionException(info, ModLoadingStage.CONSTRUCT, "Cannot construct the mod container because the BML library is possibly unavailable.", "BML Library detection error", e);
        } finally {
            client = null;
            server = null;
            bml_module_name = null;
        }
    }
}
