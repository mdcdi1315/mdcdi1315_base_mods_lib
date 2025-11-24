package com.github.mdcdi1315.basemodslib.mods.proxy;

/**
 * Exception type thrown when a suitable class constructor could not be found for the specified proxy class and the passed arguments.
 */
public final class ProxyClassConstructorNotFoundException
    extends ProxyReflectionException
{
    /**
     * Initializes a new instance of the {@link ProxyClassConstructorNotFoundException} class, providing the name of the class that constructor resolving was failed.
     * @param class_name The name of the proxy class.
     */
    public ProxyClassConstructorNotFoundException(String class_name)
    {
        super(String.format(
                "A suitable constructor for constructing proxy object of type '%s' with the specified arguments could not be found. \n" +
                "Try to better specify your input arguments, and see whether that class declares public constructors.",
                class_name
        ));
    }
}

