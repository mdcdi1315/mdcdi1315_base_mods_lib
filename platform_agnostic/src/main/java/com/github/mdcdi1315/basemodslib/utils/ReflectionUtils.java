package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.*;

/**
 * Several reflection utilities required throughout the library. Mods depending on the library can use these as well.
 * @since 1.0.9
 */
public final class ReflectionUtils
{
    // Do not let anyone instantiate this class.
    private ReflectionUtils() {}

    /**
     * Gets ALL the implemented interfaces for the given class type. <br />
     * If the specified class does not implement any interface, this will return an empty iterable object.
     * @param class_to_search The class to search for it's implemented interfaces.
     * @return The interfaces that are implemented by {@code class_to_search} directly, or indirectly.
     * @throws ArgumentNullException {@code class_to_search} is {@code null}.
     */
    @NotNull
    public static Iterable<Class<?>> GetAllImplementedInterfaces(Class<?> class_to_search)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(class_to_search, "class_to_search");
        Set<Class<?>> set = new HashSet<>(8); // Typically, classes do not implement more than 8 distinct interfaces.
        Class<?> cls_current = class_to_search;
        do {
            set.addAll(Arrays.asList(cls_current.getInterfaces()));
        } while ((cls_current = cls_current.getSuperclass()) != null);
        // Class inheritance chain finished, but we have not found ALL the interfaces that are implemented...
        // To do that we will run a check in the built set for additional interfaces to return.
        Class<?> additional;
        List<Class<?>> classes_additional;
        Deque<Class<?>> classes = new ArrayDeque<>(set);
        while ((additional = classes.pollFirst()) != null) { // While not all the elements in the queue are processed...
            classes_additional = Arrays.asList(additional.getInterfaces());
            classes.addAll(classes_additional);
            set.addAll(classes_additional);
        }
        return set; // Phew! we have possibly scanned EVERYTHING we could scan. Return our results.
    }

    /**
     * Gets a value whether the specified class implements the specified interface.
     * @param class_to_search The class to search for it's implemented interfaces.
     * @param interface_class_to_check The interface to see whether it is implemented.
     * @return A value whether {@code class_to_search} implements or not the {@code interface_class_to_check}.
     * @throws ArgumentNullException {@code class_to_search} and/or {@code interface_class_to_check} are {@code null}.
     */
    @NotNull
    public static boolean ImplementsInterface(Class<?> class_to_search, Class<?> interface_class_to_check)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(interface_class_to_check, "interface_class_to_check");
        for (Class<?> implemented : GetAllImplementedInterfaces(class_to_search)) {
            if (interface_class_to_check.equals(implemented)) {
                return true; // Found a match
            }
        }
        // No interfaces found, or none of the interfaces specified is not of type interface_class_to_check.
        return false;
    }

    /**
     * Gets ALL the super classes extended by the current class object.
     * @param class_to_search The class object to search for it's super classes.
     * @return The super classes that extended by {@code class_to_search} directly, or indirectly.
     * @throws ArgumentNullException {@code class_to_search} is {@code null}.
     */
    @NotNull
    public static Iterable<Class<?>> GetAllSuperClasses(Class<?> class_to_search)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(class_to_search, "class_to_search");
        Set<Class<?>> set = new HashSet<>();
        Class<?> cls_current = class_to_search.getSuperclass();
        while (cls_current != null) {
            set.add(cls_current);
            cls_current = cls_current.getSuperclass();
        }
        return set;
    }

    /**
     * Gets a value whether the class object specified in {@code class_to_search} extends the class specified in {@code super_class} parameter.
     * @param class_to_search The class object to search for it's super classes.
     * @param super_class The class to see whether it extends from the {@code class_to_search} class object.
     * @return A value whether {@code class_to_search} extends or not the {@code super_class}.
     * @throws ArgumentNullException {@code class_to_search} and/or {@code super_class} are {@code null}.
     */
    @NotNull
    public static boolean ExtendsClass(Class<?> class_to_search, Class<?> super_class)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(super_class, "super_class");
        for (Class<?> extended : GetAllSuperClasses(class_to_search)) {
            if (super_class.equals(extended)) {
                return true; // Found a match
            }
        }
        // No super classes found, or none of the super classes specified is not of type super_class.
        return false;
    }
}
