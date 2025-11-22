package com.github.mdcdi1315.bml_lang_loader;

import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.eventbus.EventBusErrorMessage;

import net.minecraftforge.fml.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Objects;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class FMLBMLModContainer
    extends ModContainer
{
    private Module layer;
    private final IEventBus event_bus;
    private final BMLWrappedModObject mod_object;
    private final FMLBMLJavaModLoadingContext context;
    private Class<?> server_class_instance, client_class_instance;

    private record ContextGetter(FMLBMLJavaModLoadingContext context)
        implements Supplier<FMLBMLJavaModLoadingContext>
    {
        @Override
        public FMLBMLJavaModLoadingContext get() { return context; }
    }

    public FMLBMLModContainer(
            IModInfo info,
            String class_name_server,
            String class_name_client,
            ModuleLayer gameLayer,
            Module bml_mod_layer)
    {
        super(info);
        FMLBMLLanguageProvider.LOGGER.debug("Initializing FMLBMLModContainer for mod ID {}" , info.getModId());
        context = new FMLBMLJavaModLoadingContext(this);
        this.contextExtension = new ContextGetter(context);
        event_bus = BusBuilder.builder().setExceptionHandler(this::onEventFailed).setTrackPhases(false).markerType(IModBusEvent.class).useModLauncher().build();
        Module mod_layer;
        try {
            layer = bml_mod_layer;
            mod_layer = gameLayer.findModule(info.getOwningFile().moduleName()).orElseThrow();
        } catch (Throwable e) {
            throw new ModCreationException(String.format(
                    "Failed to create a new instance of the mod with ID %s because the module for it could not be found.",
                    info.getModId()
            ), e);
        }
        activityMap.put(ModLoadingStage.CONSTRUCT, this::RunConstruction);
        if (class_name_server != null)
        {
            try {
                server_class_instance = Class.forName(mod_layer, class_name_server);
                FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Loaded server mod class {} with class loader of name '{}'.", server_class_instance.getName(), server_class_instance.getClassLoader());
            } catch (Throwable e) {
                FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING, "Failed to load class {}", class_name_server, e);
                throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e);
            }
        }
        if (class_name_client != null && FMLEnvironment.dist == Dist.CLIENT) { CreateClient(info, mod_layer, class_name_client); }
        mod_object = new BMLWrappedModObject();
    }

    private void CreateClient(IModInfo info, Module layer, String class_name_client)
    {
        try {
            client_class_instance = Class.forName(layer, class_name_client);
            FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Loaded client mod class {} with class loader of name '{}'.", client_class_instance.getName(), client_class_instance.getClassLoader());
        } catch (Throwable e) {
            FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING, "Failed to load class {}", class_name_client, e);
            throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e);
        }
    }

    private void RunConstruction()
    {
        if (server_class_instance != null) {
            ConstructMod_Server();
        }
        if (client_class_instance != null) {
            ConstructMod_Client();
        }
    }

    @Override
    public boolean matches(Object mod) { return mod == mod_object; }

    // Returns a wrapped mod object that holds the mod instances, so that those are not accidentally freed during run.
    @Override
    public BMLWrappedModObject getMod() { return mod_object; }

    // Gets the loading context of this mod instance.
    public FMLBMLJavaModLoadingContext GetContext() { return context; }

    // Gets the event bus associated with this mod.
    public IEventBus GetEventBus() { return event_bus; }

    private void onEventFailed(IEventBus event_bus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable) {
        FMLBMLLanguageProvider.LOGGER.error(new EventBusErrorMessage(event, i, iEventListeners, throwable));
    }

    @Override
    protected <T extends Event & IModBusEvent> void acceptEvent(final T e)
    {
        try {
            FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Dispatching event for mod id '{}': {}", this.getModId(), e);
            event_bus.post(e);
            FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Dispatched event for mod id '{}': {}", this.getModId(), e);
        } catch (Throwable t) {
            FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING, "Caught exception during event dispatch for mod id '{}' and event {}", this.getModId(), e, t);
            // Keep the below line in sync with the FMLModContainer class.
            throw new ModLoadingException(modInfo, modLoadingStage, "fml.modloading.errorduringevent", t);
        }
    }

    private void ConstructMod_Server()
    {
        try {
            FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Loading server mod instance {} of type {}", getModId(), server_class_instance.getName());
            Constructor<?> constructor;
            try {
                constructor = server_class_instance.getDeclaredConstructor(context.getClass());
            } catch (NoSuchMethodException | SecurityException exception) {
                constructor = server_class_instance.getDeclaredConstructor();
            }
            InitializeToBaseModsLibrary_Server(constructor.getParameterCount() == 0 ? constructor.newInstance() : constructor.newInstance(context));
            FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Loaded server mod instance {} of type {}", getModId(), server_class_instance.getName());
        } catch (Throwable e) {
            // When a mod constructor throws an exception, it's wrapped in an InvocationTargetException which hides the
            // actual exception from the mod loading error screen.
            if (e instanceof InvocationTargetException wrapped)
                e = Objects.requireNonNullElse(wrapped.getCause(), e); // unwrap the exception

            FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING,"Failed to create server mod instance. ModID: {}, class {}", getModId(), server_class_instance.getName(), e);
            throw new ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", e, server_class_instance);
        } finally {
            server_class_instance = null;
        }
    }

    private void ConstructMod_Client()
    {
        try {
            FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Loading client mod instance {} of type {}", getModId(), client_class_instance.getName());
            Constructor<?> constructor;
            try {
                constructor = client_class_instance.getDeclaredConstructor(context.getClass());
            } catch (NoSuchMethodException | SecurityException exception) {
                constructor = client_class_instance.getDeclaredConstructor();
            }
            InitializeToBaseModsLibrary_Client(constructor.getParameterCount() == 0 ? constructor.newInstance() : constructor.newInstance(context));
            FMLBMLLanguageProvider.LOGGER.trace(Logging.LOADING, "Loaded client mod instance {} of type {}", getModId(), client_class_instance.getName());
        } catch (Throwable e) {
            // When a mod constructor throws an exception, it's wrapped in an InvocationTargetException which hides the
            // actual exception from the mod loading error screen.
            if (e instanceof InvocationTargetException wrapped)
                e = Objects.requireNonNullElse(wrapped.getCause(), e); // unwrap the exception

            FMLBMLLanguageProvider.LOGGER.error(Logging.LOADING,"Failed to create server mod instance. ModID: {}, class {}", getModId(), client_class_instance.getName(), e);
            throw new ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", e, client_class_instance);
        } finally {
            client_class_instance = null;
        }
    }

    private Method GetMethodFrom(String class_name , String name)
            throws NoSuchMethodException
    {
        for (Method m : Class.forName(layer , class_name).getMethods())
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
        mod_object.AddInstance(mod_instance);
        GetMethodFrom("com.github.mdcdi1315.basemodslib.BaseModsLib", "InitializeServerSideMod").invoke(null, mod_instance , event_bus);
    }

    private void InitializeToBaseModsLibrary_Client(Object mod_instance)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        mod_object.AddInstance(mod_instance);
        GetMethodFrom("com.github.mdcdi1315.basemodslib.BaseModsLibClient", "InitializeClientSideMod").invoke(null, mod_instance , event_bus);
    }
}
