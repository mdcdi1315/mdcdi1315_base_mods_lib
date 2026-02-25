package com.github.mdcdi1315.basemodslib.mods.proxy;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.IDisposable;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.CommonModLoaderBranding;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides the proxy objects manager, a service that can create proxy objects for consuming them in mod loader cross-operating scenarios.
 */
public final class ProxyManager
    implements IDisposable, ISynchronized
{
    private volatile boolean finalized;
    private Map<String , Class<? extends IProxyable>> registrations;

    /**
     * Creates a new and empty instance of the {@link ProxyManager} class.
     */
    public ProxyManager() {
        finalized = false;
        registrations = new ConcurrentHashMap<>();
    }

    /**
     * Registers a new proxy object to be created at a later time. <br />
     * If a proxy object with the provided ID is already registered, it is replaced with the currently provided one.
     * @param platform The mod loader platform to use this object. If the platform does not match, the proxy object will not be registered.
     * @param class_name The name of the proxy class. Must be a Java full class name, as described in {@link Class#forName(String)} method.
     * @param id A unique ID for your proxy registration. This will be later used to create your object.
     * @throws ArgumentNullException {@code platform} and/or {@code class_name} and/or {@code id} are {@code null}.
     * @throws ProxyImplementationNotFoundException The proxy class specified by {@code class_name} does not exist.
     * @throws InvalidOperationException For improved safety, once mod loading is complete, no additional proxy objects can be added.
     */
    public void RegisterProxyObject(CommonModLoaderBranding platform, String class_name, String id)
            throws ArgumentNullException, InvalidOperationException, ProxyImplementationNotFoundException
    {
        ArgumentNullException.ThrowIfNull(id, "id");
        ArgumentNullException.ThrowIfNull(platform , "platform");
        ArgumentNullException.ThrowIfNull(class_name, "class_name");
        if (finalized) {
            throw new InvalidOperationException("Cannot register additional proxy objects once mod loading is complete.");
        }
        try {
            if (CommonModLoaderBranding.IsModLoader(platform)) {
                registrations.put(id , ValidateProxyImplementation(Class.forName(class_name)));
            }
        } catch (ClassNotFoundException e) {
            throw new ProxyImplementationNotFoundException(String.format("Cannot register class named as %s because it does not exist." , class_name));
        }
    }

    /**
     * Creates a new proxy object of the specified type, and returns it.
     * @param id The ID of previously registered proxy object to use.
     * @param arguments The arguments to pass to the constructor of the proxy object.
     * @return The proxy object, cast to the abstract type.
     * @param <T> The type of the object to return the proxy object through.
     * @throws ArgumentNullException {@code id} is {@code null}.
     * @throws ProxyImplementationNotFoundException A proxy for the current mod loader has not been registered.
     * @throws ProxyClassConstructorNotFoundException A constructor matching the specified arguments passed could not be found.
     * @throws ProxyInstantiationException The proxy object could not be instantiated due to an exception in the constructor itself.
     * @throws ProxyReflectionException The proxy object could not be instantiated due to a Java reflection exception.
     */
    @SuppressWarnings("unchecked")
    public <T extends IProxyable> T CreateProxyObject(String id, Object... arguments)
            throws ArgumentNullException,
            ProxyImplementationNotFoundException,
            ProxyReflectionException
    {
        ArgumentNullException.ThrowIfNull(id , "id");
        Class<? extends IProxyable> cls = registrations.get(id);
        if (cls == null) {
            throw new ProxyImplementationNotFoundException(String.format(
                    "Could not find registered proxy object with ID %s! \nCheck whether in this mod loader you have registered appropriately a proxy class.",
                    id
            ));
        }
        Constructor<?> ctor = ResolveConstructorByArgs(cls, arguments == null ? new Object[0] : arguments);
        try {
            return (T) ctor.newInstance(arguments);
        } catch (ReflectiveOperationException e) {
            if (e instanceof InvocationTargetException ite) {
                throw new ProxyInstantiationException(cls.getName() , (Exception) Objects.requireNonNullElse(ite.getCause() , ite));
            } else {
                throw new ProxyReflectionException(cls.getName(), e);
            }
        }
    }

    private static <T extends IProxyable> Constructor<?> ResolveConstructorByArgs(Class<T> proxy_class, Object[] arguments)
    {
        List<Constructor<?>> ctors = new ArrayList<>(10);
        for (Constructor<?> tc : proxy_class.getConstructors())
        {
            // First cleanup by the number of parameters passed. We have this through the arguments.
            if (tc.getParameterCount() == arguments.length) { ctors.add(tc); }
        }
        // Check that we are OK to continue, we might not have public constructors which in such case we are cooked!
        if (ctors.isEmpty()) {
            throw new ProxyClassConstructorNotFoundException(proxy_class.getName());
        }
        // Then, from those parameters that are not-null, resolve the best constructor.
        if (arguments.length == 0 || ctors.size() == 1) {
            // Special-case: We have a single constructor of the specified length, we can just use that directly, or
            // we have constructor with zero arguments, that is always only one in each class (and we will have that, possibly).
            return ctors.get(0);
        } else {
            // We have multiple constructors with same arity but different parameters due to overloading.
            // We will match against that constructor that is the best for us to use.
            // It is possible some parameters to be null by that constructor definition.
            int max_index = DisambiguateArguments(arguments, ctors);
            if (max_index == -1) {
                // Disambiguation did not help, possibly we are into a case where all args are null, or not matching at all!
                throw new ProxyClassConstructorNotFoundException(proxy_class.getName());
            } else {
                return ctors.get(max_index);
            }
        }
    }

    // Attempts to disambiguate the constructor to use by the given arguments in the list
    // It returns an index into that list, or -1 if this disambiguation did not help at all.
    // Note: It does not mean that this will be the correct constructor that the user may want to; they have been warned about this occurring ambiguation.
    private static int DisambiguateArguments(Object[] arguments, List<Constructor<?>> ctors)
    {
        Class<?>[] parameter_types;
        int max_index = -1, disambiguities, max = 0; // max: the best candidate with as fewer disambiguations as possible.
        for (int CT = 0; CT < ctors.size(); CT++)
        {
            Object arg;
            disambiguities = 0;
            parameter_types = ctors.get(CT).getParameterTypes();
            for (int I = 0; I < parameter_types.length; I++)
            {
                arg = arguments[I];
                if (arg != null && arg.getClass() == parameter_types[I]) {
                    // We have a possible match, let's use this!
                    disambiguities++;
                }
            }
            if (disambiguities > max) {
                max = disambiguities;
                max_index = CT;
            }
        }
        return max_index;
    }

    @StackTraceHidden
    @SuppressWarnings("unchecked")
    private Class<? extends IProxyable> ValidateProxyImplementation(Class<?> cls)
    {
        if (!ReflectionUtils.ImplementsInterface(cls , IProxyable.class)) {
            throw new ProxyImplementationNotFoundException(String.format("The class with name '%s' does not implement the IProxyable interface." , cls.getName()));
        }
        if (cls.isInterface() || cls.isPrimitive()) {
            throw new ProxyImplementationNotFoundException(String.format("The class with name '%s' is not a non-abstract class." , cls.getName()));
        }
        return (Class<? extends IProxyable>) cls; // We verified that above, we are OK!!!
    }

    /**
     * Locks this proxy manager, not allowing to further accept registrations. <br />
     * It additionally returns a value whether this proxy manager has any proxy objects, so that if not used, to be deallocated. <br />
     * This is called by the Base Mods Library!!!
     * @return A value whether it is useful to keep this proxy manager allocated.
     */
    @ApiStatus.Internal
    public boolean Lock() { finalized = true; return !registrations.isEmpty(); }

    /**
     * Disposes this proxy manager. This is called by the Base Mods Library!!!
     */
    @Override
    @ApiStatus.Internal
    public void Dispose() {
        registrations = null;
    }
}
