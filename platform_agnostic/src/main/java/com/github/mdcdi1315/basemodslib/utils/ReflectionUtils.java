package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.*;
import java.lang.reflect.*;

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

    // Newer ImplementsInterface implementation by making recursive calls.
    // Might execute faster since we do not use a set.
    private static boolean ImplementsInterfaceInternal(Class<?> class_to_search, Class<?> interface_impl)
    {
        do {
            for (Class<?> i : class_to_search.getInterfaces())
            {
                if (interface_impl.equals(i)) { return true; } // Found a match, return true
                if (ImplementsInterfaceInternal(i, interface_impl)) { return true; } // Found a match from recursion, return true
            }
        } while ((class_to_search = class_to_search.getSuperclass()) != null);
        // No interfaces found, or none of the interfaces specified is not of type interface_impl.
        return false;
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
        ArgumentNullException.ThrowIfNull(class_to_search, "class_to_search");
        ArgumentNullException.ThrowIfNull(interface_class_to_check, "interface_class_to_check");
        return ImplementsInterfaceInternal(class_to_search, interface_class_to_check);
    }

    private record SuperClassIterable(Class<?> root)
        implements Iterable<Class<?>>
    {
        private static final class SuperClassIterator
                implements Iterator<Class<?>>
        {
            private Class<?> current;

            public SuperClassIterator(Class<?> en) { current = en; }

            @Override
            public boolean hasNext()
            {
                if (current == null) {
                    return false;
                } else {
                    Class<?> next = current.getSuperclass();
                    if (next == null) {
                        current = null;
                        return false;
                    } else {
                        current = next;
                        return true;
                    }
                }
            }

            @Override
            public Class<?> next() { return current; }
        }

        @Override
        public Iterator<Class<?>> iterator() { return new SuperClassIterator(root); }
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
        return new SuperClassIterable(class_to_search);
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

    /**
     * Invokes the specified public method and returns it's result, if applicable.
     * @param class_providing_method The {@link Class} object to look up for the specified method.
     * @param name The name of the method which is to be invoked. Must not be {@code null} or the empty string.
     * @param instance The instance of the object (If invoking an instance method), or {@code null} if the method to be invoked is a static method.
     * @param arguments The method's arguments to pass once the method is actually invoked.
     * @return The return value of the method, if it has one. If the return type of the method is {@code void}, it returns {@code null}.
     * @throws InvocationTargetException The invoked method has thrown an exception. Get the exception that was thrown by using the {@link InvocationTargetException#getCause()} method.
     * @throws IllegalAccessException If the {@link Method} object associated with {@code name} is enforcing Java language access control and the underlying method is inaccessible.
     * @throws NoSuchMethodException The method with name {@code name} was not found.
     * @throws ArgumentNullException {@code class_providing_method} and/or {@code name} are {@code null}.
     * @since 1.0.11
     */
    @MaybeNull
    public static Object InvokeMethod(Class<?> class_providing_method, String name, @MaybeNull Object instance, Object... arguments)
            throws InvocationTargetException, IllegalAccessException , NoSuchMethodException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNullOrEmpty(name, "name");
        ArgumentNullException.ThrowIfNull(class_providing_method, "class_providing_method");
        for (Method m : class_providing_method.getMethods())
        {
            if (name.equals(m.getName())) {
                return m.invoke(instance , arguments);
            }
        }
        throw new NoSuchMethodException(String.format("A public method named as '%s' was not found in class named as '%s'." , name , class_providing_method.getName()));
    }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code long}, or it's box type {@link Long}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Long}.
     * @since 1.0.26
     */
    public static boolean IsLong(Class<?> cls) { return cls == Long.class || cls == long.class; }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code byte}, or it's box type {@link Byte}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Byte}.
     * @since 1.0.26
     */
    public static boolean IsByte(Class<?> cls) { return cls == Byte.class || cls == byte.class; }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code short}, or it's box type {@link Short}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Short}.
     * @since 1.0.26
     */
    public static boolean IsShort(Class<?> cls) { return cls == Short.class || cls == short.class; }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code float}, or it's box type {@link Float}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Float}.
     * @since 1.0.26
     */
    public static boolean IsFloat(Class<?> cls) { return cls == Float.class || cls == float.class; }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code int}, or it's box type {@link Float}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Float}.
     * @since 1.0.26
     */
    public static boolean IsInteger(Class<?> cls) { return cls == Integer.class || cls == int.class; }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code double}, or it's box type {@link Double}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Double}.
     * @since 1.0.26
     */
    public static boolean IsDouble(Class<?> cls) { return cls == Double.class || cls == double.class; }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code boolean}, or it's box type {@link Boolean}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Boolean}.
     * @since 1.0.26
     */
    public static boolean IsBoolean(Class<?> cls) { return cls == Boolean.class || cls == boolean.class; }

    /**
     * Finds out whether a {@link Class} object is the numeric {@code char}, or it's box type {@link Character}.
     * @param cls The {@link Class} object to test.
     * @return A value whether {@code cls} is a class of type {@link Character}.
     * @since 1.0.26
     */
    public static boolean IsChar(Class<?> cls) { return cls == Character.class || cls == char.class; }

}
