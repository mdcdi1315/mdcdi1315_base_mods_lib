package com.github.mdcdi1315.bml_lang_loader;

import net.minecraftforge.fml.*;
import net.minecraftforge.forgespi.language.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.Consumer;

public final class FMLBMLLanguageProvider
    implements IModLanguageProvider
{
    public static final Logger LOGGER;

    public static final String LANG_PROVIDER_NAME = "bml_java_fml";
    public static final String MOD_INFO_SERVER_INSTANCE_CLASS_NAME = "server_mod_instance_class_name";
    public static final String MOD_INFO_CLIENT_INSTANCE_CLASS_NAME = "client_mod_instance_class_name";

    static {
        LOGGER = LogManager.getLogger();
        LOGGER.info("FMLBMLLanguageProvider is loaded and now active!");
    }

    @Override
    public String name() { return LANG_PROVIDER_NAME; }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() { return new BMLFileVisitor(); }

    @Override
    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(Supplier<R> consumeEvent) {}
}
