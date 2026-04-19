package com.github.mdcdi1315.bml_lang_loader;

import net.neoforged.fml.*;
import net.neoforged.bus.api.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.Objects;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class FMLBMLModContainer
    extends ModContainer
{
    private Module bml_mod_layer;
    private final IEventBus event_bus;
    private final FMLBMLJavaModLoadingContext context;
    private Class<?> server_class_instance, client_class_instance;

    public FMLBMLModContainer(
            IModInfo info,
            String class_name_server,
            String class_name_client,
            ModuleLayer gameLayer,
            Module bml_mod_layer)
    {
        super(info);
        String hc = Integer.toHexString(hashCode());
        FMLBMLLanguageProvider.LOGGER.debug(
                "Initializing FMLBMLModContainer of hash code 0x{} that will manage mod with ID {} - on game layer 0x{}",
                hc,
                info.getModId(),
                Integer.toHexString(gameLayer.hashCode())
        );
        context = new FMLBMLJavaModLoadingContext(this);
        event_bus = BusBuilder.builder().setExceptionHandler(this::onEventFailed).markerType(IModBusEvent.class).allowPerPhasePost().build();
        Module mod_layer;
        try {
            this.bml_mod_layer = bml_mod_layer;
            mod_layer = gameLayer.findModule(info.getOwningFile().moduleName()).orElseThrow();
        } catch (Throwable e) {
            throw new ModConstructionException(
                    info,
                    String.format(
                            "Failed to create a new instance of the mod with ID %s because the module for it could not be found.",
                            info.getModId()
                    ),
                    "Failed to locate BML or mod module",
                    e
            );
        }
        if (class_name_server != null)
        {
            try {
                FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "FMLBMLModContainer[0x{}]: Detected server class name: {}", hc, class_name_server);
                server_class_instance = Class.forName(mod_layer, class_name_server);
                FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "FMLBMLModContainer[0x{}]: Loaded server mod class {} with class loader of name '{}'.", hc, server_class_instance.getName(), server_class_instance.getClassLoader());
            } catch (Throwable e) {
                FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING, "FMLBMLModContainer[0x{}]: Failed to load class {}", hc, class_name_server, e);
                throw new ModLoadingException(ModLoadingIssue.error("fml.modloadingissue.failedtoloadmodclass").withCause(e).withAffectedMod(info));
            }
        }
        if (class_name_client != null && FMLEnvironment.dist == Dist.CLIENT) { CreateClient(info, mod_layer, class_name_client); }
    }

    private void CreateClient(IModInfo info, Module layer, String class_name_client)
    {
        String hc = Integer.toHexString(hashCode());
        try {
            FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "FMLBMLModContainer[0x{}]: Detected client class name: {}", hc, class_name_client);
            client_class_instance = Class.forName(layer, class_name_client);
            FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "FMLBMLModContainer[0x{}]: Loaded client mod class {} with class loader of name '{}'.", hc, client_class_instance.getName(), client_class_instance.getClassLoader());
        } catch (Throwable e) {
            FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING, "FMLBMLModContainer[0x{}]: Failed to load class {}", hc, class_name_client, e);
            throw new ModLoadingException(ModLoadingIssue.error("fml.modloadingissue.failedtoloadmodclass").withCause(e).withAffectedMod(info));
        }
    }

    protected void constructMod()
    {
        try {
            if (server_class_instance != null) {
                ConstructMod_Server();
            }
            if (client_class_instance != null) {
                ConstructMod_Client();
            }
        } finally {
            bml_mod_layer = null;
        }
    }

    // Gets the loading context of this mod instance.
    public FMLBMLJavaModLoadingContext GetContext() { return context; }

    // Gets the event bus associated with this mod.
    public IEventBus getEventBus() { return event_bus; }

    private void onEventFailed(IEventBus bus, Event event, EventListener[] listeners, int index, Throwable throwable) {
        FMLBMLLanguageProvider.LOGGER.error(new InternalEventBusErrorMessage(event, index, listeners, throwable));
    }

    private void ConstructMod_Server()
    {
        try {
            FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "Loading server mod instance {} of type {}", getModId(), server_class_instance.getName());
            Constructor<?> constructor;
            try {
                constructor = server_class_instance.getDeclaredConstructor(context.getClass());
            } catch (NoSuchMethodException | SecurityException exception) {
                constructor = server_class_instance.getDeclaredConstructor();
            }
            Object inst = constructor.getParameterCount() == 0 ? constructor.newInstance() : constructor.newInstance(context);
            FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "Loaded server mod instance {} of type {}", getModId(), server_class_instance.getName());
            InitializeToBaseModsLibrary_Server(inst);
        } catch (Throwable e) {
            // When a mod constructor throws an exception, it's wrapped in an InvocationTargetException which hides the
            // actual exception from the mod loading error screen.
            if (e instanceof InvocationTargetException wrapped)
                e = Objects.requireNonNullElse(wrapped.getCause(), e); // unwrap the exception

            FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING,"Failed to create server mod instance. ModID: {}, class {}", getModId(), server_class_instance.getName(), e);
            throw new ModLoadingException(ModLoadingIssue.error("fml.modloadingissue.failedtoloadmodclass").withCause(e));
        } finally {
            server_class_instance = null;
        }
    }

    private void ConstructMod_Client()
    {
        try {
            FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "Loading client mod instance {} of type {}", getModId(), client_class_instance.getName());
            Constructor<?> constructor;
            try {
                constructor = client_class_instance.getDeclaredConstructor(context.getClass());
            } catch (NoSuchMethodException | SecurityException exception) {
                constructor = client_class_instance.getDeclaredConstructor();
            }
            Object inst = constructor.getParameterCount() == 0 ? constructor.newInstance() : constructor.newInstance(context);
            FMLBMLLanguageProvider.LOGGER.debug(Logging.LOADING, "Loaded client mod instance {} of type {}", getModId(), client_class_instance.getName());
            InitializeToBaseModsLibrary_Client(inst);
        } catch (Throwable e) {
            // When a mod constructor throws an exception, it's wrapped in an InvocationTargetException which hides the
            // actual exception from the mod loading error screen.
            if (e instanceof InvocationTargetException wrapped)
                e = Objects.requireNonNullElse(wrapped.getCause(), e); // unwrap the exception

            FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING,"Failed to create server mod instance. ModID: {}, class {}", getModId(), client_class_instance.getName(), e);
            throw new ModLoadingException(ModLoadingIssue.error("fml.modloadingissue.failedtoloadmodclass").withCause(e));
        } finally {
            client_class_instance = null;
        }
    }

    private Method GetMethodFrom(String class_name , String name)
            throws NoSuchMethodException
    {
        for (Method m : Class.forName(bml_mod_layer , class_name).getMethods())
        {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        throw new NoSuchMethodException("Cannot find the method named as " + name);
    }

    private void InitializeToBaseModsLibrary_Server(Object mod_instance)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        GetMethodFrom("com.github.mdcdi1315.basemodslib.BaseModsLib", "InitializeServerSideMod").invoke(null, mod_instance , event_bus);
    }

    private void InitializeToBaseModsLibrary_Client(Object mod_instance)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        GetMethodFrom("com.github.mdcdi1315.basemodslib.BaseModsLibClient", "InitializeClientSideMod").invoke(null, mod_instance , event_bus);
    }
}
